import model.*;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by baris on 11/29/2015.
 * Reads a recipe and constructs the model
 */
public class RecipeReader {

    public String filename;
    public static int tId = 1;

    public RecipeReader(String filename) {
        this.filename = filename;
    }

    public Recipe read() {
        Scanner sc = null;
        Recipe recipe = new Recipe();

        try {
            File file = new File(this.filename);
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
                    StringSpan predicate =  currentAction.getPredicate();
                    predicate.setWord(predicate.getWord() + " " + oargString);
                }


                // System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (sc != null) {
                sc.close();
            }
        }
        printRecipe(recipe);
        return recipe;
    }


    public void printRecipe(Recipe recipe) {
        tId=1;
        for(Action action : recipe.getActions()) {
            StringSpan predicate = action.getPredicate();
            System.out.println("T" + tId + " predicate " + predicate.getStart() + " " + predicate.getEnd() + " " + predicate.getWord());
            tId++;

            for(Argument argument : action.getArguments()) {
                for(StringSpan argumentSpan : argument.getWords()) {
                    SemanticType semanticType = argument.getSemanticType();
                    String semanticTypeString = "OBJECT";
                    if(semanticType!=null) {
                        semanticTypeString = semanticType.toString();
                    }
                    System.out.println("T" + tId + " arg_" + semanticTypeString + " " + argumentSpan.getStart() + " " + argumentSpan.getEnd() + " " + argumentSpan.getWord());
                    tId++;
                }
            }
        }
    }
}
