import model.*;

/**
 * Created by baris on 12/9/2015.
 * Utiities class
 */
public class Util {

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
}
