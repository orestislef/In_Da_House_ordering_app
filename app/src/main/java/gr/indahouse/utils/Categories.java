package gr.indahouse.utils;

public class Categories {
    private String categoryId, categoryImageUrl, categoryName, categoryDesc, categoryPosition;

    public Categories() {
    }

    public Categories(String categoryId, String categoryDesc, String categoryImageUrl, String categoryName, String categoryPosition) {
        this.categoryId = categoryId;
        this.categoryDesc = categoryDesc;
        this.categoryImageUrl = categoryImageUrl;
        this.categoryName = categoryName;
        this.categoryPosition = categoryPosition;
    }

    public String getCategoryPosition() {
        return categoryPosition;
    }

    public void setCategoryPosition(String categoryPosition) {
        this.categoryPosition = categoryPosition;
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

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
