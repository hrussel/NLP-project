package probabilityModel;

import model.Action;
import model.Recipe;
import model.VerbSignature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baris on 12/5/2015.
 * This class produces the verb signature distribution given recipes
 */
public class VerbSignatureModel {

    private Map<String, Map<VerbSignature, Integer>> verbSignatureDistribution;
    private Map<String, Integer> totalCounts;
    private List<Recipe> recipes;

    public VerbSignatureModel(List<Recipe> recipes) {
        this.recipes = recipes;
        this.verbSignatureDistribution = new HashMap<>();
        this.totalCounts = new HashMap<>();
    }

    public Map<String, Map<VerbSignature, Integer>> calculate() {
        for (Recipe recipe : recipes) {
            for (Action action : recipe.getActions()) {
                String verb = action.getPredicate().getBaseWord();
                VerbSignature verbSignature = action.getSignature();
                if (verbSignatureDistribution.containsKey(verb)) {
                    Map<VerbSignature, Integer> signatureCounts = verbSignatureDistribution.get(verb);
                    if (signatureCounts.containsKey(verbSignature)) {
                        signatureCounts.put(verbSignature, signatureCounts.get(verbSignature) + 1);
                    } else {
                        signatureCounts.put(verbSignature, 1);
                    }
                    totalCounts.put(verb, totalCounts.get(verb) + 1);
                } else {
                    Map<VerbSignature, Integer> signatureCounts = new HashMap<>();
                    signatureCounts.put(verbSignature, 1);
                    verbSignatureDistribution.put(verb, signatureCounts);
                    totalCounts.put(verb, 1);
                }
            }
        }
        return this.verbSignatureDistribution;
    }

    public Map<String, Map<VerbSignature, Integer>> getVerbSignatureDistribution() {
        return verbSignatureDistribution;
    }

    public double getProbabilityOfAction(Action action) {
        String verb = action.getPredicate().getBaseWord();
        if (verbSignatureDistribution.containsKey(verb)) {
            VerbSignature signature = action.getSignature();
            Map<VerbSignature, Integer> signatureCounts = verbSignatureDistribution.get(verb);
            if (signatureCounts.containsKey(signature)) {
                int signatureCount = signatureCounts.get(signature);
                int totalCount = totalCounts.get(verb);
                return 1.0 * signatureCount / totalCount;
            } else {
                return 0;
            }
        } else {
            return 0;
        }
    }
}
