import model.*;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

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
        return this.read(false);
    }

    public Recipe read(boolean print) {
        Scanner sc = null;
        Scanner scSemiText = null;
        Recipe recipe = new Recipe();
        String sentence = "";
        String lastLine = "";
        int actionIndex = 0;
        int offset=-1;
        try {
            //"data/chunked/BeefMeatLoaf-chunked/amish-meatloaf.txt"
            File chunkedTextFile = new File(Main.FOLDER_CHUNKED + this.folder + "-chunked/" + this.filename);
            File semiTextFile = new File(Main.FOLDER_SEMI+folder+"-semi/"+filename);
            sc = new Scanner(chunkedTextFile);
            scSemiText = new Scanner(semiTextFile);

            Action currentAction = null;
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                lastLine = s;

                if (s.contains("SENTID: ")) {
                    String line = sc.nextLine().trim();
                    lastLine = line;
                    //System.out.println(line);
                    line = getSubString("SENT: ", line);
                    String semi_s = null;

                   while(scSemiText.hasNextLine()){
                       semi_s = scSemiText.nextLine();
                       semi_s = semi_s.toLowerCase();
                       semi_s = semi_s.replaceAll("[\\W]", "");

                       String tempLine = line.toLowerCase();
                       tempLine = tempLine.replaceAll("[\\W]", "");
                       offset=offset+sentence.length()+1;
                       //System.out.println("SEMI_S::"+semi_s);
                       //System.out.println("SENT::"+tempLine);

                       //I think we could use equals here, but you never know ^^
                       //Otherwise some libraries exist for comparing string similarities
                       if(semi_s.contains(tempLine) || tempLine.contains(semi_s)){
                           sentence = line.toLowerCase();
//                           sentence = sentence.replaceAll("-lrb- ", "(");
//                           sentence = sentence.replaceAll(" -rrb-", ")");
//                           sentence = sentence.replaceAll(" \\.",".");
//                           System.out.println("offset="+offset+" -"+sentence);
                           break;
                       }
                    }
                }
                if (s.contains("PRED: ")) {
                    String predicateString = getSubString("PRED: ",s);

                    int predIdx = offset+sentence.indexOf(predicateString);
                    StringSpan predicate = new StringSpan(predicateString, predIdx, predIdx+predicateString.length());
                    currentAction = new Action(actionIndex);
                    actionIndex++;
                    currentAction.setPredicate(predicate);
                    recipe.getActions().add(currentAction);

                }

                if (s.contains("DOBJ: ")) {
                    String argumentStringFull = getSubString("DOBJ: ",s);
                    String[] argumentStrings = argumentStringFull.split(",");
                    Argument argument = new Argument();
                    for (String argumentString : argumentStrings) {

                        argumentString = argumentString.trim();
                        int argIdx = offset+sentence.indexOf(argumentString);
                        StringSpan argumentSpan = new StringSpan(argumentString, argIdx, argIdx+argumentString.length());
                        argument.getWords().add(argumentSpan);
                        //argument.setSemanticType();
                        argument.setSyntacticType(SyntacticType.DOBJ);
                    }
                    currentAction.getArguments().add(argument);
                }

                if (s.contains("PARG: ")) {
                    String argumentString = getSubString("PARG: ",s);
                    String prep = sc.nextLine();
                    lastLine = prep;
                    prep = getSubString("PREP: ", prep);
                    Argument argument = new Argument();

                    String spanString = prep + " " + argumentString;
                    int argIdx = offset+sentence.indexOf(spanString);
//                    System.out.println("***ARG="+argumentString);
                    StringSpan argumentSpan = new StringSpan(spanString, argIdx, argIdx+spanString.length());
                    argument.getWords().add(argumentSpan);
                    argument.setSemanticType(SemanticType.OTHER);
                    argument.setSyntacticType(SyntacticType.PP);

                    currentAction.getArguments().add(argument);
                }

                if (s.contains("OARG: ")) {
                    String oargString = getSubString("OARG: ", s);
                    StringSpan predicate = currentAction.getPredicate();
                    predicate.addExtra(oargString);
                    int wordIdx = offset+sentence.indexOf(predicate.getBaseWord());
                    predicate.setStart(wordIdx);
                    predicate.setEnd(wordIdx+predicate.getBaseWord().length());
                }

            }
            sc.close();
            chunkedTextFile = new File("data/fulltext/" + this.folder + "-fulltext/" + this.filename);
            sc = new Scanner(chunkedTextFile);
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
        recipe.build();
        if(print) {
            printRecipe(recipe);
        }
        return recipe;
    }

    private String getSubString(String pattern, String string){
        String str=string;
        str = str.replaceAll(" ,", ",");
        str = str.replaceAll("-LRB- ", "(");
        str = str.replaceAll(" -RRB-", ")");
        str = str.replaceAll("-lrb- ", "(");
        str = str.replaceAll(" -rrb-", ")");
        str = str.replaceAll(" \\.",".");
        str = str.substring(str.indexOf(pattern) + 6).trim();
        return str;
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
