package com.example.tlotlotau.Sales;

public class SaleRecord {
    private long id;
    private String userId;
    private String userName;
    private String userRole;
    private double subtotal;
    private double tax;
    private double total;
    private String paymentMethod;
    private String timestamp;
    private int localId;
    private String cloudId;
    private long localSaleId;





    // getters/setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserRole() { return userRole; }
    public void setUserRole(String userRole) { this.userRole = userRole; }
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    public int getLocalId() { return localId; }
    public void setLocalId(int localId) { this.localId = localId; }
    public long getLocalSaleId() { return localSaleId; }


    public String getCloudId() {return cloudId;}
    public void setCloudId(String cloudId){this.cloudId= cloudId;}
}

