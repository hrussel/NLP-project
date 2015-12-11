package em;

import model.Recipe;
import probabilityModel.*;

import java.util.List;

/**
 * Created by baris on 12/9/2015.
 * This class is the main EM class as in 5.
 */
public class EM {

    private List<Recipe> recipes;
    private VerbSignatureModel verbSignatureModel;
    private PartCompositeModel partCompositeModel;
    private LocationModel locationModel;
    private LocalSearch localSearch;
    private int iteration;

    public EM(List<Recipe> recipes) {
        this.recipes = recipes;
        this.iteration = 0;
    }

    public void search() {
        initialize();
        boolean improved = true;
        while (improved) {
            improved = eStep();
            mStep();
        }
        System.out.println("E-Step: Done with iteration " + iteration);
    }

    private void initialize() {
        System.out.println("Initializing EM");
        this.verbSignatureModel = new VerbSignatureModel(recipes);
        verbSignatureModel.calculate();
        this.partCompositeModel = new PartCompositeModel(recipes);
        partCompositeModel.calculate();
        this.locationModel= new LocationModel(recipes);
        locationModel.calculateProbability();
        System.out.println("Initialized EM");
    }

    private boolean eStep() {
        this.localSearch = new LocalSearch(recipes, verbSignatureModel,partCompositeModel, locationModel);
        int swappedCount = this.localSearch.search();
        iteration++;
        System.out.println("E-Step: iteration " + iteration + " improved " + swappedCount + " connections.");
        return swappedCount > 0;
    }

    private void mStep() {
        for(Recipe recipe: recipes) {
            recipe.update();
        }
        this.verbSignatureModel = new VerbSignatureModel(recipes);
        verbSignatureModel.calculate();
        this.partCompositeModel = new PartCompositeModel(recipes);
        partCompositeModel.calculate();
        System.out.println("M-Step: iteration " + iteration + " updated.");
    }


}
