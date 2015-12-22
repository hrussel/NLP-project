package probabilityModel;

import model.*;
import util.StringMatcher;

import java.util.ArrayList;
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
    //private Map<String, Map<String,Double>> probabilities; //verb, location, probability

    public LocationModel(List<Recipe> recipes) {

        this.recipes = recipes;
        locationsDistribution = new HashMap<>();
        locationCounts = new HashMap<>();


    }

    public void calculateProbability() {


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
                                        if ((currentLocation.getBaseWord().equals(""))) //case it's not implicit
                                        {


                                            continue;

                                        } else //case implicit
                                        {
                                            if (locationsDistribution.containsKey(verb_i)) {
                                                Map<String, Integer> locationCounts = locationsDistribution.get(verb_i);
                                                if (locationCounts.containsKey(location)) {
                                                    locationCounts.put(location, locationCounts.get(location) + 1);
                                                } else {
                                                    locationCounts.put(location, 1);
                                                }
                                            } else {
                                                Map<String, Integer> locationCounts = new HashMap<>();
                                                locationCounts.put(location, 1);
                                                locationsDistribution.put(verb_i, locationCounts);

                                            }


                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }


    }

    public double returnProbability(Recipe rec, Action verb,Argument argument, StringSpan location) {
        double totalProbability = 0.0;
        if (!(location.getBaseWord().equals(""))) //case it's not implicit
        {
            List<StringSpan> fromLocation = new ArrayList<>();
            Connection con = rec.getConnectionGoingTo(location);
            if (con != null) {
                Action originAction = con.getFromAction();
                for (Argument originArgument : originAction.getArguments()) {

                    if (originArgument.getSemanticType().equals(SemanticType.LOCATION)) {
                        fromLocation = originArgument.getWords();
                        break;
                    }
                }
                List<String> stringList = new ArrayList<>();
                stringList.add(location.getBaseWord());
                StringMatcher stringMatcher = new StringMatcher(stringList);
                for (StringSpan loc : fromLocation) {

                    if (stringMatcher.isMatching(loc.getBaseWord())) {
                        totalProbability = 1.0;
                        argument.setSemanticType(SemanticType.LOCATION);
                        argument.setSemanticTypeFixed(true);

                    }
                }
            } else {
                return 1.0;
            }
        } else {
            try {
                if(locationsDistribution.containsKey(verb.getPredicate().getBaseWord())) {
                    Map<String,Integer> counts = locationsDistribution.get(verb.getPredicate().getBaseWord());
                    double totalVerbCount = counts.size();
                    if(totalVerbCount == 0) {
                        totalProbability = 0.0;
                        rec.increaseZeroCount();
                    } else {
                        Connection con = rec.getConnectionGoingTo(location);
                        if (con != null) {
                            Action originAction = con.getFromAction();

                            List<StringSpan> fromLocation = new ArrayList<>();
                            for (Argument originArgument : originAction.getArguments()) {
                                if (originArgument.getSemanticType().equals(SemanticType.LOCATION)) {
                                    fromLocation = originArgument.getWords();
                                    break;
                                }
                            }

                            for(StringSpan fromStringSpan : fromLocation) {
                                if(counts.containsKey(fromStringSpan.getBaseWord())) {
                                    double totalVerbLocationCount = counts.get(fromStringSpan.getBaseWord());
                                    totalProbability = totalVerbLocationCount / totalVerbCount;
                                    if(totalProbability==0) {
                                        rec.increaseZeroCount();
                                        totalProbability = 0.0000001;
                                    }
                                    return totalProbability;
                                }
                            }

                        } else {
                            return 1.0;
                        }
                    }
                } else {
                    totalProbability = 0.01;
                    rec.increaseZeroCount();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return totalProbability;
    }
}
