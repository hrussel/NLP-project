package probabilityModel;

import model.Action;
import model.Recipe;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baris on 12/5/2015.
 * This class calculates the probability of P(C) as in 3.1
 */
public class ConnectionPriorModel {

    private Recipe recipe;
    private VerbSignatureModel verbSignatureModel;

    public ConnectionPriorModel(Recipe recipe, VerbSignatureModel verbSignatureModel) {
        this.recipe = recipe;
        this.verbSignatureModel = verbSignatureModel;
    }

    public double calculate() {
        double probability = 1.0;
        Map<Action, Double> previousProbabilities = new HashMap<>();
        for (Action action : recipe.getActions()) {
            probability *= calculateProbabilityOfDestinationSubset(action, previousProbabilities);
            previousProbabilities.put(action, probability);
        }
        return probability;
    }

    private double calculateProbabilityOfDestinationSubset(Action action, Map<Action, Double> previousProbabilities) {
        double probability = verbSignatureModel.getProbabilityOfAction(recipe, action);
        ConnectionOriginModel connectionOriginModel = new ConnectionOriginModel(recipe, action, previousProbabilities);
        probability *= connectionOriginModel.calculate();
        return probability;
    }
}
