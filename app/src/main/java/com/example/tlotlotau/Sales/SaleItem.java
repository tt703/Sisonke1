package com.example.tlotlotau.Sales;

import com.example.tlotlotau.Inventory.Product;

public class SaleItem {
    public Product product;
    public int quantity;
    public int LocalId;
    public String CloudId;


    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = Math.max(0, quantity);
    }

    public SaleItem() {

    }

    public String getCloudId() {return CloudId;}
    public long getLocalId() {return LocalId;}



    public double lineTotal() {
        return product.getProductPrice() * quantity;
    }
}
