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
    private PartCompositeModel partCompositeModel;

    public RecipeModel(Recipe recipe, VerbSignatureModel verbSignatureModel, PartCompositeModel partCompositeModel) {
        this.recipe = recipe;
        this.verbSignatureModel = verbSignatureModel;
        this.partCompositeModel = partCompositeModel;
    }

    public double calculate() {
        double probability = 1.0;
        for (Action action : recipe.getActions()) {
            probability *= verbSignatureModel.getProbabilityOfAction(action);
            if(probability == 0) {
                return probability;
            }
            for (Argument argument : action.getArguments()) {
                probability *= calculateArgumentProbability(action, argument);
                if(probability == 0) {
                    return probability;
                }
            }
        }
        return probability;
    }

    private double calculateArgumentProbability(Action action, Argument argument) {
        ArgumentTypesModel argumentTypesModel = new ArgumentTypesModel(recipe, argument);
        double probability = argumentTypesModel.calculateProbability();
        if(probability == 0) {
            return probability;
        }
        if(argument.getSemanticType().equals(SemanticType.FOOD)) {

            for (StringSpan stringSpan : argument.getWords()) {

                List<Connection> stringSpanConnections = recipe.getConnectionsGoingTo(stringSpan);
                boolean ingredient = false;
                for (Connection stringSpanConnection : stringSpanConnections) {
                    //We don't look at the ingredients
                    if (stringSpanConnection.isFromIngredient()) {
                        ingredient = true;
                        break;
                    }
                }
                if (!ingredient && !stringSpan.getBaseWord().isEmpty()) {
                    probability *= partCompositeModel.getProbabilityOfString(stringSpan.getBaseWord(), action);
                    if (probability == 0) {
                        return probability;
                    }
                }
            }
        }
        return probability;
    }
}
