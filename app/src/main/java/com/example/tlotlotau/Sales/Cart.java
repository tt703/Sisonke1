package com.example.tlotlotau.Sales;

import com.example.tlotlotau.Inventory.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Cart {
    private static Cart INSTANCE;
    private final List<SaleItem> items = new ArrayList<>();

    private Cart(){}

    public static synchronized Cart get() {
        if (INSTANCE == null) INSTANCE = new Cart();
        return INSTANCE;
    }

    private SaleItem findItemByProductId(int productId) {
        for (SaleItem it : items) {
            if (it.product.getProductId() == productId) return it;
        }
        return null;
    }

    public synchronized int getQuantityForProduct(int productId) {
        SaleItem it = findItemByProductId(productId);
        return it == null ? 0 : it.quantity;
    }


    public synchronized int addProduct(Product p, int quantity) {
        if (p == null || quantity <= 0) return 0;
        SaleItem existing = findItemByProductId(p.getProductId());
        if (existing != null) {
            existing.quantity += quantity;
            return existing.quantity;
        } else {
            SaleItem item = new SaleItem(p, quantity);
            items.add(item);
            return item.quantity;
        }
    }

    public synchronized void setQuantity(int productId, int qty) {
        SaleItem it = findItemByProductId(productId);
        if (it == null) return;
        if (qty <= 0) {
            items.remove(it);
        } else {
            it.quantity = qty;
        }
    }

    public synchronized void removeProduct(int productId) {
        SaleItem it = findItemByProductId(productId);
        if (it != null) items.remove(it);
    }

    public synchronized List<SaleItem> getItems() {
        return Collections.unmodifiableList(new ArrayList<>(items));
    }

    public synchronized boolean isEmpty() {
        return items.isEmpty();
    }

    public synchronized void clear() {
        items.clear();
    }

    public synchronized double subtotal() {
        double s = 0;
        for (SaleItem it : items) s += it.lineTotal();
        return s;
    }

    public synchronized double tax(double percent) {
        return subtotal() * percent / 100.0;
    }

    public synchronized double total(double percent) {
        return subtotal() + tax(percent);
    }
}
