package com.clientsphere.crm.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnection {

    private static final String CONNECTION_STRING =
            "mongodb://localhost:27017";

    private static final String DATABASE_NAME =
            "clientsphere_crm";

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {

        if (database == null) {

            mongoClient = MongoClients.create(CONNECTION_STRING);

            database = mongoClient.getDatabase(DATABASE_NAME);

            System.out.println("Connected to MongoDB successfully!");
        }

        return database;
    }

    public static void closeConnection() {

        if (mongoClient != null) {

            mongoClient.close();

            System.out.println("MongoDB connection closed.");
        }
    }
}