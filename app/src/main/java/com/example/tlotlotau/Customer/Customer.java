package com.example.tlotlotau.Customer;

import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable {
    public long id; // This is the Local SQLite ID
    private String name;
    private String address;
    private String phone;
    private String email;

    // Stats (not saved to DB directly, calculated on fly)
    private String amountDue;
    private String numEstimatesSent;

    // Sync fields
    private String cloudId; // This is the Firestore ID
    private String lastInvoiceTs;
    private String dateCreated;
    private String NumEstimates;
    private String NumInvoices;
    private String TotalAmount;

    public Customer(long id, String name, String address, String phone, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

    // --- FIX: Make sure ID and CloudID are read here ---
    protected Customer(Parcel in) {
        id = in.readLong();          // <--- CRITICAL: Read ID
        name = in.readString();
        address = in.readString();
        phone = in.readString();
        email = in.readString();
        amountDue = in.readString();
        numEstimatesSent = in.readString();
        cloudId = in.readString();   // <--- CRITICAL: Read CloudID
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);          // <--- CRITICAL: Write ID
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeString(amountDue);
        dest.writeString(numEstimatesSent);
        dest.writeString(cloudId);   // <--- CRITICAL: Write CloudID
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    public Customer() { }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getAmountDue() { return amountDue; }
    public void setAmountDue(String amountDue) { this.amountDue = amountDue; }

    public String getNumEstimatesSent() { return numEstimatesSent; }
    public void setNumEstimateSent(String numEstimatesSent) { this.numEstimatesSent = numEstimatesSent; }

    public String getCloudId() { return cloudId; }
    public void setCloudId(String cloudId) { this.cloudId = cloudId; }

    public void setLastInvoiceTs(String lastInvoiceTs) { this.lastInvoiceTs = lastInvoiceTs; }
    public String getLastInvoiceTs() { return lastInvoiceTs; }

    public String getDateCreated() { return dateCreated; }
    public String getNumEstimates() { return NumEstimates; }
    public String getNumInvoices() { return NumInvoices; }
    public String getTotalAmount() { return TotalAmount; }

    @Override
    public int describeContents() { return 0; }
}