import model.Recipe;

public class Main {

    public static void main(String[] args) {

        RecipeReader reader = new RecipeReader("data/chunked/BeefMeatLoaf-chunked/amish-meatloaf.txt");
        Recipe recipe = reader.read();
        System.out.println("done.");
    }
}
