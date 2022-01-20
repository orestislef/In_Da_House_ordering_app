package gr.indahouse.utils;

public class Categories {
    private String idOfCategory, categoryImageUrl, categoryName, categoryDesc;

    public Categories() {

    }

    public Categories(String idOfCategory, String categoryDesc, String categoryImageUrl, String categoryName) {
        this.idOfCategory = idOfCategory;
        this.categoryDesc = categoryDesc;
        this.categoryImageUrl = categoryImageUrl;
        this.categoryName = categoryName;
    }

    public String getIdOfCategory() {
        return idOfCategory;
    }

    public void setIdOfCategory(String idOfCategory) {
        this.idOfCategory = idOfCategory;
    }

    public String getCategoryImageUrl() {
        return categoryImageUrl;
    }

    public void setCategoryImageUrl(String categoryImageUrl) {
        this.categoryImageUrl = categoryImageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDesc() {
        return categoryDesc;
    }

    public void setCategoryDesc(String categoryDesc) {
        this.categoryDesc = categoryDesc;
    }
}
