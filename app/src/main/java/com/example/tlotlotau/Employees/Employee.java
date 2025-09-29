package com.example.tlotlotau.Employees;

import com.google.firebase.Timestamp;

public class Employee {
    public String uid;
    public String name;
    public String email;
    public String phone;
    public String role;
    public boolean isActive;       // public so Firestore mapping and adapter can access
    public boolean mustChangePassword;
    public Timestamp createdAt;
    public String createdBy;


    public Employee() {}

    public Employee(String uid, String name, String email, String phone, String role,
                    boolean isActive, boolean mustChangePassword, Timestamp createdAt,
                    String createdBy) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.isActive = isActive;
        this.mustChangePassword = mustChangePassword;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
    }
}
