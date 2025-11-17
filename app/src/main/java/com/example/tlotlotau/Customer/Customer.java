package com.example.tlotlotau.Customer;

import android.os.Parcel;
import android.os.Parcelable;


public class Customer implements Parcelable {
    public long id;
    private String name;
    private String address;
    private String phone;
    private String email;

    private String amountDue;
    private String numEstimatesSent;


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

    public String getName() {
        return name;
    }

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