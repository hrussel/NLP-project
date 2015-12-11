package model;

import util.StringMatcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by baris on 11/29/2015.
 * Recipe model
 */
public class Recipe {
    private List<String> locations;
    private List<Action> actions;
    private List<String> ingredients;
    private List<Connection> connections;
    private Map<StringSpan, Connection> connectionsGoingTo;

    public Recipe() {
        this.actions = new ArrayList<>();
        this.setIngredients(new ArrayList<>());
        this.setConnections(new ArrayList<>());
        connectionsGoingTo = new HashMap<>();
    }

    public void build() {
        matchIngredients();
        matchLocations();
        createImplicitArguments();
        buildIngredientConnections();
        buildSequentialConnections();
        update();
    }

    public void update() {
        updateArgumentTypes();
        updateActionTypes();
        buildVerbSignatures();
    }

    private void updateActionTypes() {
        for (Action action : actions) {
            action.setSemanticType(SemanticType.LOCATION);
            for (Argument argument : action.getArguments()) {
                if(argument.getSemanticType().equals(SemanticType.FOOD)) {
                    action.setSemanticType(SemanticType.FOOD);
                }
            }
        }
    }

    private void updateArgumentTypes() {
        for (Action action : actions) {
            for (Argument argument : action.getArguments()) {
                for (StringSpan stringSpan : argument.getWords()) {
                    Connection connection = getConnectionGoingTo(stringSpan);
                    if (connection != null) {
                        if(connection.getFromAction().getSemanticType()!=SemanticType.OTHER) {
                            argument.setSemanticType(connection.getFromAction().getSemanticType());
                        }
                        break;
                    }
                }
            }
        }
    }

    private void matchLocations() {
        locations = new ArrayList<>();
        File locationstxt = new File("data/locations.txt");
        try {
            Scanner sc = null;

            sc = new Scanner(locationstxt);
            while (sc.hasNextLine()) {
                String loc = sc.nextLine();
                locations.add(loc);

            }
            StringMatcher locationMatcher = new StringMatcher(locations);

            for (Action act : this.getActions()) {
                for (Argument argument : act.getArguments()) {

                    List<StringSpan> words = argument.getWords();
                    for (StringSpan str : words) {
                        if (locationMatcher.isMatching(str.getBaseWord())) {
                            argument.setSemanticType(SemanticType.LOCATION);
                            break;
                        }
                    }
                }

            }

        } catch (FileNotFoundException exception) {
            System.out.println("The file " + locationstxt.getPath() + " was not found.");
        }
    }

    private void matchIngredients() {
        StringMatcher ingredientMather = new StringMatcher(ingredients);
        for (Action action : this.getActions()) {
            for (Argument argument : action.getArguments()) {
                boolean found = false;

                List<StringSpan> argumentSpans = argument.getWords();
                for (StringSpan argumentSpan : argumentSpans) {
                    String word = argumentSpan.getBaseWord();
                    if (ingredientMather.isMatching(word)) {
                        argument.setSemanticType(SemanticType.FOOD);
                    }
                }
            }
        }
    }

    private void createImplicitArguments() {
        for (Action action : this.getActions()) {
            boolean dobjFound = false;
            boolean ppFound = false;
            for (Argument argument : action.getArguments()) {
                SyntacticType type = argument.getSyntacticType();
                if (type.equals(SyntacticType.DOBJ)) {
                    dobjFound = true;
                }
                if (type.equals(SyntacticType.PP)) {
                    ppFound = true;
                }
                if (dobjFound && ppFound) {
                    break;
                }
            }
            if (!dobjFound) {
                Argument implicitDobj = new Argument();
                implicitDobj.setSyntacticType(SyntacticType.DOBJ);
                implicitDobj.setSemanticType(SemanticType.OTHER);
                implicitDobj.getWords().add(new StringSpan());
                action.getArguments().add(implicitDobj);
            }
            if (!ppFound) {
                Argument implicitPP = new Argument();
                implicitPP.setSyntacticType(SyntacticType.PP);
                implicitPP.setSemanticType(SemanticType.OTHER);
                implicitPP.getWords().add(new StringSpan());
                action.getArguments().add(implicitPP);
            }
        }
    }

    private void buildVerbSignatures() {
        for (Action action : this.getActions()) {
            Set<SyntacticType> syntacticTypeSet = new HashSet<>();
            for (Argument argument : action.getArguments()) {
                if (argument.getSemanticType().equals(SemanticType.FOOD)) {
                    syntacticTypeSet.add(argument.getSyntacticType());
                }
            }

            boolean leaf = true;
            for (Connection connection : this.getConnections()) {
                if (connection.getToAction() == action && !connection.isFromIngredient()) {
                    leaf = false;
                    break;
                }
            }

            VerbSignature verbSignature = new VerbSignature(syntacticTypeSet, leaf);
            action.setSignature(verbSignature);
        }
    }

    public void buildIngredientConnections() {
        StringMatcher ingredientMatcher = new StringMatcher(ingredients);
        for (Action action : actions) {
            List<Argument> arguments = action.getArguments();
            for (Argument argument : arguments) {
                List<StringSpan> stringSpans = argument.getWords();
                for (StringSpan stringSpan : stringSpans) {
                    if(ingredientMatcher.isMatching(stringSpan.getBaseWord())) {
                        Action ingredientAction = new Action(-1);
                        Set<SyntacticType> syntacticTypes = new HashSet<>();
                        //TODO @Baris can foods be pp
                        syntacticTypes.add(SyntacticType.DOBJ);
                        ingredientAction.setSignature(new VerbSignature(syntacticTypes, true));
                        ingredientAction.setSemanticType(SemanticType.FOOD);
                        Connection connection = new Connection(ingredientAction, action, argument, stringSpan, true);

                        this.getConnections().add(connection);
                        connectionsGoingTo.put(stringSpan, connection);
                    }
                }
            }
        }
    }

    public void buildSequentialConnections() {
        for (int i = 0; i < actions.size() - 1; i++) {
            Action action1 = actions.get(i);
            Action action2 = actions.get(i + 1);
            for (Argument argument : action2.getArguments()) {
                boolean found = false;
                Connection lastConnection = null;
                for (StringSpan stringSpan : argument.getWords()) {
                    Connection existingConnection = this.getConnectionGoingTo(stringSpan);
                    if (existingConnection == null) {
                        if (lastConnection != null && !lastConnection.getFromAction().getSemanticType().equals(action1.getSemanticType())) {
                            continue;
                        }
                        Connection connection = new Connection(action1, action2, argument, stringSpan, false);
                        this.connections.add(connection);
                        connectionsGoingTo.put(stringSpan, connection);
                        found = true;
                        break;
                    } else {
                        lastConnection = existingConnection;
                    }
                }
                if (found) {
                    break;
                }
            }
        }
    }

    public Connection getConnectionGoingTo(StringSpan stringSpan) {
        if (connectionsGoingTo.containsKey(stringSpan)) {
            return connectionsGoingTo.get(stringSpan);
        }
        return null;
    }

    public void setConnectionGoingTo(StringSpan stringSpan, Connection connection) {
        connectionsGoingTo.values().remove(connection);
        connectionsGoingTo.put(stringSpan, connection);
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public void setConnections(List<Connection> connections) {
        this.connections = connections;
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

}
