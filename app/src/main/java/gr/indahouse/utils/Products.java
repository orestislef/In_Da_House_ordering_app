package gr.indahouse.utils;

public class Products {
    private String idOfProduct,productImageUrl, productName, productDesc, productPrice;

    public Products() {
    }

    public Products(String idOfProduct, String productDesc, String productImageUrl, String productName, String productPrice) {
        this.idOfProduct = idOfProduct;
        this.productDesc = productDesc;
        this.productImageUrl = productImageUrl;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public String getIdOfProduct() {
        return idOfProduct;
    }

    public void setIdOfProduct(String idOfProduct) {
        this.idOfProduct = idOfProduct;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
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

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String idOfProduct) {
        this.productPrice = productPrice;
    }
}
