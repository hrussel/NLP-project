import model.Recipe;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Main {

    private static final String FOLDER_DEV_ANNOTATIONS = "data/DevSet-annotations/";
    private static final String FOLDER_TEST_ANNOTATIONS = "data/TestSet-annotations/";
    private static final String FOLDER_CHUNKED = "data/chunked/";
    private static final String FOLDER_FULL = "data/fulltext/";

    private static final String FILE_DEV_LIST = "data/devset_list.txt";
    private static final String FILE_TEST_LIST = "data/testset_list.txt";

    public static void main(String[] args) {
        RecipeReader reader = new RecipeReader("BeefMeatLoaf", "amish-meatloaf");
        Recipe recipe = reader.read();
        System.out.println("done.");
    }

    //TODO @Helena readAllRecipes()

}
