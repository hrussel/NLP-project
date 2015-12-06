package probabilityModel;

import model.*;

import java.util.List;

/**
 * Created by baris on 12/6/2015.
 * This class calculates the probability P(R|C) as in 3.2
 */
public class RecipeModel {

    private Recipe recipe;
    private VerbSignatureModel verbSignatureModel;
    private StringSpanModel stringSpanModel;

    public RecipeModel(Recipe recipe, VerbSignatureModel verbSignatureModel, StringSpanModel stringSpanModel) {
        this.recipe = recipe;
        this.verbSignatureModel = verbSignatureModel;
        this.stringSpanModel = stringSpanModel;
    }

    public double calculate() {
        double probability = 1.0;
        for (Action action : recipe.getActions()) {
            probability *= verbSignatureModel.getProbabilityOfAction(action);
            if(probability == 0) {
                return probability;
            }
            for (Argument argument : action.getArguments()) {
                probability *= calculateArgumentProbability(argument);
                if(probability == 0) {
                    return probability;
                }
            }
        }
        return probability;
    }

    private double calculateArgumentProbability(Argument argument) {
        ArgumentTypesModel argumentTypesModel = new ArgumentTypesModel(recipe, argument);
        double probability = argumentTypesModel.calculateProbability();
        if(probability == 0) {
            return probability;
        }
        for (StringSpan stringSpan : argument.getWords()) {
            if(!stringSpan.getBaseWord().isEmpty()) {
                boolean hasOrigin = false;
                List<Connection> connections = recipe.getConnectionsGoingTo(stringSpan);
                if(!connections.isEmpty() && !connections.get(0).isFromIngredient())  {
                    hasOrigin = true;
                }
                probability *= stringSpanModel.getProbabilityOfString(stringSpan.getBaseWord(), argument.getSemanticType(), hasOrigin);
                if(probability == 0) {
                    return probability;
                }
            }
        }
        return probability;
    }
}
