package com.example.tlotlotau;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Customer extends ArrayList<Parcelable> implements Parcelable {
    private String name;
    private String address;
    private String contactInfo;

    public Customer(String name, String address, String contactInfo) {
        this.name = name;
        this.address = address;
        this.contactInfo = contactInfo;
    }

    protected Customer(Parcel in) {
        name = in.readString();
        address = in.readString();
        contactInfo = in.readString();
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

    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(contactInfo);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
