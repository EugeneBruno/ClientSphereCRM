package com.clientsphere.crm.repository;

import com.clientsphere.crm.config.MongoDBConnection;
import com.clientsphere.crm.model.Task;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class TaskRepository {

    private final MongoCollection<Document> taskCollection;


    public TaskRepository() {

        MongoDatabase database =
                MongoDBConnection.getDatabase();

        taskCollection =
                database.getCollection("tasks");
    }


    // =========================
    // CREATE TASK
    // =========================

    public void save(Task task) {

        Document document = new Document()
                .append(
                        "customerId",
                        task.getCustomerId()
                )
                .append(
                        "title",
                        task.getTitle()
                )
                .append(
                        "description",
                        task.getDescription()
                )
                .append(
                        "dueDate",
                        task.getDueDate() != null
                                ? task.getDueDate().toString()
                                : null
                )
                .append(
                        "priority",
                        task.getPriority()
                )
                .append(
                        "status",
                        task.getStatus()
                )
                .append(
                        "createdAt",
                        task.getCreatedAt() != null
                                ? task.getCreatedAt().toString()
                                : null
                );


        taskCollection.insertOne(document);


        task.setId(
                document.getObjectId("_id")
        );
    }


    // =========================
    // READ ALL TASKS
    // =========================

    public List<Task> findAll() {

        List<Task> tasks =
                new ArrayList<>();


        for (Document document :
                taskCollection.find()) {

            Task task =
                    documentToTask(document);

            tasks.add(task);
        }


        return tasks;
    }


    // =========================
    // FIND TASKS BY CUSTOMER
    // =========================

    public List<Task> findByCustomerId(
            ObjectId customerId
    ) {

        List<Task> tasks =
                new ArrayList<>();


        for (Document document :
                taskCollection.find(
                        eq("customerId", customerId)
                )) {

            Task task =
                    documentToTask(document);

            tasks.add(task);
        }


        return tasks;
    }


    // =========================
    // UPDATE TASK
    // =========================

    public void update(Task task) {

        Document updateDocument = new Document()
                .append(
                        "customerId",
                        task.getCustomerId()
                )
                .append(
                        "title",
                        task.getTitle()
                )
                .append(
                        "description",
                        task.getDescription()
                )
                .append(
                        "dueDate",
                        task.getDueDate() != null
                                ? task.getDueDate().toString()
                                : null
                )
                .append(
                        "priority",
                        task.getPriority()
                )
                .append(
                        "status",
                        task.getStatus()
                );


        taskCollection.updateOne(
                eq("_id", task.getId()),
                new Document("$set", updateDocument)
        );
    }


    // =========================
    // DELETE TASK
    // =========================

    public void delete(ObjectId id) {

        taskCollection.deleteOne(
                eq("_id", id)
        );
    }


    // =========================
    // DOCUMENT → TASK
    // =========================

    private Task documentToTask(
            Document document
    ) {

        Task task =
                new Task();


        task.setId(
                document.getObjectId("_id")
        );


        task.setCustomerId(
                document.getObjectId("customerId")
        );


        task.setTitle(
                document.getString("title")
        );


        task.setDescription(
                document.getString("description")
        );


        String dueDate =
                document.getString("dueDate");

        if (dueDate != null) {

            task.setDueDate(
                    LocalDateTime.parse(dueDate)
            );
        }


        task.setPriority(
                document.getString("priority")
        );


        task.setStatus(
                document.getString("status")
        );


        String createdAt =
                document.getString("createdAt");

        if (createdAt != null) {

            task.setCreatedAt(
                    LocalDateTime.parse(createdAt)
            );
        }


        return task;
    }
}