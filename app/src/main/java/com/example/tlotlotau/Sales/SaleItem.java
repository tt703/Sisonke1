package com.example.tlotlotau.Sales;

import com.example.tlotlotau.Inventory.Product;

public class SaleItem {
    public final Product product;
    public int quantity;

    public SaleItem(Product product, int quantity) {
        this.product = product;
        this.quantity = Math.max(0, quantity);
    }

    public double lineTotal() {
        return product.getProductPrice() * quantity;
    }
}
