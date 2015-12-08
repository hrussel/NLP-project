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

            List<Connection> connections = recipe.getConnectionsGoingTo(str);
            if (totalprobability == 0.0) {
                return totalprobability;
            }

            for (Connection con : connections) {

                if (firstPass) {
                    previousConnection = con;
                    firstPass = false;
                }

                SemanticType previousSemType = previousConnection.getFromAction().getSemanticType();
                Set<SyntacticType> previousSynType = previousConnection.getFromAction().getSignature().getSyntacticTypeSet();


                if (!(con.getFromAction().getSemanticType().equals(previousSemType))) {
                        //&& !(con.getFromAction().getSignature().getSyntacticTypeSet().equals(previousSynType))) {

                    totalprobability = 0.0;
                    break;
                } else totalprobability = 1.0;

                previousConnection = con;
            }

        }


        return totalprobability;
    }

}
