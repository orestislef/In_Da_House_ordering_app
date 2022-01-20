package gr.indahouse.utils;

public class Categories {
    private String categoryId, categoryImageUrl, categoryName, categoryDesc;

    public Categories() {
    }

    public Categories(String categoryId, String categoryDesc, String categoryImageUrl, String categoryName) {
        this.categoryId = categoryId;
        this.categoryDesc = categoryDesc;
        this.categoryImageUrl = categoryImageUrl;
        this.categoryName = categoryName;
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
