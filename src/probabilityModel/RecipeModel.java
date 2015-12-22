package probabilityModel;

import model.*;

/**
 * Created by baris on 12/6/2015.
 * This class calculates the probability P(R|C) as in 3.2
 */
public class RecipeModel {

    private Recipe recipe;
    private VerbSignatureModel verbSignatureModel;
    private PartCompositeModel partCompositeModel;
    private LocationModel locationModel;

    public RecipeModel(Recipe recipe, VerbSignatureModel verbSignatureModel, PartCompositeModel partCompositeModel, LocationModel locationModel) {
        this.recipe = recipe;
        this.verbSignatureModel = verbSignatureModel;
        this.partCompositeModel = partCompositeModel;
        this.locationModel = locationModel;
    }

    public double calculate() {
        double probability = 1.0;
        for (Action action : getRecipe().getActions()) {
            probability *= verbSignatureModel.getProbabilityOfAction(getRecipe(), action);
            for (Argument argument : action.getArguments()) {
                probability *= calculateArgumentProbability(action, argument);
            }
        }
        return probability;
    }

    private double calculateArgumentProbability(Action action, Argument argument) {
        ArgumentTypesModel argumentTypesModel = new ArgumentTypesModel(getRecipe(), argument);
        double probability = argumentTypesModel.calculateProbability();
        if (argument.getSemanticType().equals(SemanticType.FOOD)) {

            for (StringSpan stringSpan : argument.getWords()) {

                Connection stringSpanConnection = getRecipe().getConnectionGoingTo(stringSpan);
                boolean ingredient = false;
                //We don't look at the ingredients
                if (stringSpanConnection != null && stringSpanConnection.isFromIngredient()) {
                    ingredient = true;
                }
                if (!ingredient && !stringSpan.getBaseWord().isEmpty()) {
                    probability *= partCompositeModel.getProbabilityOfString(getRecipe(), stringSpan.getBaseWord(), action);
                }
            }
        } else if (argument.getSemanticType().equals(SemanticType.LOCATION)) {
            for (StringSpan stringSpan : argument.getWords()) {
                probability *= locationModel.returnProbability(getRecipe(), action, argument, stringSpan);
            }
        }
        return probability;
    }

    public Recipe getRecipe() {
        return recipe;
    }
}
