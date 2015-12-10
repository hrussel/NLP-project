import model.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by baris on 12/9/2015.
 * Utiities class
 */
public class Util {

    public static final String [] MEASURES = {"boxes","box", "cups", "cup", "small", "medium", "large", "teaspoons", "teaspoon", "tablespoons",
            "tablespoon", "pinches", "pinch", "cans", "can", "ounces", "ounce", "pounds", "pound", "packages", "package",
            "packs", "pack", "quart", "half", "whole", "drops", "drop","stalks", "stalk", "slices", "slice",
            "bunches", "bunch", "cubes", "cube", "dashes", "dash", "cloves", "clove", "inch", "gallons", "gallon", "pints",
            "pint", "grams", "gram", "jars", "jar", "heads", "head", "squares", "square"};

    public static void printRecipe(Recipe recipe) {
        int tId = 1;
        int rId = 1;
        for (Action action : recipe.getActions()) {
            StringSpan predicate = action.getPredicate();
            predicate.setTid("T" + tId);
            System.out.println("T" + tId + " predicate " + predicate.getStart() + " " + predicate.getEnd() + " " + predicate.getWord());
            tId++;

            for (Argument argument : action.getArguments()) {
                for (StringSpan argumentSpan : argument.getWords()) {
                    argumentSpan.setTid("T" + tId);
                    SemanticType semanticType = argument.getSemanticType();
                    String semanticTypeString = "OBJECT";
                    if (semanticType != null) {
                        semanticTypeString = semanticType.toString();
                    }

                    System.out.println(argumentSpan.getTid() + " arg_" + semanticTypeString + " " + argumentSpan.getStart() + " " + argumentSpan.getEnd() + " " + argumentSpan.getWord());
                    System.out.println("R" + rId + " " + semanticTypeString + " Arg1:" + predicate.getTid() + " Arg2:" + argumentSpan.getTid());
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

        for (Connection connection : recipe.getConnections()) {
            System.out.println("Connection from " + connection.getFromAction().getIndex() + " to " + connection.getToAction().getIndex() + " to string " + connection.getToStringSpan().getWord() + " connection type " + connection.getFromAction().getSemanticType() + " " + connection.getFromAction().getSignature().getSyntacticTypeSet().toString());
        }
    }

    public static String cleanIngredient(String s){
        s = s.toLowerCase();
        //If ends xith : don't add this as an ingredient
        if(s.contains(":"))
            return null;

        //end bracket
        while(s.contains("(") && s.contains(")")){
            int lp = s.indexOf("(");
            int rp = s.indexOf(")");
            String start = s.substring(0, lp).trim();
            String end = s.substring(rp+1, s.length()).trim();
            s = start + " "+ end;
        }

        //begin number (fraction ok)
        Pattern pattern = Pattern.compile("(\\d)+(/\\d)?[ \t]");
        Matcher matcher = pattern.matcher(s);
        if(matcher.find())
            s = s.substring(matcher.end()).trim();

        for (String measure : MEASURES) {

            pattern = Pattern.compile(measure+" ");
            matcher = pattern.matcher(s);
            if(matcher.find())
                s = s.substring(matcher.end()).trim();
        }

        //coma case
        if(s.contains(",")){
            s = s.substring(0, s.indexOf(",")).trim();
        }

        return s;
    }
}
