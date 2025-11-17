package com.example.tlotlotau.Inventory;

public class Product {
    private int productId;
    private String productName;
    private double productPrice;
    private String productDescription;
    private int productQuantity;
    private String productDateCreated;
    private String getProductbyQRCode;
    private Long categoryCId;


    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductDateCreated() {
        return productDateCreated;
    }

    public void setProductDateCreated(String productDateCreated) {
        this.productDateCreated = productDateCreated;
    }

    public void setCategoryCId(Long id){ this.categoryCId = id; }
    public Long getCategoryCId(){ return categoryCId; }

    public void setProductQRCode(String productQRCode) {
        this.getProductbyQRCode = productQRCode;
    }

    public String getQrCodeByHelper() {
        return getProductbyQRCode;
    }
}