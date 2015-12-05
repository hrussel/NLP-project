import model.Recipe;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Main {

    public static final String FOLDER_DEV_ANNOTATIONS = "data/DevSet-annotations/";
    public static final String FOLDER_TEST_ANNOTATIONS = "data/TestSet-annotations/";
    public static final String FOLDER_CHUNKED = "data/chunked/";
    public static final String FOLDER_FULL = "data/fulltext/";
    public static final String FOLDER_SEMI = "data/semitext/";

    public static final String FILE_DEV_LIST = "data/devset_list.txt";
    public static final String FILE_TEST_LIST = "data/testset_list.txt";

    private static List<Recipe> recipes;

    public static void main(String[] args) {
        recipes = new ArrayList<Recipe>();
        readAllRecipes();
        //RecipeReader reader = new RecipeReader("BeefMeatLoaf", "amish-meatloaf");
        //Recipe recipe = reader.read();
        System.out.println("done.");
    }

    //TODO @Helena readAllRecipes()
    private static void readAllRecipes(){
        File chunkedFolder = new File(FOLDER_CHUNKED);
        String chunkedFolderEnd = "-chunked";
        int chunkedFolderEndLenght = chunkedFolderEnd.length();
        File[] folders = chunkedFolder.listFiles();
        for (File subFolder : folders) {

            if(subFolder.isDirectory()){
                String subFolderName = subFolder.getName();
                subFolderName = subFolderName.substring(0, subFolderName.length()-chunkedFolderEndLenght);
//                System.out.println(subFolderName);

                File[] recipeFiles = subFolder.listFiles();
                for (File recipeFile : recipeFiles) {
                    if(recipeFile.getName().contains(".txt")){
                       RecipeReader recipeReader = new RecipeReader(subFolderName, recipeFile.getName());
                       Recipe recipe = recipeReader.read();
                       recipes.add(recipe);
                        //System.out.println(subFolderName+"/"+recipeFile.getName());
                    }

                }

            }

        }
        }

}
