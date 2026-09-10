package com.clientsphere.crm.repository;

import com.clientsphere.crm.config.MongoDBConnection;
import com.clientsphere.crm.model.Deal;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class DealRepository {

    private final MongoCollection<Document> dealsCollection;

    public DealRepository() {
        MongoDatabase database = MongoDBConnection.getDatabase();
        dealsCollection = database.getCollection("deals");
    }

    public void save(Deal deal) {
        Document document = new Document()
                .append("customerId", deal.getCustomerId())
                .append("title", deal.getTitle())
                .append("description", deal.getDescription())
                .append("value", deal.getValue())
                .append("status", deal.getStatus())
                .append(
                        "expectedCloseDate",
                        deal.getExpectedCloseDate() != null
                                ? deal.getExpectedCloseDate().toString()
                                : null
                )
                .append(
                        "createdAt",
                        deal.getCreatedAt() != null
                                ? deal.getCreatedAt().toString()
                                : null
                );

        dealsCollection.insertOne(document);

        deal.setId(document.getObjectId("_id"));
    }

    public List<Deal> findAll() {
        List<Deal> deals = new ArrayList<>();

        for (Document document : dealsCollection.find()) {
            deals.add(documentToDeal(document));
        }

        return deals;
    }

    public List<Deal> findByCustomerId(ObjectId customerId) {
        List<Deal> deals = new ArrayList<>();

        for (Document document :
                dealsCollection.find(eq("customerId", customerId))) {

            deals.add(documentToDeal(document));
        }

        return deals;
    }

    public void update(Deal deal) {
        dealsCollection.updateOne(
                eq("_id", deal.getId()),
                combine(
                        set("customerId", deal.getCustomerId()),
                        set("title", deal.getTitle()),
                        set("description", deal.getDescription()),
                        set("value", deal.getValue()),
                        set("status", deal.getStatus()),
                        set(
                                "expectedCloseDate",
                                deal.getExpectedCloseDate() != null
                                        ? deal.getExpectedCloseDate().toString()
                                        : null
                        )
                )
        );
    }

    public void delete(ObjectId id) {
        dealsCollection.deleteOne(eq("_id", id));
    }

    private Deal documentToDeal(Document document) {
        Deal deal = new Deal();

        deal.setId(document.getObjectId("_id"));
        deal.setCustomerId(document.getObjectId("customerId"));
        deal.setTitle(document.getString("title"));
        deal.setDescription(document.getString("description"));

        Number value = document.get("value", Number.class);
        deal.setValue(value != null ? value.doubleValue() : 0.0);

        deal.setStatus(document.getString("status"));

        String expectedCloseDate =
                document.getString("expectedCloseDate");

        if (expectedCloseDate != null) {
            deal.setExpectedCloseDate(
                    LocalDateTime.parse(expectedCloseDate)
            );
        }

        String createdAt = document.getString("createdAt");

        if (createdAt != null) {
            deal.setCreatedAt(LocalDateTime.parse(createdAt));
        }

        return deal;
    }
}