package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baris on 11/29/2015.
 * Recipe model
 */
public class Recipe {

    private List<Action> actions;

    public Recipe() {
        this.actions = new ArrayList<>();
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
