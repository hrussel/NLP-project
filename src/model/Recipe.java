package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baris on 11/29/2015.
 * Recipe model
 */
public class Recipe {

    private List<Action> actions;
    private List<String> ingredients;
    private List<Connection> connections;

    public Recipe() {
        this.actions = new ArrayList<>();
        this.setIngredients(new ArrayList<>());
        this.connections = new ArrayList<>();
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public void ingredientConnections(){
        for (String ingredient : ingredients) {
            for (Action action : actions) {
                List<Argument> arguments = action.getArguments();
                for (Argument argument : arguments) {
                    List<StringSpan> stringSpans = argument.getWords();
                    for (StringSpan stringSpan : stringSpans) {
                        if(ingredient.toLowerCase().contains(stringSpan.getWord().toLowerCase())){
                            Connection connection = new Connection(null, action, argument, stringSpan);
                            this.connections.add(connection);
                        }
                    }
                }
            }
        }
    }

    public void sequentialConnections(){
        for (int i = 0; i < actions.size()-1; i++) {
            Action action1 = actions.get(i);
            Action action2 = actions.get(i+1);
            Argument argument = action2.getArguments().get(0);
            StringSpan sp = argument.getWords().get(0);
            Connection connection = new Connection(action1, action2, argument, sp);
            this.connections.add(connection);

        }
    }
}
