import model.Recipe;

public class Main {

    public static void main(String[] args) {
        RecipeReader reader = new RecipeReader("BeefMeatLoaf", "amish-meatloaf");
        Recipe recipe = reader.read();
        System.out.println("done.");
    }
}
