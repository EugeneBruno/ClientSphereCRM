package com.clientsphere.crm.repository;

import com.clientsphere.crm.config.MongoDBConnection;
import com.clientsphere.crm.model.Interaction;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class InteractionRepository {

    private final MongoCollection<Document> interactionCollection;


    public InteractionRepository() {

        MongoDatabase database =
                MongoDBConnection.getDatabase();

        interactionCollection =
                database.getCollection("interactions");
    }


    // =========================
    // CREATE INTERACTION
    // =========================

    public void save(Interaction interaction) {

        Document document = new Document()
                .append(
                        "customerId",
                        interaction.getCustomerId()
                )
                .append(
                        "type",
                        interaction.getType()
                )
                .append(
                        "subject",
                        interaction.getSubject()
                )
                .append(
                        "description",
                        interaction.getDescription()
                )
                .append(
                        "interactionDate",
                        interaction.getInteractionDate().toString()
                )
                .append(
                        "createdAt",
                        interaction.getCreatedAt().toString()
                );


        interactionCollection.insertOne(document);


        interaction.setId(
                document.getObjectId("_id")
        );
    }


    // =========================
    // READ ALL INTERACTIONS
    // =========================

    public List<Interaction> findAll() {

        List<Interaction> interactions =
                new ArrayList<>();


        for (Document document :
                interactionCollection.find()) {

            Interaction interaction =
                    new Interaction();


            interaction.setId(
                    document.getObjectId("_id")
            );


            interaction.setCustomerId(
                    document.getObjectId("customerId")
            );


            interaction.setType(
                    document.getString("type")
            );


            interaction.setSubject(
                    document.getString("subject")
            );


            interaction.setDescription(
                    document.getString("description")
            );


            String interactionDate =
                    document.getString("interactionDate");


            if (interactionDate != null) {

                interaction.setInteractionDate(
                        LocalDateTime.parse(interactionDate)
                );
            }


            String createdAt =
                    document.getString("createdAt");


            if (createdAt != null) {

                interaction.setCreatedAt(
                        LocalDateTime.parse(createdAt)
                );
            }


            interactions.add(interaction);
        }


        return interactions;
    }


    // =========================
    // FIND INTERACTIONS BY CUSTOMER
    // =========================

    public List<Interaction> findByCustomerId(
            ObjectId customerId
    ) {

        List<Interaction> interactions =
                new ArrayList<>();


        for (Document document :
                interactionCollection.find(
                        eq("customerId", customerId)
                )) {

            Interaction interaction =
                    new Interaction();


            interaction.setId(
                    document.getObjectId("_id")
            );


            interaction.setCustomerId(
                    document.getObjectId("customerId")
            );


            interaction.setType(
                    document.getString("type")
            );


            interaction.setSubject(
                    document.getString("subject")
            );


            interaction.setDescription(
                    document.getString("description")
            );


            String interactionDate =
                    document.getString(
                            "interactionDate"
                    );


            if (interactionDate != null) {

                interaction.setInteractionDate(
                        LocalDateTime.parse(
                                interactionDate
                        )
                );
            }


            String createdAt =
                    document.getString("createdAt");


            if (createdAt != null) {

                interaction.setCreatedAt(
                        LocalDateTime.parse(createdAt)
                );
            }


            interactions.add(interaction);
        }


        return interactions;
    }

    // =========================
    // UPDATE INTERACTION
    // =========================

    public void update(Interaction interaction) {

        Document updatedInteraction = new Document()
                .append(
                        "customerId",
                        interaction.getCustomerId()
                )
                .append(
                        "type",
                        interaction.getType()
                )
                .append(
                        "subject",
                        interaction.getSubject()
                )
                .append(
                        "description",
                        interaction.getDescription()
                )
                .append(
                        "interactionDate",
                        interaction.getInteractionDate().toString()
                );


        interactionCollection.updateOne(

                eq("_id", interaction.getId()),

                new Document(
                        "$set",
                        updatedInteraction
                )
        );


        System.out.println(
                "Interaction updated successfully!"
        );
    }

    // =========================
    // DELETE INTERACTION
    // =========================

    public void delete(ObjectId id) {

        interactionCollection.deleteOne(
                eq("_id", id)
        );
    }
}