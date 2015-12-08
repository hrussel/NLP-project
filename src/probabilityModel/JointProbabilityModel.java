package probabilityModel;

/**
 * Created by baris on 12/8/2015.
 * This class calculates the join probability P(R,C) as in 3
 */
public class JointProbabilityModel {
    private final ConnectionPriorModel connectionPriorModel;
    private final RecipeModel recipeModel;

    public JointProbabilityModel(ConnectionPriorModel connectionPriorModel, RecipeModel recipeModel) {
        this.connectionPriorModel = connectionPriorModel;
        this.recipeModel = recipeModel;
    }

    public double calculate() {
        double probability = this.connectionPriorModel.calculate();
        if (probability == 0) {
            return probability;
        }
        probability *= this.recipeModel.calculate();
        return probability;
    }
}
