package com.clientsphere.crm.model;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class Interaction {

    private ObjectId id;
    private ObjectId customerId;
    private String type;
    private String subject;
    private String description;
    private LocalDateTime interactionDate;
    private LocalDateTime createdAt;

    public Interaction() {

    }

    // ID
    public ObjectId getId() {

        return id;
    }
    public void setId(ObjectId id) {

        this.id = id;
    }

    // CUSTOMER ID
    public ObjectId getCustomerId() {

        return customerId;
    }
    public void setCustomerId(ObjectId customerId) {

        this.customerId = customerId;
    }

    // TYPE
    public String getType() {

        return type;
    }
    public void setType(String type) {

        this.type = type;
    }

    // SUBJECT
    public String getSubject() {

        return subject;
    }
    public void setSubject(String subject) {

        this.subject = subject;
    }


     
    // DESCRIPTION
    public String getDescription() {

        return description;
    }
    public void setDescription(String description) {

        this.description = description;
    }

    // INTERACTION DATE
    public LocalDateTime getInteractionDate() {

        return interactionDate;
    }
    public void setInteractionDate(
            LocalDateTime interactionDate
    ) {

        this.interactionDate = interactionDate;
    }


     
    // CREATED AT
    public LocalDateTime getCreatedAt() {

        return createdAt;
    }
    public void setCreatedAt(
            LocalDateTime createdAt
    ) {

        this.createdAt = createdAt;
    }
}