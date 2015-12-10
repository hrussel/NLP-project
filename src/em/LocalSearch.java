package em;

import model.*;
import probabilityModel.*;

import java.util.List;
import java.util.Random;

/**
 * Created by baris on 12/6/2015.
 * This class does a local search as in 4
 */
public class LocalSearch {

    public static final int SEARCH_SIZE = 100;
    private final List<Recipe> recipes;
    private final VerbSignatureModel verbSignatureModel;
    private final LocationModel locationModel;
    private PartCompositeModel partCompositeModel;
    private Random random = new Random(42);
    private int swappedCount;

    public LocalSearch(List<Recipe> recipes, VerbSignatureModel verbSignatureModel, PartCompositeModel partCompositeModel, LocationModel locationModel) {
        this.recipes = recipes;
        this.locationModel=locationModel;
        this.verbSignatureModel = verbSignatureModel;
        this.partCompositeModel = partCompositeModel;
        this.swappedCount = 0;
    }

    public int search() {
        for (Recipe recipe : recipes) {
            searchForRecipe(recipe);
        }
        return swappedCount;
    }

    public void searchForRecipe(Recipe recipe) {
        ConnectionPriorModel connectionPriorModel = new ConnectionPriorModel(recipe, verbSignatureModel);
        RecipeModel recipeModel = new RecipeModel(recipe, verbSignatureModel, partCompositeModel);
        JointProbabilityModel jointProbabilityModel = new JointProbabilityModel(connectionPriorModel, recipeModel);

        boolean foundImprovement = true;
        while (foundImprovement) {
            foundImprovement = false;
            for (int i = 0; i < SEARCH_SIZE; i++) {
                boolean swapped = searchStep(recipe, jointProbabilityModel);
                if (swapped) {
                    foundImprovement = true;
                    swappedCount++;
                }
            }
        }
    }

    public boolean searchStep(Recipe recipe, JointProbabilityModel jointProbabilityModel) {
        double initialProbability = jointProbabilityModel.calculate();
        int index = random.nextInt(recipe.getActions().size());
        Action action1 = recipe.getActions().get(index);

        index = random.nextInt(recipe.getActions().size());
        Action action2 = recipe.getActions().get(index);

        index = random.nextInt(action1.getArguments().size());
        Argument argument1 = action1.getArguments().get(index);

        index = random.nextInt(action2.getArguments().size());
        Argument argument2 = action2.getArguments().get(index);

        index = random.nextInt(argument1.getWords().size());
        StringSpan stringSpan1 = argument1.getWords().get(index);

        index = random.nextInt(argument2.getWords().size());
        StringSpan stringSpan2 = argument2.getWords().get(index);

        boolean swapped = swapConnections(recipe, action1, argument1, stringSpan1, action2, argument2, stringSpan2);
        if (!swapped) {
            return false;
        }

        double nextProbability = jointProbabilityModel.calculate();
        if (nextProbability <= initialProbability) {
            swapConnections(recipe, action1, argument1, stringSpan1, action2, argument2, stringSpan2);
            return false;
        }
        return true;
    }

    public boolean swapConnections(Recipe recipe, Action action1, Argument argument1, StringSpan stringSpan1, Action action2, Argument argument2, StringSpan stringSpan2) {
        Connection connection1 = recipe.getConnectionGoingTo(stringSpan1);
        Connection connection2 = recipe.getConnectionGoingTo(stringSpan2);

        if (connection1 != null) {
            if (connection1.getFromAction().getIndex() < 0) {
                return false;
            }
        }

        if (connection2 != null) {
            if (connection2.getFromAction().getIndex() < 0) {
                return false;
            }
        }

        if (connection1 != null) {
            if (connection1.getFromAction().getIndex() >= action2.getIndex()) {
                return false;
            }
        }

        if (connection2 != null) {
            if (connection2.getFromAction().getIndex() >= action1.getIndex()) {
                return false;
            }
        }

        if (connection1 != null) {
            connection1.setToAction(action2);
            connection1.setToArgument(argument2);
            connection1.setToStringSpan(stringSpan2);
            recipe.setConnectionGoingTo(stringSpan2, connection1);
        }

        if (connection2 != null) {
            connection2.setToAction(action1);
            connection2.setToArgument(argument1);
            connection2.setToStringSpan(stringSpan1);
            recipe.setConnectionGoingTo(stringSpan1, connection2);
        }
        return true;
    }
}
