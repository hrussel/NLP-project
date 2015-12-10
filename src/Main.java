import em.EM;
import em.LocalSearch;
import model.Argument;
import model.Recipe;
import probabilityModel.*;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        System.out.println("Reading all recipes");
        readAllRecipes();

        /*Recipe amishMeatloaf = recipes.get(AMISH_MEATLOAF_INDEX);

        VerbSignatureModel verbSignatureModel = new VerbSignatureModel(recipes);
        verbSignatureModel.calculate();

        ConnectionPriorModel connectionPriorModel = new ConnectionPriorModel(amishMeatloaf, verbSignatureModel);
        double connectionProbability = connectionPriorModel.calculate();
        System.out.println("P(C) of amish meatloaf is " + connectionProbability);

        PartCompositeModel partCompositeModel = new PartCompositeModel(recipes);
        RecipeModel recipeModel = new RecipeModel(amishMeatloaf, verbSignatureModel, partCompositeModel);
        double recipeProbability = recipeModel.calculate();
        System.out.println("P(R|C) of recipe amish meatloaf is " + recipeProbability);
        System.out.println("P(R,C) of recipe amish meatloaf is " + connectionProbability * recipeProbability);

        /*JointProbabilityModel jointProbabilityModel = new JointProbabilityModel(connectionPriorModel, recipeModel);

        LocalSearch localSearch = new LocalSearch(recipes,verbSignatureModel, partCompositeModel);
        localSearch.search();*/

       EM em = new EM(recipes);
       em.search();

        Recipe amishMeatloaf = recipes.get(AMISH_MEATLOAF_INDEX);
        Util.printRecipe(amishMeatloaf);

        /*String [] measures = Util.MEASURES;
        Pattern pattern = null;
        Matcher matcher = null;
        for (Recipe recipe : recipes) {
            for (String s : recipe.getIngredients()) {
                boolean print = false;
                String old = s;
                s = s.toLowerCase();
                //If ends xith : don't add this as an ingredient
                if(s.contains(":"))
                    continue;

                //end bracket
                while(s.contains("(") && s.contains(")")){
                    int lp = s.indexOf("(");
                    int rp = s.indexOf(")");
                    String start = s.substring(0, lp).trim();
                    String end = s.substring(rp+1, s.length()).trim();
                    s = start + " "+ end;
                }

                //begin number (fraction ok)
                pattern = Pattern.compile("(\\d)+(/\\d)?[ \t]");
                matcher = pattern.matcher(s);
                if(matcher.find())
                    s = s.substring(matcher.end()).trim();

                for (String measure : measures) {

                    pattern = Pattern.compile(measure+" ");
                    matcher = pattern.matcher(s);
                    if(matcher.find())
                        s = s.substring(matcher.end()).trim();
                    print = true;

                }

                //coma case
                if(s.contains(",")){
                    s = s.substring(0, s.indexOf(",")).trim();
                }

                if(print)
                    System.out.println(s);
                if(s.length() <= 1)
                    System.out.println("OLD:"+old);
            }
        }*/

        //System.out.println(testArgumentTypesModel());


        //System.out.println(partCompositeModel.toString());

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
