package probabilityModel;

import model.*;

import java.util.List;
import java.util.Set;

/**
 * Created by Andy on 12/5/2015.
 */
public class ArgumentTypesModel {

    Argument arg;
    Recipe recipe;

    public ArgumentTypesModel(Recipe recipe, Argument arg) {
        this.recipe = recipe;
        this.arg = arg;
    }

    /* Arguments types probability
        For all strings in argument I J
        check whether all connections coming into them are of the same type (tsym tsem)
        iff true probability = 1
            else probability = 0
     */


    public double calculateProbability() {


        double totalprobability = 1.0;

        boolean firstPass = true;
        Connection previousConnection = null;
        for (StringSpan str : arg.getWords()) {

            Connection connection = recipe.getConnectionGoingTo(str);
           /* if (totalprobability == 0.0) {
                return totalprobability;
            }*/

            if (connection != null) {

                if (firstPass) {
                    previousConnection = connection;
                    firstPass = false;
                }

                SemanticType previousSemType = previousConnection.getFromAction().getSemanticType();
                Set<SyntacticType> previousSynType = previousConnection.getFromAction().getSignature().getSyntacticTypeSet();

                if (!(connection.getFromAction().getSemanticType().equals(previousSemType))){// || !(connection.getFromAction().getSignature().getSyntacticTypeSet().equals(previousSynType))) {

                    totalprobability *= 0.0;
                    recipe.increaseZeroCount();
                } else totalprobability *= 1.0;

                if(arg.isSemanticTypeFixed() && !connection.getFromAction().getSemanticType().equals(arg.getSemanticType())) {
                    if(arg.getSemanticType()==SemanticType.OTHER) {
                        totalprobability *= 0.0;
                    } else {
                        totalprobability *= 0.0;
                    }
                    recipe.increaseZeroCount();
                }
                previousConnection = connection;
            }


        }


        return totalprobability;
    }

}
