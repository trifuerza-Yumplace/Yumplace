package modelo;

public class RecipeIngredientResponse {

    private Ingredient ingredient;
    private String quantity;

    public Ingredient getIngredient() {
        return ingredient;
    }

    public String getQuantity() {
        return quantity;
    }

    public static class Ingredient {
        private int id;
        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}