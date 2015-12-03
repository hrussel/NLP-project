import model.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by baris on 11/29/2015.
 * Reads a recipe and constructs the model
 */
public class RecipeReader {

    public String folder;
    public String filename;
    public static int tId = 1;
    public static int rId = 1;
    public RecipeReader(String folder, String filename) {
        this.folder = folder;
        this.filename = filename;
    }

    public Recipe read() {
        Scanner sc = null;
        //Scanner scFullText = null;
        Recipe recipe = new Recipe();
        String sentence = "";
        int offset=0;
        try {
            //"data/chunked/BeefMeatLoaf-chunked/amish-meatloaf.txt"
            File file = new File("data/chunked/" + this.folder + "-chunked/" + this.filename + ".txt");
            //File fullTextFile = new File("data/fulltext/"+this.folder+"-fulltext/"+this.filename+".txt");
            sc = new Scanner(file);
            //scFullText = new Scanner(fullTextFile);

            Action currentAction = null;
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                if (s.contains("SENTID: ")) {
                    String line = sc.nextLine().trim();
                    //System.out.println(line);
                    line = line.substring(s.indexOf("SENT: ") + 6);


                   /* while(scFullText.hasNextLine()){
                        s = scFullText.nextLine();
                        s.replaceAll("(", "-LRB- ");
                        s.replaceAll(")", " -RRB-");
                        if(s.contains(line)){

                            sentence = line;
                        }
                    }*/
                }
                //TODO @Helena : add offset in StringSpans
                if (s.contains("PRED: ")) {
                    String predicateString = s.substring(s.indexOf("PRED: ") + 6);
                    int predIdx = offset+sentence.indexOf(predicateString);
                    StringSpan predicate = new StringSpan(predicateString, predIdx, predIdx+predicateString.length());
                    currentAction = new Action();
                    currentAction.setPredicate(predicate);
                    recipe.getActions().add(currentAction);
                }

                if (s.contains("DOBJ: ")) {
                    String argumentStringFull = s.substring(s.indexOf("DOBJ: ") + 6);
                    String[] argumentStrings = argumentStringFull.split(",");
                    Argument argument = new Argument();
                    for (String argumentString : argumentStrings) {

                        argumentString = argumentString.trim();

                        StringSpan argumentSpan = new StringSpan(argumentString, 0, 0);
                        argument.getWords().add(argumentSpan);
                        //argument.setSemanticType();
                        argument.setSyntacticType(SyntacticType.DOBJ);
                    }
                    currentAction.getArguments().add(argument);
                }

                if (s.contains("PARG: ")) {
                    String argumentString = s.substring(s.indexOf("PARG: ") + 6);
                    String prep = sc.nextLine();
                    prep = prep.substring(prep.indexOf("PREP: ") + 6);
                    Argument argument = new Argument();
                    StringSpan argumentSpan = new StringSpan(prep + " " + argumentString, 0, 0);
                    argument.getWords().add(argumentSpan);
                    argument.setSemanticType(SemanticType.OTHER);
                    argument.setSyntacticType(SyntacticType.PP);

                    currentAction.getArguments().add(argument);
                }

                if (s.contains("OARG: ")) {
                    String oargString = s.substring(s.indexOf("OARG: ") + 6);
                    StringSpan predicate = currentAction.getPredicate();
                    predicate.setWord(predicate.getWord() + " " + oargString);
                }

            }
            sc.close();
            file = new File("data/fulltext/" + this.folder + "-fulltext/" + this.filename + ".txt");
            sc = new Scanner(file);
            //TODO @Oz add connection
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                if (s.contains("Ingredients")) {
                    String currentIngredient = null;
                    sc.nextLine();
                    while (sc.hasNextLine()) {
                        s = sc.nextLine();
                        if (s.isEmpty()) {
                            recipe.getIngredients().add(currentIngredient);
                            //connection.setFrom(currentIngredient) connection.setTo(s)
                            currentIngredient = null;
                        } else {
                            if (s.contains("Data Parsed")) {
                                break;
                            }
                            if (currentIngredient == null) {
                                currentIngredient = s;
                            } else {
                                currentIngredient += " " + s;
                            }
                        }
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
        matchIngredients(recipe);
        createImplicitArguments(recipe);
        printRecipe(recipe);
        return recipe;
    }

    private void matchIngredients(Recipe recipe) {
        for (Action action : recipe.getActions()) {
            for (Argument argument : action.getArguments()) {
                boolean found = false;
                for (StringSpan argumentSpan : argument.getWords()) {
                    String word = argumentSpan.getWord();
                    for (String ingredient : recipe.getIngredients()) {
                        if (ingredient.contains(word)) {
                            argument.setSemanticType(SemanticType.FOOD);
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
                if (!found) {
                    argument.setSemanticType(SemanticType.OTHER);
                }
            }
        }
    }


    private void createImplicitArguments(Recipe recipe) {
        for (Action action : recipe.getActions()) {
            boolean dobjFound = false;
            boolean ppFound = false;
            for (Argument argument : action.getArguments()) {
                SyntacticType type = argument.getSyntacticType();
                if (type.equals(SyntacticType.DOBJ)) {
                    dobjFound = true;
                }
                if (type.equals(SyntacticType.PP)) {
                    ppFound = true;
                }
                if (dobjFound && ppFound) {
                    break;
                }
            }
            if (!dobjFound) {
                Argument implicitDobj = new Argument();
                implicitDobj.setSyntacticType(SyntacticType.DOBJ);
                implicitDobj.setSemanticType(SemanticType.OTHER);
                implicitDobj.getWords().add(new StringSpan());
                action.getArguments().add(implicitDobj);
            }
            if (!ppFound) {
                Argument implicitPP = new Argument();
                implicitPP.setSyntacticType(SyntacticType.PP);
                implicitPP.setSemanticType(SemanticType.OTHER);
                implicitPP.getWords().add(new StringSpan());
                action.getArguments().add(implicitPP);
            }
        }
    }


    public void printRecipe(Recipe recipe) {
        tId = 1;
        rId = 1;
        for (Action action : recipe.getActions()) {
            StringSpan predicate = action.getPredicate();
            predicate.setTid("T"+tId);
            System.out.println("T" + tId + " predicate " + predicate.getStart() + " " + predicate.getEnd() + " " + predicate.getWord());
            tId++;

            for (Argument argument : action.getArguments()) {
                for (StringSpan argumentSpan : argument.getWords()) {
                    argumentSpan.setTid("T"+tId);
                    SemanticType semanticType = argument.getSemanticType();
                    String semanticTypeString = "OBJECT";
                    if (semanticType != null) {
                        semanticTypeString = semanticType.toString();
                    }
                    if(argumentSpan.getStart() == -1){
                        System.out.println("WRONG!");
                    }
                    System.out.println(argumentSpan.getTid() + " arg_" + semanticTypeString + " " + argumentSpan.getStart() + " " + argumentSpan.getEnd() + " " + argumentSpan.getWord());
                    System.out.println("R" + rId + " "+ semanticTypeString + " Arg1:" + predicate.getTid()  + " Arg2:" + argumentSpan.getTid());
                    rId++;
                    tId++;
                }

            }
        }
        System.out.println("");
        System.out.println("Ingredients:");
        for (String ingredient : recipe.getIngredients()) {
            System.out.println(ingredient);
        }

    }
}
