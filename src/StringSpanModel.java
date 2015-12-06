import model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Helena Russello on 05-12-15.
 */
public class StringSpanModel {

    private Map<String, Map<PartComposite, Integer>> partCompositeDistribution;
    private Map<String, Integer> totalCounts;
    private List<Recipe> recipes;

    public StringSpanModel(List<Recipe> recipes) {
        this.recipes = recipes;
        this.partCompositeDistribution = new HashMap<>();
        this.totalCounts = new HashMap<>();

        for (Recipe recipe : recipes) {
            System.out.print(".");
            for (Action action : recipe.getActions()) {
                for (Argument argument : action.getArguments()) {
                    SemanticType semanticType = argument.getSemanticType();
                    for (StringSpan stringSpan : argument.getWords()) {
                        Map<PartComposite, Integer> stringCounts = null;
                        List<Connection> stringSpanConnections = recipe.getConnectionsGoingTo(stringSpan);
                        boolean hasOrigin = false;
                        int count = 0;
                        
                        if(!stringSpanConnections.isEmpty()){
                            hasOrigin = true;
                            SemanticType semanticTypeConnection = stringSpanConnections.get(0).getSemanticType();
                            if(semanticTypeConnection != null)
                                semanticType = semanticTypeConnection;
                        }
                        PartComposite partComposite = new PartComposite(semanticType, hasOrigin);
                        String word = stringSpan.getWord();
                        if(partCompositeDistribution.containsKey(word)){
                            stringCounts = partCompositeDistribution.get(word);

                            
                            if(stringCounts.containsKey(partComposite)){
                                count = stringCounts.get(partComposite);
                            }
                            
                        }else{
                             stringCounts = new HashMap<>();
                        }
                        stringCounts.put(partComposite, ++count);
                        partCompositeDistribution.put(word, stringCounts);
                        incrementTotalCount(word);
                    }
                }
            }
            break;
        }
        System.out.println("");
    }

    private void incrementTotalCount(String string){
        int count = 0;
        if(totalCounts.containsKey(string)){
            count = totalCounts.get(string);
        }
        totalCounts.put(string, ++count);
    }

    public Map<String, Map<PartComposite, Integer>> getPartCompositeDistribution() {
        return partCompositeDistribution;
    }

    public double getProbabilityOfString(String string, SemanticType semanticType, boolean hasOrigin){
        if(!partCompositeDistribution.containsKey(string))
            return 0;
        Map<PartComposite, Integer> typeCounts = partCompositeDistribution.get(string);

        PartComposite partComposite = new PartComposite(semanticType, hasOrigin);
        if(!typeCounts.containsKey(partComposite)){
            System.out.println("StringSpanModel error ==> ask Helena");
            return 0;
        }
        int count = typeCounts.get(partComposite);

        return count / totalCounts.get(string);

    }

    @Override
    public String toString() {
        String string ="";
        for (String word : partCompositeDistribution.keySet()) {
            string+=word+"\t";
            Map<PartComposite, Integer> counts = partCompositeDistribution.get(word);
            for (PartComposite partComposite : counts.keySet()) {
                string+=partComposite.getSemanticType()+":";
                string+=counts.get(partComposite)+"\t";
            }
            string+="TOTAL:"+totalCounts.get(word);
            string+="\n";
        }
        return string;
    }
}
