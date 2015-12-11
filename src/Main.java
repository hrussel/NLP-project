import em.EM;
import model.*;
import probabilityModel.*;
import util.Parameters;
import util.RecipeReader;
import util.StringMatcher;
import util.Util;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

     private List<Recipe> recipes;


    private Recipe getAmishRecipe() {
        return recipes.get(Parameters.AMISH_MEATLOAF_INDEX);
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

        Map<String, Map<String, Integer>> verbSignatureMap = new HashMap<>();
        Map<String, Integer> predicateCounts = new HashMap<>();
        for (Recipe recipe : recipes) {
            for (Action action : recipe.getActions()) {

                String predicate = action.getPredicate().getBaseWord().toLowerCase().trim();

                String verbSignature = action.getSignature().toString();


                Map<String, Integer> predicateMap = null;
                int count = 0;
                int pCount = 0;
                if (!verbSignatureMap.containsKey(predicate)) {
                    predicateMap = new HashMap<String, Integer>();
                } else {
                    predicateMap = verbSignatureMap.get(predicate);
                    pCount = predicateCounts.get(predicate);
                    if(predicateMap.containsKey(verbSignature)){
                        count = predicateMap.get(verbSignature);

                    }

                }

                predicateMap.put(verbSignature, ++count);
                verbSignatureMap.put(predicate, predicateMap);
                predicateCounts.put(predicate, ++pCount);

            }
        }
        System.out.println("#########################################################################");
        System.out.println(verbSignatureMap.toString());

        Map<String, Map<String, Integer>> topVerbSignatures = new HashMap<>();

        Map<String, Integer> topPredicates = sortByValue1(predicateCounts);
        //TOP 10 predicates
        int iter = 0;
        for (String predicate : topPredicates.keySet()) {
            System.out.print(predicate);
            Map<String, Integer> vs = verbSignatureMap.get(predicate);
            int total = predicateCounts.get(predicate);
            int i=0;
            for (Map.Entry<String, Integer> stringIntegerEntry : vs.entrySet()) {
                if(i>0){
                    System.out.print("\t\t");
                }
                System.out.println("\t"+stringIntegerEntry.getKey()+"\t\t"+stringIntegerEntry.getValue()+"\t"+total);
                i++;
            }

            if(iter++ == 9)
                break;
        }

        List<String> locationList = new ArrayList<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader("data/locations.txt"));
            String line = null;
            while((line = br.readLine()) != null){
                locationList.add(line.toLowerCase().trim());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Location counts
        Map<String, Map<String, Integer>> locationCounts = new HashMap<>();
        Map<String, Integer> totalLocation = new HashMap<>();
        for (Recipe recipe : recipes) {
            for (Connection connection : recipe.getConnections()) {
                Action actionFrom = connection.getFromAction();
                if(actionFrom == null || actionFrom.getPredicate() == null ||  actionFrom.getPredicate().getBaseWord() == null)
                    continue;
                String predicate = actionFrom.getPredicate().getBaseWord().toLowerCase().trim();
                Action actionTo = connection.getToAction();
                if(!totalLocation.containsKey(predicate))
                    totalLocation.put(predicate, 0);
                for (Argument argument : actionTo.getArguments()) {


                        int totalCount = 0;

                        Map<String, Integer> locs = null;
                        if(!locationCounts.containsKey(predicate)){
                            locs = new HashMap<>();

                        }else{
                            locs = locationCounts.get(predicate);
                            totalCount = totalLocation.get(predicate);
                        }

                        for (StringSpan stringSpan : argument.getWords()) {
                            String spString = stringSpan.getBaseWord().toLowerCase().trim();
                            boolean foundLoc = false;
                            for (String loc : locationList) {
                                if(spString.contains(loc)) {
                                    spString = loc;
                                    foundLoc = true;
                                    break;
                                }
                            }
                            if(!foundLoc)
                                continue;
                            int count = 0;
                            if(locs.containsKey(spString))
                                count = locs.get(spString);
                            locs.put(spString, ++count);
                            totalLocation.put(predicate, ++totalCount);
                        }

                        locationCounts.put(predicate, locs);

                }
            }
        }

        System.out.println("#########################################################################");
        System.out.println(verbSignatureMap.toString());

        Map<String, Map<String, Integer>> topTenLocations = new HashMap<>();

        Map<String, Integer> sortedTopLocs = sortByValue1(totalLocation);
        //TOP 10 predicates
        iter = 0;
        for (String predicate : sortedTopLocs.keySet()) {

            if(!locationCounts.containsKey(predicate))
                continue;
            Map<String, Integer> vs = locationCounts.get(predicate);
            System.out.print(predicate);
            int total = totalLocation.get(predicate);
            int i=0;
            for (Map.Entry<String, Integer> stringIntegerEntry : vs.entrySet()) {
                if(i>0){
                    System.out.print("\t\t");
                }
                System.out.println("\t"+stringIntegerEntry.getKey()+"\t\t"+stringIntegerEntry.getValue()+"\t"+total);
                i++;
            }

            if(iter++ == 9)
                break;
        }

//        Recipe amishMeatloaf = recipes.get(Parameters.AMISH_MEATLOAF_INDEX);
//        Util.printRecipe(amishMeatloaf);

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
        File chunkedFolder = new File(Parameters.FOLDER_CHUNKED);
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

    /* Source : http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java */
    public Map<String, Integer> sortByValue( Map<String, Integer> map ) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>( map.entrySet() );
        Collections.sort( list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare( Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2 ) {
                int result = 0;
                Integer a = o1.getValue();
                Integer b = o2.getValue();
                return a - b;
            }
        } );

        Map<String, Integer> result = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            result.put( entry.getKey(), entry.getValue() );
        }
        return result;
    }

    Map sortByValue1(Map map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue())
                        .compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry)it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static void main(String[] args) {
        new Main();
    }
}
