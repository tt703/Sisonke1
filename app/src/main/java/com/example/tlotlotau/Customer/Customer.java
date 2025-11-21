package com.example.tlotlotau.Customer;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Customer implements Parcelable {
    public long id;
    private String name;
    private String address;
    private String phone;
    private String email;

    private String amountDue;
    private String numEstimatesSent;
    private String lastInvoiceTs;
    private String cloudId;
    private String dateCreated;
    private String NumEstimates;
    private String NumInvoices;
    private String TotalAmount;





    public Customer(long id,String name, String address, String phone, String email) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;

    }

    protected Customer(Parcel in) {
        name = in.readString();
        address = in.readString();
        phone = in.readString();
        email = in.readString();
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

    public Customer() {

    }


    public String getName() {
        return name;
    }
    public long getId() {return id;}

    public void setName(String name) {this.name = name;}
    public void setAddress(String address) {this.address = address;}
    public void setPhone(String phone) {this.phone = phone;}
    public void setEmail(String email) {this.email = email;}

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }
    public String getAmountDue(){
        return amountDue;
    }
    public String getEmail(){return email;}
    public String getNumEstimatesSent(){
        return numEstimatesSent;
    }
    public void setAmountDue(String amountDue){this.amountDue = amountDue;}
    public void setNumEstimateSent(String numEstimatesSent){this.numEstimatesSent = numEstimatesSent;}
    public void setId(long id){this.id = id;}
    public void setLastInvoiceTs(String lastInvoiceTs){this.lastInvoiceTs = lastInvoiceTs;}
    public String getLastInvoiceTs(){return lastInvoiceTs;}
    public String getCloudId() { return cloudId; }
    public void setCloudId(String cloudId) { this.cloudId = cloudId; }
    public String getDateCreated() {return dateCreated;}
    public String getNumEstimates(){return NumEstimates;}
    public String getNumInvoices(){return NumInvoices;}
    public String getTotalAmount(){return TotalAmount;}









    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(phone);
        dest.writeString(email);
    }

    @Override
    public int describeContents() {
        return 0;
    }


}