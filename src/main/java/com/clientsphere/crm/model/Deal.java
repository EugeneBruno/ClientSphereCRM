package com.clientsphere.crm.model;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class Deal {

    private ObjectId id;
    private ObjectId customerId;
    private String title;
    private String description;
    private double value;
    private String status;
    private LocalDateTime expectedCloseDate;
    private LocalDateTime createdAt;

    public Deal() {
    }

    public Deal(
            ObjectId customerId,
            String title,
            String description,
            double value,
            String status,
            LocalDateTime expectedCloseDate,
            LocalDateTime createdAt
    ) {
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        this.value = value;
        this.status = status;
        this.expectedCloseDate = expectedCloseDate;
        this.createdAt = createdAt;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public ObjectId getCustomerId() {
        return customerId;
    }

    public void setCustomerId(ObjectId customerId) {
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getExpectedCloseDate() {
        return expectedCloseDate;
    }

    public void setExpectedCloseDate(LocalDateTime expectedCloseDate) {
        this.expectedCloseDate = expectedCloseDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}