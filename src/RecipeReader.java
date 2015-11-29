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
                if(s.contains("SENTID: ")) {
                    String line = sc.nextLine();
                    //System.out.println(line);
                    line = line.substring(s.indexOf("SENT: ")+6);
                    //String sentence = sc.nextLine().substring()

                }

                if(s.contains("PRED: ")) {
                    String predicateString = s.substring(s.indexOf("PRED: ")+6);
                    StringSpan predicate = new StringSpan(predicateString,0,0);
                    currentAction = new Action();
                    currentAction.setPredicate(predicate);
                    recipe.getActions().add(currentAction);
                    System.out.println("T"+tId+ " predicate "+ predicate.getStart()+" "+predicate.getEnd()+" "+predicate.getWord());
                    tId++;
                }

                if(s.contains("DOBJ: ")) {
                    String argumentStringFull = s.substring(s.indexOf("DOBJ: ")+6);
                    String[] argumentStrings = argumentStringFull.split(",");
                    Argument argument = new Argument();
                    for(String argumentString : argumentStrings) {
                        argumentString = argumentString.trim();
                        StringSpan argumentSpan = new StringSpan(argumentString,0,0);
                        argument.getWords().add(argumentSpan);
                        //argument.setSemanticType();
                        argument.setSyntacticType(SyntacticType.DOBJ);
                        System.out.println("T"+tId+ " arg_object "+ argumentSpan.getStart()+" "+argumentSpan.getEnd()+" "+argumentSpan.getWord());
                        tId++;
                    }
                    currentAction.getArguments().add(argument);
                }

                if(s.contains("PARG: ")) {
                    String argumentStringFull = s.substring(s.indexOf("PARG: ")+6);
                    String[] argumentStrings = argumentStringFull.split(",");
                    Argument argument = new Argument();
                    for(String argumentString : argumentStrings) {
                        argumentString = argumentString.trim();
                        StringSpan argumentSpan = new StringSpan(argumentString,0,0);
                        argument.getWords().add(argumentSpan);
                        argument.setSemanticType(SemanticType.OTHER);
                        argument.setSyntacticType(SyntacticType.PP);
                        System.out.println("T"+tId+ " arg_"+argument.getSemanticType()+ " "+ argumentSpan.getStart()+" "+argumentSpan.getEnd()+" "+argumentSpan.getWord());
                        tId++;
                    }
                    currentAction.getArguments().add(argument);
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

        return recipe;
    }
}
