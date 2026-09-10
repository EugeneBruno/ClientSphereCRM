package com.clientsphere.crm.repository;

import com.clientsphere.crm.config.MongoDBConnection;
import com.clientsphere.crm.model.Customer;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class CustomerRepository {

    private final MongoCollection<Document> customerCollection;


    public CustomerRepository() {

        MongoDatabase database = MongoDBConnection.getDatabase();

        customerCollection = database.getCollection("customers");
    }


    // CREATE CUSTOMER
    public void save(Customer customer) {

        Document document = new Document()
                .append("firstName", customer.getFirstName())
                .append("lastName", customer.getLastName())
                .append("email", customer.getEmail())
                .append("phone", customer.getPhone())
                .append("company", customer.getCompany())
                .append("status", customer.getStatus())
                .append("createdAt", customer.getCreatedAt().toString());

        customerCollection.insertOne(document);

        customer.setId(document.getObjectId("_id"));
    }


    // READ ALL CUSTOMERS
    public List<Customer> findAll() {

        List<Customer> customers = new ArrayList<>();

        for (Document document : customerCollection.find()) {

            Customer customer = new Customer();

            customer.setId(document.getObjectId("_id"));
            customer.setFirstName(document.getString("firstName"));
            customer.setLastName(document.getString("lastName"));
            customer.setEmail(document.getString("email"));
            customer.setPhone(document.getString("phone"));
            customer.setCompany(document.getString("company"));
            customer.setStatus(document.getString("status"));

            String createdAt =
                    document.getString("createdAt");

            if (createdAt != null) {
                customer.setCreatedAt(
                        LocalDateTime.parse(createdAt)
                );
            }

            customers.add(customer);
        }

        return customers;
    }

    public void updateStatus(String email, String newStatus) {

        var result = customerCollection.updateOne(
                Filters.eq("email", email),
                Updates.set("status", newStatus)
        );

        if (result.getModifiedCount() > 0){
            System.out.println("Customer status updated successfully!");
        } else {
            System.out.println("Customer not found or status was already the same.");
        }

        System.out.println("Customer status updated successfully!");
    }


    // FIND CUSTOMER BY ID
    public Customer findById(ObjectId id) {

        Document document =
                customerCollection.find(eq("_id", id)).first();

        if (document == null) {
            return null;
        }

        Customer customer = new Customer();

        customer.setId(document.getObjectId("_id"));
        customer.setFirstName(document.getString("firstName"));
        customer.setLastName(document.getString("lastName"));
        customer.setEmail(document.getString("email"));
        customer.setPhone(document.getString("phone"));
        customer.setCompany(document.getString("company"));
        customer.setStatus(document.getString("status"));

        String createdAt =
                document.getString("createdAt");

        if (createdAt != null) {
            customer.setCreatedAt(
                    LocalDateTime.parse(createdAt)
            );
        }

        return customer;
    }


    // UPDATE CUSTOMER
    public void update(Customer customer) {

        Document updatedCustomer = new Document()
                .append("firstName", customer.getFirstName())
                .append("lastName", customer.getLastName())
                .append("email", customer.getEmail())
                .append("phone", customer.getPhone())
                .append("company", customer.getCompany())
                .append("status", customer.getStatus());

        customerCollection.updateOne(
                eq("_id", customer.getId()),
                new Document("$set", updatedCustomer)
        );
    }


    // DELETE CUSTOMER
    public void delete(ObjectId id) {

        customerCollection.deleteOne(
                eq("_id", id)
        );
    }
}