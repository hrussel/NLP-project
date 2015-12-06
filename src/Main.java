import model.Argument;
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


    private Recipe getAmishRecipe() {
        return recipes.get(AMISH_MEATLOAF_INDEX);
    }

    private double testArgumentTypesModel() {
        double pResult;
        Recipe testrecipe = getAmishRecipe();
        Argument testarg = testrecipe.getActions().get(1).getArguments().get(0);
        ArgumentTypesModel testATM = new ArgumentTypesModel(testrecipe, testarg);
        pResult = testATM.calculateProbability();

        return pResult;
    }

    public Main() {
        recipes = new ArrayList<>();
        readAllRecipes();

        VerbSignatureModel verbSignatureModel = new VerbSignatureModel(recipes);
        verbSignatureModel.calculate();

        ConnectionPriorModel connectionPriorModel = new ConnectionPriorModel(recipes.get(AMISH_MEATLOAF_INDEX), verbSignatureModel);
        double connectionProbability = connectionPriorModel.calculate();
        System.out.println("P(C) of amish meatloaf is " + connectionProbability);

        StringSpanModel stringSpanModel = new StringSpanModel(recipes);
        RecipeModel recipeModel = new RecipeModel(recipes.get(AMISH_MEATLOAF_INDEX), verbSignatureModel, stringSpanModel);
        double recipeProbability = recipeModel.calculate();
        System.out.println("P(R|C) of recipe amish meatloaf is " + recipeProbability);
        System.out.println("P(R,C) of recipe amish meatloaf is " + connectionProbability * recipeProbability);
        //System.out.println(testArgumentTypesModel());


        //System.out.println(stringSpanModel.toString());

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
