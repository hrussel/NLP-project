import model.Recipe;

import java.io.File;
import java.util.*;

public class Main {

    public static final String FOLDER_DEV_ANNOTATIONS = "data/DevSet-annotations/";
    public static final String FOLDER_TEST_ANNOTATIONS = "data/TestSet-annotations/";
    public static final String FOLDER_CHUNKED = "data/chunked/";
    public static final String FOLDER_FULL = "data/fulltext/";
    public static final String FOLDER_SEMI = "data/semitext/";

    public static final String FILE_DEV_LIST = "data/devset_list.txt";
    public static final String FILE_TEST_LIST = "data/testset_list.txt";

    public static final int AMISH_MEATLOAF_INDEX = 280;

    private List<Recipe> recipes;


    public Main() {
        recipes = new ArrayList<>();
        readAllRecipes();

        VerbSignatureModel verbSignatureModel = new VerbSignatureModel(recipes);
        verbSignatureModel.calculate();

        ConnectionPriorModel connectionPriorModel = new ConnectionPriorModel(recipes.get(0),verbSignatureModel);
        double probability = connectionPriorModel.calculate();
        System.out.println("P(C) of recipe 1 is "+probability);


        StringSpanModel stringSpanModel = new StringSpanModel(recipes);
       // stringSpanDistribution = stringSpanModel

        System.out.println("done.");
    }

    private void readAllRecipes() {
        File chunkedFolder = new File(FOLDER_CHUNKED);
        String chunkedFolderEnd = "-chunked";
        int chunkedFolderEndLenght = chunkedFolderEnd.length();
        File[] folders = chunkedFolder.listFiles();
        for (File subFolder : folders) {

            if (subFolder.isDirectory()) {
                String subFolderName = subFolder.getName();
                subFolderName = subFolderName.substring(0, subFolderName.length() - chunkedFolderEndLenght);
//                System.out.println(subFolderName);

                File[] recipeFiles = subFolder.listFiles();
                for (File recipeFile : recipeFiles) {
                    if (recipeFile.getName().contains(".txt")) {
                        RecipeReader recipeReader = new RecipeReader(subFolderName, recipeFile.getName());
                        Recipe recipe = recipeReader.read();
                        recipes.add(recipe);
                        //System.out.println(subFolderName+"/"+recipeFile.getName());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new Main();
    }
}
