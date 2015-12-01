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

    public RecipeReader(String folder, String filename) {
        this.folder = folder;
        this.filename = filename;
    }

    public Recipe read() {
        Scanner sc = null;
        Recipe recipe = new Recipe();

        try {
            //"data/chunked/BeefMeatLoaf-chunked/amish-meatloaf.txt"
            File file = new File("data/chunked/" + this.folder + "-chunked/" + this.filename + ".txt");
            sc = new Scanner(file);

            Action currentAction = null;
            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                if (s.contains("SENTID: ")) {
                    String line = sc.nextLine();
                    //System.out.println(line);
                    line = line.substring(s.indexOf("SENT: ") + 6);
                    //String sentence = sc.nextLine().substring()

                }

                if (s.contains("PRED: ")) {
                    String predicateString = s.substring(s.indexOf("PRED: ") + 6);
                    StringSpan predicate = new StringSpan(predicateString, 0, 0);
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

            while (sc.hasNextLine()) {
                String s = sc.nextLine();
                if (s.contains("Ingredients")) {
                    String currentIngredient = null;
                    sc.nextLine();
                    while (sc.hasNextLine()) {
                        s = sc.nextLine();
                        if (s.isEmpty()) {
                            recipe.getIngredients().add(currentIngredient);
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
        for (Action action : recipe.getActions()) {
            StringSpan predicate = action.getPredicate();
            System.out.println("T" + tId + " predicate " + predicate.getStart() + " " + predicate.getEnd() + " " + predicate.getWord());
            tId++;

            for (Argument argument : action.getArguments()) {
                for (StringSpan argumentSpan : argument.getWords()) {
                    SemanticType semanticType = argument.getSemanticType();
                    String semanticTypeString = "OBJECT";
                    if (semanticType != null) {
                        semanticTypeString = semanticType.toString();
                    }
                    System.out.println("T" + tId + " arg_" + semanticTypeString + " " + argumentSpan.getStart() + " " + argumentSpan.getEnd() + " " + argumentSpan.getWord());
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
