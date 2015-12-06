package probabilityModel;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by baris on 12/5/2015.
 * This class calculates the Connection Origin Model in 3.1.2
 */
public class ConnectionOriginModel {

    private final Recipe recipe;
    private final Action action;
    private final Map<Action, Double> previousProbabilities;

    public ConnectionOriginModel(Recipe recipe, Action action, Map<Action, Double> previousProbabilities) {
        this.recipe = recipe;
        this.action = action;
        this.previousProbabilities = previousProbabilities;
    }

    public double calculate() {
        double probability = 1.0;
        List<Connection> connections = new ArrayList<>();
        for (Argument argument : action.getArguments()) {
            for (StringSpan stringSpan : argument.getWords()) {
                connections.addAll(recipe.getConnectionsGoingTo(stringSpan));
            }
        }
        boolean foundConnection = false;
        for (Connection connection : connections) {
            Action fromAction = connection.getFromAction();
            if (!connection.isFromIngredient()) {
                foundConnection = true;
                probability *= previousProbabilities.get(fromAction);
            }
        }
        //TODO @Baris if an origin has been used in a previous connection much less likely to be used again.
        if (foundConnection && action.getSignature().isLeaf()) {
            return 0.0;
        }
        return 1.0;
    }
}
