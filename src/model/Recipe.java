package model;

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
    private Map<StringSpan, List<Connection>> connectionsGoingTo;

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
        buildVerbSignatures();
    }


    private void matchLocations()
    {
        locations = new ArrayList<>();
        File locationstxt = new File("data/locations.txt");
        try
        {
            Scanner sc = null;

            sc = new Scanner(locationstxt);
            while (sc.hasNextLine())
            {
                String loc = sc.nextLine();
                locations.add(loc);

            }


            for(Action act : this.getActions())
            {
                for (Argument argument : act.getArguments())
                {

                    List<StringSpan> wordz=argument.getWords();
                    for(StringSpan str : wordz)
                    {
                        for(String location :locations )
                        if(str.getBaseWord().matches(".*"+location+".*"))
                        {
                            argument.setSemanticType(SemanticType.LOCATION);
                        }
                    }
                }

            }

        }
        catch(FileNotFoundException exception){System.out.println("The file " + locationstxt.getPath() + " was not found.");}




    }


    private void matchIngredients() {
        for (Action action : this.getActions()) {
            for (Argument argument : action.getArguments()) {
                boolean found = false;

                List<StringSpan> argumentSpans = argument.getWords();
                for (StringSpan argumentSpan : argumentSpans) {
                    String word = argumentSpan.getWord();
                    for (String ingredient : this.getIngredients()) {
                        if (ingredient.contains(word)) {
                            argument.setSemanticType(SemanticType.FOOD);
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        break;
                    }
                }
                //TODO type of the srpingspan instead of the argument
                if (!found) {
                    argument.setSemanticType(SemanticType.OTHER);
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
                syntacticTypeSet.add(argument.getSyntacticType());
            }

            boolean leaf = true;
            for (Connection connection : this.getConnections()) {
                if (connection.getToAction() == action && connection.getFromAction() != null) {
                    leaf = false;
                    break;
                }
            }

            VerbSignature verbSignature = new VerbSignature(syntacticTypeSet, leaf);
            action.setSignature(verbSignature);
        }
    }

    public void buildIngredientConnections() {
        for (String ingredient : ingredients) {
            for (Action action : actions) {
                List<Argument> arguments = action.getArguments();
                for (Argument argument : arguments) {
                    List<StringSpan> stringSpans = argument.getWords();
                    for (StringSpan stringSpan : stringSpans) {
                        if (ingredient.toLowerCase().contains(stringSpan.getWord().toLowerCase())) {
                            Action ingredientAction = new Action();
                            Set<SyntacticType> syntacticTypes = new HashSet<>();
                            //TODO @Baris can foods be pp
                            syntacticTypes.add(SyntacticType.DOBJ);
                            ingredientAction.setSignature(new VerbSignature(syntacticTypes, true));
                            ingredientAction.setSemanticType(SemanticType.FOOD);
                            Connection connection = new Connection(ingredientAction, action, argument, stringSpan, true);

                            this.getConnections().add(connection);
                            if (!connectionsGoingTo.containsKey(stringSpan)) {
                                connectionsGoingTo.put(stringSpan, new ArrayList<>());
                            }
                            connectionsGoingTo.get(stringSpan).add(connection);
                        }
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
                for (StringSpan stringSpan : argument.getWords()) {
                    List<Connection> existingConnections = this.getConnectionsGoingTo(stringSpan);
                    if (existingConnections.isEmpty()) {
                        Connection connection = new Connection(action1, action2, argument, stringSpan, false);
                        this.connections.add(connection);
                        if (!connectionsGoingTo.containsKey(stringSpan)) {
                            connectionsGoingTo.put(stringSpan, new ArrayList<>());
                        }
                        connectionsGoingTo.get(stringSpan).add(connection);
                        found = true;
                        break;
                    }
                }
                if (found) {
                    break;
                }
            }
        }
    }

    public List<Connection> getConnectionsGoingTo(StringSpan stringSpan) {
        if (connectionsGoingTo.containsKey(stringSpan)) {
            return connectionsGoingTo.get(stringSpan);
        }
        return new ArrayList<>();
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
