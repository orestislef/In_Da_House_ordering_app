package gr.indahouse.utils;

public class Products {
    private String productId, productImageUrl, productName, productDesc, productPrice, productCategoryId;

    public Products() {
    }

    public Products(String productId, String productDesc, String productImageUrl, String productName, String productPrice, String productCategoryId) {
        this.productId = productId;
        this.productDesc = productDesc;
        this.productImageUrl = productImageUrl;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productCategoryId = productCategoryId;
    }

    public String getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(String productCategoryId) {
        this.productCategoryId = productCategoryId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice + "€";
    }
}
