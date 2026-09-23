package com.clientsphere.crm.repository;

import com.clientsphere.crm.config.MongoDBConnection;
import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.util.CurrentUser;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {

    private final MongoCollection<Document> collection;

    public CustomerRepository() {

        collection = MongoDBConnection
                .getDatabase()
                .getCollection("customers");
    }

    // ============================================================
    // SAVE CUSTOMER
    // ============================================================

    public void save(Customer customer) {

        /*
         * Automatically assign the customer to the
         * currently logged-in HR/User.
         *
         * Admin can also create a customer. In that case,
         * no HR is assigned automatically.
         */
        if (
                customer.getAssignedTo() == null &&
                        CurrentUser.isLoggedIn() &&
                        CurrentUser.isUser()
        ) {
            customer.setAssignedTo(
                    CurrentUser.getUser().getId()
            );
        }

        Document document = new Document()
                .append(
                        "_id",
                        customer.getId()
                )
                .append(
                        "firstName",
                        customer.getFirstName()
                )
                .append(
                        "lastName",
                        customer.getLastName()
                )
                .append(
                        "email",
                        customer.getEmail()
                )
                .append(
                        "phone",
                        customer.getPhone()
                )
                .append(
                        "company",
                        customer.getCompany()
                )
                .append(
                        "status",
                        customer.getStatus()
                )
                .append(
                        "createdAt",
                        customer.getCreatedAt() != null
                                ? customer.getCreatedAt().toString()
                                : LocalDateTime.now().toString()
                )
                .append(
                        "assignedTo",
                        customer.getAssignedTo()
                );

        collection.insertOne(document);
    }

    // ============================================================
    // FIND ALL
    // ============================================================

    public List<Customer> findAll() {

        List<Customer> customers =
                new ArrayList<>();

        /*
         * ADMIN:
         * See every customer.
         */
        if (CurrentUser.isAdmin()) {

            for (
                    Document document :
                    collection.find()
            ) {
                customers.add(
                        documentToCustomer(document)
                );
            }

            return customers;
        }

        /*
         * USER / HR:
         * Only see customers assigned to them.
         */

        if (CurrentUser.isUser()) {

            ObjectId userId =
                    CurrentUser.getUser().getId();

            for (
                    Document document :
                    collection.find(
                            Filters.or(
                                    Filters.eq(
                                            "assignedTo",
                                            userId
                                    ),
                                    Filters.eq(
                                            "assignedTo",
                                            null
                                    )
                            )
                    )
            ) {
                customers.add(
                        documentToCustomer(document)
                );
            }

            return customers;
        }

        /*
         * If nobody is logged in, return nothing.
         */
        return customers;
    }

    // ============================================================
    // FIND BY ID
    // ============================================================

    public Customer findById(ObjectId id) {

        Document document =
                collection.find(
                        Filters.eq("_id", id)
                ).first();

        if (document == null) {
            return null;
        }

        Customer customer =
                documentToCustomer(document);

        /*
         * ADMIN can access every customer.
         */
        if (CurrentUser.isAdmin()) {
            return customer;
        }

        /*
         * USER can only access their own customer.
         */
        if (CurrentUser.isUser()) {

            ObjectId currentUserId =
                    CurrentUser.getUser().getId();

            if (
                    customer.getAssignedTo() != null &&
                            customer.getAssignedTo()
                                    .equals(currentUserId)
            ) {
                return customer;
            }
        }

        return null;
    }

    // ============================================================
    // UPDATE CUSTOMER
    // ============================================================

    public void update(Customer customer) {

        if (customer.getId() == null) {
            return;
        }

        /*
         * HR/User can only update a customer assigned to them.
         */
        if (
                CurrentUser.isUser() &&
                        (
                                customer.getAssignedTo() == null ||
                                        !customer.getAssignedTo()
                                                .equals(
                                                        CurrentUser
                                                                .getUser()
                                                                .getId()
                                                )
                        )
        ) {
            return;
        }

        collection.updateOne(
                Filters.eq(
                        "_id",
                        customer.getId()
                ),
                Updates.combine(

                        Updates.set(
                                "firstName",
                                customer.getFirstName()
                        ),

                        Updates.set(
                                "lastName",
                                customer.getLastName()
                        ),

                        Updates.set(
                                "email",
                                customer.getEmail()
                        ),

                        Updates.set(
                                "phone",
                                customer.getPhone()
                        ),

                        Updates.set(
                                "company",
                                customer.getCompany()
                        ),

                        Updates.set(
                                "status",
                                customer.getStatus()
                        ),

                        Updates.set(
                                "assignedTo",
                                customer.getAssignedTo()
                        )
                )
        );
    }

    // ============================================================
    // UPDATE STATUS
    // ============================================================

    public void updateStatus(
            String customerId,
            String status
    ) {

        if (customerId == null || status == null) {
            return;
        }

        ObjectId objectId;

        try {

            objectId =
                    new ObjectId(customerId);

        } catch (IllegalArgumentException e) {

            return;
        }

        Customer customer =
                findById(objectId);

        if (customer == null) {
            return;
        }

        collection.updateOne(
                Filters.eq(
                        "_id",
                        objectId
                ),
                Updates.set(
                        "status",
                        status
                )
        );
    }

    // ============================================================
    // DELETE CUSTOMER
    // ============================================================

    public void delete(ObjectId customerId) {

        if (customerId == null) {
            return;
        }

        /*
         * ADMIN can delete any customer.
         */
        if (CurrentUser.isAdmin()) {

            collection.deleteOne(
                    Filters.eq(
                            "_id",
                            customerId
                    )
            );

            return;
        }

        /*
         * USER can only delete their own customer.
         */
        if (CurrentUser.isUser()) {

            collection.deleteOne(
                    Filters.and(
                            Filters.eq(
                                    "_id",
                                    customerId
                            ),
                            Filters.eq(
                                    "assignedTo",
                                    CurrentUser
                                            .getUser()
                                            .getId()
                            )
                    )
            );
        }
    }

    // ============================================================
    // FIND CUSTOMERS BY USER
    // ============================================================

    public List<Customer> findByAssignedTo(
            ObjectId userId
    ) {

        List<Customer> customers =
                new ArrayList<>();

        if (userId == null) {
            return customers;
        }

        for (
                Document document :
                collection.find(
                        Filters.eq(
                                "assignedTo",
                                userId
                        )
                )
        ) {

            customers.add(
                    documentToCustomer(document)
            );
        }

        return customers;
    }

    // ============================================================
    // ASSIGN CUSTOMER TO USER
    // ============================================================

    public void assignTo(
            ObjectId customerId,
            ObjectId userId
    ) {

        if (
                customerId == null ||
                        userId == null
        ) {
            return;
        }

        /*
         * Only Admin should assign/reassign customers.
         */
        if (!CurrentUser.isAdmin()) {
            return;
        }

        collection.updateOne(
                Filters.eq(
                        "_id",
                        customerId
                ),
                Updates.set(
                        "assignedTo",
                        userId
                )
        );
    }

    // ============================================================
    // CONVERT DOCUMENT TO CUSTOMER
    // ============================================================

    private Customer documentToCustomer(
            Document document
    ) {

        Customer customer =
                new Customer();

        customer.setId(
                document.getObjectId("_id")
        );

        customer.setFirstName(
                document.getString("firstName")
        );

        customer.setLastName(
                document.getString("lastName")
        );

        customer.setEmail(
                document.getString("email")
        );

        customer.setPhone(
                document.getString("phone")
        );

        customer.setCompany(
                document.getString("company")
        );

        customer.setStatus(
                document.getString("status")
        );

        // --------------------------------------------------------
        // CREATED AT
        // --------------------------------------------------------

        String createdAt =
                document.getString("createdAt");

        if (createdAt != null) {

            try {

                customer.setCreatedAt(
                        LocalDateTime.parse(
                                createdAt
                        )
                );

            } catch (Exception e) {

                customer.setCreatedAt(
                        LocalDateTime.now()
                );
            }

        } else {

            customer.setCreatedAt(
                    LocalDateTime.now()
            );
        }

        // --------------------------------------------------------
        // ASSIGNED TO
        // --------------------------------------------------------

        ObjectId assignedTo =
                document.getObjectId(
                        "assignedTo"
                );

        customer.setAssignedTo(
                assignedTo
        );

        return customer;
    }
}