import model.Action;
import model.Argument;
import model.Recipe;
import model.StringSpan;

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
            for (Argument argument : action.getArguments()) {
                probability *= calculateArgumentProbability(argument);
            }
        }
        return probability;
    }

    private double calculateArgumentProbability(Argument argument) {
        ArgumentTypesModel argumentTypesModel = new ArgumentTypesModel(recipe, argument);
        double probability = argumentTypesModel.calculateProbability();
        for(StringSpan stringSpan : argument.getWords()) {
            //probability*=stringSpanModel.getProbability(stringSpan.getBaseWord());
        }
        return probability;
    }
}
