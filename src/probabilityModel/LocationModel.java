package probabilityModel;

import model.*;
import util.StringMatcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Andy on 12/8/2015.
 */
public class LocationModel {

    private List<Recipe> recipes;
    private Map<String, Map<String, Integer>> locationsDistribution; //<Verb, <Location, Times>>
    private Map<String, Integer> locationCounts;

    public LocationModel(List<Recipe> recipes) {

        this.recipes = recipes;
        locationsDistribution = new HashMap<>();
        locationCounts = new HashMap<>();


    }

    public double calculateProbability() {
        double totalProbability = 0;
        //!attention Needs to be checked if location is retrieved properly

        for (Recipe recipe : recipes) {
            for (Action action : recipe.getActions()) {
                String verb_i = action.getPredicate().getBaseWord();
                for (Argument argument : action.getArguments()) {
                    if (argument.getSemanticType().equals(SemanticType.LOCATION)) {
                        for (StringSpan currentLocation : argument.getWords()) {
                            Connection stringSpanConnection = recipe.getConnectionGoingTo(currentLocation);
                            if (stringSpanConnection != null) {
                                Action fromAction = stringSpanConnection.getFromAction();

                                for (Argument arg2 : fromAction.getArguments()) {
                                    List<StringSpan> fromLocation = arg2.getWords();
                                    for (StringSpan str : fromLocation) {
                                        String location = str.getBaseWord();
                                        if (!(currentLocation.getBaseWord().equals(""))) //case it's not implicit
                                        {


                                            if (currentLocation.getBaseWord().matches(".*" + verb_i + ".*")) {
                                                totalProbability = 1;
                                            }
                                        } else //case implicit
                                        {
                                            if (locationsDistribution.containsKey(verb_i)) {
                                                Map<String, Integer> locationCounts = locationsDistribution.get(verb_i);
                                                if (locationCounts.containsKey(action.getPredicate().getBaseWord())) {
                                                    locationCounts.put(location, locationCounts.get(location) + 1);

                                                } else {
                                                    locationCounts.put(location, 1);
                                                }

                                            } else {
                                                Map<String, Integer> locationCounts = new HashMap<>();
                                                locationCounts.put(location, 1);
                                                locationsDistribution.put(verb_i, locationCounts);

                                            }

                                            /*TODO total probability =
                                            Times a verb appears with a location
                                            over the times the verb appears in general */
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }


        return totalProbability;
    }
}
