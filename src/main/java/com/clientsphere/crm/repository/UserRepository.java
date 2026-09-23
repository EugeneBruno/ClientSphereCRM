package com.clientsphere.crm.repository;

import com.clientsphere.crm.config.MongoDBConnection;
import com.clientsphere.crm.model.User;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final MongoCollection<Document> collection;

    public UserRepository() {
        collection = MongoDBConnection
                .getDatabase()
                .getCollection("users");
    }

    public void create(User user) {

        Document document = new Document()
                .append("_id", user.getId())
                .append("firstName", user.getFirstName())
                .append("lastName", user.getLastName())
                .append("email", user.getEmail().toLowerCase())
                .append("password", user.getPassword())
                .append("role", user.getRole().name())
                .append("active", user.isActive())
                .append(
                        "createdAt",
                        user.getCreatedAt().toString()
                );

        collection.insertOne(document);
    }

    public User findByEmail(String email) {

        Document document = collection.find(
                Filters.eq(
                        "email",
                        email.toLowerCase()
                )
        ).first();

        if (document == null) {
            return null;
        }

        return documentToUser(document);
    }

    public User findById(ObjectId id) {

        Document document = collection.find(
                Filters.eq("_id", id)
        ).first();

        if (document == null) {
            return null;
        }

        return documentToUser(document);
    }

    public List<User> findAll() {

        List<User> users = new ArrayList<>();

        for (Document document : collection.find()) {
            users.add(documentToUser(document));
        }

        return users;
    }

    public List<User> findAllActive() {

        List<User> users = new ArrayList<>();

        for (
                Document document :
                collection.find(Filters.eq("active", true))
        ) {
            users.add(documentToUser(document));
        }

        return users;
    }

    public boolean emailExists(String email) {

        return collection.find(
                Filters.eq(
                        "email",
                        email.toLowerCase()
                )
        ).first() != null;
    }

    public void update(User user) {

        collection.updateOne(
                Filters.eq("_id", user.getId()),
                Updates.combine(
                        Updates.set(
                                "firstName",
                                user.getFirstName()
                        ),
                        Updates.set(
                                "lastName",
                                user.getLastName()
                        ),
                        Updates.set(
                                "email",
                                user.getEmail().toLowerCase()
                        ),
                        Updates.set(
                                "password",
                                user.getPassword()
                        ),
                        Updates.set(
                                "role",
                                user.getRole().name()
                        ),
                        Updates.set(
                                "active",
                                user.isActive()
                        )
                )
        );
    }

    public void updateActiveStatus(
            ObjectId userId,
            boolean active
    ) {

        collection.updateOne(
                Filters.eq("_id", userId),
                Updates.set("active", active)
        );
    }

    public void delete(ObjectId userId) {

        collection.deleteOne(
                Filters.eq("_id", userId)
        );
    }

    private User documentToUser(Document document) {

        User user = new User();

        user.setId(document.getObjectId("_id"));

        user.setFirstName(
                document.getString("firstName")
        );

        user.setLastName(
                document.getString("lastName")
        );

        user.setEmail(
                document.getString("email")
        );

        user.setPassword(
                document.getString("password")
        );

        String role = document.getString("role");

        if (role != null) {
            user.setRole(
                    User.Role.valueOf(role)
            );
        } else {
            user.setRole(User.Role.USER);
        }

        Boolean active = document.getBoolean("active");

        user.setActive(
                active == null || active
        );

        String createdAt =
                document.getString("createdAt");

        if (createdAt != null) {
            user.setCreatedAt(
                    LocalDateTime.parse(createdAt)
            );
        } else {
            user.setCreatedAt(
                    LocalDateTime.now()
            );
        }

        return user;
    }
}