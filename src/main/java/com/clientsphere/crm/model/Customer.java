package com.clientsphere.crm.model;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class Customer {

    private ObjectId id;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String company;
    private String status;

    private LocalDateTime createdAt;

    // HR/User responsible for this customer
    private ObjectId assignedTo;

    public Customer() {
    }

    public Customer(
            String firstName,
            String lastName,
            String email,
            String phone,
            String company,
            String status
    ) {
        this.id = new ObjectId();

        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.status = status;

        this.createdAt = LocalDateTime.now();

        // No HR assigned until the customer is created/assigned.
        this.assignedTo = null;
    }

    // ============================================================
    // ID
    // ============================================================

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    // ============================================================
    // FIRST NAME
    // ============================================================

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // ============================================================
    // LAST NAME
    // ============================================================

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // ============================================================
    // EMAIL
    // ============================================================

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ============================================================
    // PHONE
    // ============================================================

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // ============================================================
    // COMPANY
    // ============================================================

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    // ============================================================
    // STATUS
    // ============================================================

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // ============================================================
    // CREATED AT
    // ============================================================

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ============================================================
    // ASSIGNED USER / HR
    // ============================================================

    public ObjectId getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(ObjectId assignedTo) {
        this.assignedTo = assignedTo;
    }

    // ============================================================
    // FULL NAME
    // ============================================================

    public String getFullName() {
        return firstName + " " + lastName;
    }
}