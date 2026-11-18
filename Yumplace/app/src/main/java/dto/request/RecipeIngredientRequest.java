package dto.request;

public class RecipeIngredientRequest {

    private Integer ingredientId;
    private String quantity;

    public RecipeIngredientRequest(Integer ingredientId, String quantity) {
        this.ingredientId = ingredientId;
        this.quantity = quantity;
    }

    public Integer getIngredientId() {
        return ingredientId;
    }

    public String getQuantity() {
        return quantity;
    }
}
