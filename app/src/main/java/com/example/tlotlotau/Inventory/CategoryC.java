package com.example.tlotlotau.Inventory;

import android.os.Parcel;
import android.os.Parcelable;

public class CategoryC implements Parcelable {
    private long id;
    private String name;
    private String dateCreated;

    public CategoryC(long id, String name, String dateCreated) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
    }

    protected CategoryC(Parcel in) {
        id = in.readLong();
        name = in.readString();
        dateCreated = in.readString();
    }

    public static final Creator<CategoryC> CREATOR = new Creator<CategoryC>() {
        @Override
        public CategoryC createFromParcel(Parcel in) {
            return new CategoryC(in);
        }

        @Override
        public CategoryC[] newArray(int size) {
            return new CategoryC[size];
        }
    };

    // Standard getters / setters
    public long getId() { return id; }
    public String getName() { return name; }
    public String getDateCreated() { return dateCreated; }

    public void setId(long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(dateCreated);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
