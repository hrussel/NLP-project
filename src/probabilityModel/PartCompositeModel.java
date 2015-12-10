package probabilityModel;

import model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Helena Russello on 05-12-15.
 * This is the part composite model as in 3.2.2
 */
public class PartCompositeModel {

    private Map<String, Map<String, Integer>> partCompositeDistribution;
    private Map<String, Integer> totalCounts;
    private List<Recipe> recipes;

    public PartCompositeModel(List<Recipe> recipes) {
        this.recipes = recipes;
        this.partCompositeDistribution = new HashMap<>();
        this.totalCounts = new HashMap<>();
    }

    public void calculate() {
        for (Recipe recipe : recipes) {
            //System.out.print(".");
            for (Action action : recipe.getActions()) {
                for (Argument argument : action.getArguments()) {
                    SemanticType semanticType = argument.getSemanticType();
                    //We only look at the arguments of type food
                    if (semanticType != SemanticType.FOOD)
                        continue;
                    for (StringSpan stringSpan : argument.getWords()) {
                        String word = stringSpan.getBaseWord();

                        //We dont want to look at implicit arguments
                        if (word.isEmpty()) {
                            continue;
                        }

                        Map<String, Integer> stringCounts = null;
                        if (partCompositeDistribution.containsKey(word)) {
                            stringCounts = partCompositeDistribution.get(word);
                        } else {
                            stringCounts = new HashMap<>();
                        }

                        Connection stringSpanConnection = recipe.getConnectionGoingTo(stringSpan);
                        if (stringSpanConnection != null) {
                            //We don't look at the ingredients
                            if (!stringSpanConnection.isFromIngredient()) {
                                for (Argument fromArgument : stringSpanConnection.getFromAction().getArguments()) {
                                    if (fromArgument.getSemanticType() != SemanticType.FOOD)
                                        continue;

                                    for (StringSpan fromSpan : fromArgument.getWords()) {
                                        int count = 0;
                                        String fromSpanWord = fromSpan.getBaseWord();
                                        if (stringCounts.containsKey(fromSpanWord)) {
                                            count = stringCounts.get(fromSpanWord);
                                        }
                                        stringCounts.put(fromSpanWord, ++count);
                                    }
                                }
                            }
                        }

                        partCompositeDistribution.put(word, stringCounts);
                        incrementTotalCount(word);


                    }
                }
            }
        }

    }

    private void incrementTotalCount(String string) {
        int count = 0;
        if (totalCounts.containsKey(string)) {
            count = totalCounts.get(string);
        }
        totalCounts.put(string, ++count);
    }

    public Map<String, Map<String, Integer>> getPartCompositeDistribution() {
        return partCompositeDistribution;
    }

    public double getProbabilityOfString(String string, Action origin) {
        int sumCounts = 0;
        int sumTotalCounts = 0;
        if (!partCompositeDistribution.containsKey(string)) {
            System.out.println("PartCompositeModel error ==> no key");
            return 0.0;
        }
        Map<String, Integer> stringCounts = partCompositeDistribution.get(string);

        int stringTotalCount = totalCounts.get(string);
        if (stringTotalCount == 0) {
            return 0.0;
        }

        for (Argument argument : origin.getArguments()) {
            for (StringSpan stringSpan : argument.getWords()) {
                String stringSpanWord = stringSpan.getWord();
                if (stringCounts.containsKey(stringSpanWord)) {
                    sumCounts += stringCounts.get(stringSpanWord);
                    sumTotalCounts += stringTotalCount;
                }
            }
        }

        if (sumTotalCounts == 0) {
            return 1.0;
        }

        return 1.0 * sumCounts / sumTotalCounts;

    }

    @Override
    public String toString() {
        String string = "";
        for (String word : partCompositeDistribution.keySet()) {
            string += word + "\t";
            Map<String, Integer> counts = partCompositeDistribution.get(word);
            for (String str : counts.keySet()) {
                string += str + ":";
                string += counts.get(str) + "\t";
            }
            string += "TOTAL:" + totalCounts.get(word);
            string += "\n";
        }
        return string;
    }
}
