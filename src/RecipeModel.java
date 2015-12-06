import model.Action;
import model.Recipe;

/**
 * Created by baris on 12/6/2015.
 * This class calculates the probability P(R|C) as in 3.2
 */
public class RecipeModel {

    private Recipe recipe;
    private VerbSignatureModel verbSignatureModel;

    public RecipeModel(Recipe recipe, VerbSignatureModel verbSignatureModel) {

        this.recipe = recipe;
        this.verbSignatureModel = verbSignatureModel;
    }

    public double calculate() {
        double probability = 1.0;
        for(Action action : recipe.getActions()) {
            probability *= verbSignatureModel.getProbabilityOfAction(action) * calculateArgumentProbability();
        }
        return probability;
    }

    private double calculateArgumentProbability() {
        return 1.0;
    }
}
