package modelo;

import com.google.gson.annotations.SerializedName;

public class Category {
    @SerializedName("idCategory")
    private Integer idCategory;

    @SerializedName("categoryName")
    private String categoryName;

    public Category() {}

    public Integer getIdCategory() { return idCategory; }
    public void setIdCategory(Integer idCategory) { this.idCategory = idCategory; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() {
        return categoryName;
    }
}