package com.example.tlotlotau.Documents.Estimate;

public  class Estimate {
    private int id;
    private String customerName;
    private String customerAddress;
    private String customerContact;
    private String itemDetails;
    private double totalAmount;
    private String timestamp;
    private String status;
    private String filePath; // Add this line

    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", customerName='" + customerName + '\'' +
                ", customerAddress='" + customerAddress + '\'' +
                ", customerContact='" + customerContact + '\'' +
                ", itemDetails='" + itemDetails + '\'' +
                ", totalAmount=" + totalAmount +
                ", filePath='" + filePath + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';

    }

    public int getId() {return id;}

    public void setId(int id) {this.id = id;}

    public String getCustomerName() {return customerName;}

    public void setCustomerName(String customerName) {this.customerName = customerName;}

    public void setCustomerAddress(String customerAddress) {this.customerAddress = customerAddress;}

    public void setItemDetails(String itemDetails) {this.itemDetails = itemDetails;}

    public double getTotalAmount() {return totalAmount;}
    public void setTotalAmount(double totalAmount) {this.totalAmount = totalAmount;}
    public String getFilePath() {return filePath;}

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
    public String getCustomerEmail() {
        return customerEmail;
    }
    private String customerEmail;
    public String getCustomerAddress() {return customerAddress;}

    public String getCustomerContact() {return customerContact;}
    public String getItemDetails() {return itemDetails;}
    public String getStatus() {return status;}

    public void setCustomerContact(String customerContact) {
        this.customerContact = customerContact;
    }
}