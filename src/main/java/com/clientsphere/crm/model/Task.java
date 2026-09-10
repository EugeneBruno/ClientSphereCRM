package com.clientsphere.crm.model;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class Task {

    private ObjectId id;

    private ObjectId customerId;

    private String title;

    private String description;

    private LocalDateTime dueDate;

    private String priority;

    private String status;

    private LocalDateTime createdAt;


    public Task() {

    }


    // =========================
    // ID
    // =========================

    public ObjectId getId() {

        return id;
    }

    public void setId(
            ObjectId id
    ) {

        this.id = id;
    }


    // =========================
    // CUSTOMER ID
    // =========================

    public ObjectId getCustomerId() {

        return customerId;
    }

    public void setCustomerId(
            ObjectId customerId
    ) {

        this.customerId = customerId;
    }


    // =========================
    // TITLE
    // =========================

    public String getTitle() {

        return title;
    }

    public void setTitle(
            String title
    ) {

        this.title = title;
    }


    // =========================
    // DESCRIPTION
    // =========================

    public String getDescription() {

        return description;
    }

    public void setDescription(
            String description
    ) {

        this.description = description;
    }


    // =========================
    // DUE DATE
    // =========================

    public LocalDateTime getDueDate() {

        return dueDate;
    }

    public void setDueDate(
            LocalDateTime dueDate
    ) {

        this.dueDate = dueDate;
    }


    // =========================
    // PRIORITY
    // =========================

    public String getPriority() {

        return priority;
    }

    public void setPriority(
            String priority
    ) {

        this.priority = priority;
    }


    // =========================
    // STATUS
    // =========================

    public String getStatus() {

        return status;
    }

    public void setStatus(
            String status
    ) {

        this.status = status;
    }


    // =========================
    // CREATED AT
    // =========================

    public LocalDateTime getCreatedAt() {

        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {

        this.createdAt = createdAt;
    }
}