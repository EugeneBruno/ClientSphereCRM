package com.clientsphere.crm.model;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;

public class User {

    public enum Role {
        USER,
        ADMIN
    }

    private ObjectId id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(
            String firstName,
            String lastName,
            String email,
            String password
    ) {
        this.id = new ObjectId();
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
        this.active = true;
        this.createdAt = LocalDateTime.now();
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }
}