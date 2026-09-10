package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.repository.InteractionRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class CustomerInteractionHistory {

    private final Customer customer;

    private final Stage stage;

    private final InteractionRepository interactionRepository;

    private TableView<Interaction> interactionTable;

    private ObservableList<Interaction> interactions;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, HH:mm"
            );


    public CustomerInteractionHistory(
            Customer customer
    ) {

        this.customer = customer;

        this.stage = new Stage();

        this.interactionRepository =
                new InteractionRepository();

        this.interactions =
                FXCollections.observableArrayList();

        createView();
    }


    private void createView() {

        // =========================
        // TITLE
        // =========================

        Label title = new Label(
                customer.getFirstName()
                        + " "
                        + customer.getLastName()
                        + "'s Interaction History"
        );


        title.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );


        // =========================
        // CUSTOMER DETAILS
        // =========================

        Label customerDetails = new Label(
                "Customer: "
                        + customer.getFirstName()
                        + " "
                        + customer.getLastName()
                        + " | "
                        + customer.getEmail()
        );


        customerDetails.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748B;"
        );


        // =========================
        // INTERACTION COUNT
        // =========================

        Label interactionCountLabel =
                new Label();

        interactionCountLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2563EB;"
        );


        // =========================
        // NO INTERACTIONS MESSAGE
        // =========================

        Label noInteractionsLabel =
                new Label(
                        "No interactions found for this customer."
                );

        noInteractionsLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #64748B;"
        );

        noInteractionsLabel.setVisible(false);

        noInteractionsLabel.setManaged(false);


        // =========================
        // INTERACTION TABLE
        // =========================

        interactionTable =
                createInteractionTable();


        // =========================
        // MAIN LAYOUT
        // =========================

        VBox root = new VBox(
                15,
                title,
                customerDetails,
                interactionCountLabel,
                noInteractionsLabel,
                interactionTable
        );


        root.setPadding(
                new Insets(30)
        );


        VBox.setVgrow(
                interactionTable,
                Priority.ALWAYS
        );


        // =========================
        // SCENE
        // =========================

        Scene scene = new Scene(
                root,
                900,
                550
        );


        stage.setTitle(
                "Customer Interaction History"
        );


        stage.setScene(scene);


        // =========================
        // LOAD INTERACTIONS
        // =========================

        loadInteractions(
                interactionCountLabel,
                noInteractionsLabel
        );
    }


    private TableView<Interaction>
    createInteractionTable() {

        TableView<Interaction> table =
                new TableView<>();


        // =========================
        // TYPE
        // =========================

        TableColumn<Interaction, String>
                typeColumn =
                new TableColumn<>("Type");


        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "type"
                )
        );


        // =========================
        // SUBJECT
        // =========================

        TableColumn<Interaction, String>
                subjectColumn =
                new TableColumn<>("Subject");


        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "subject"
                )
        );


        // =========================
        // DESCRIPTION
        // =========================

        TableColumn<Interaction, String>
                descriptionColumn =
                new TableColumn<>("Description");


        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "description"
                )
        );


        // =========================
        // DATE
        // =========================

        TableColumn<Interaction, String>
                dateColumn =
                new TableColumn<>("Date");


        dateColumn.setCellValueFactory(cellData -> {

            LocalDateTime date =
                    cellData.getValue()
                            .getInteractionDate();


            String formattedDate =
                    date != null
                            ? date.format(dateFormatter)
                            : "";


            return new javafx.beans.property
                    .SimpleStringProperty(
                    formattedDate
            );
        });


        // =========================
        // COLUMN WIDTHS
        // =========================

        typeColumn.setPrefWidth(120);

        subjectColumn.setPrefWidth(200);

        descriptionColumn.setPrefWidth(350);

        dateColumn.setPrefWidth(180);


        table.getColumns().addAll(
                typeColumn,
                subjectColumn,
                descriptionColumn,
                dateColumn
        );


        table.setItems(
                interactions
        );


        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        return table;
    }


    // =========================
    // LOAD CUSTOMER INTERACTIONS
    // =========================

    private void loadInteractions(
            Label interactionCountLabel,
            Label noInteractionsLabel
    ) {

        List<Interaction> interactionList =
                interactionRepository.findByCustomerId(
                        customer.getId()
                );


        // SORT NEWEST FIRST

        interactionList.sort(
                Comparator.comparing(
                        Interaction::getInteractionDate,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );


        interactions.clear();


        interactions.addAll(
                interactionList
        );


        // UPDATE INTERACTION COUNT

        interactionCountLabel.setText(
                "Total Interactions: "
                        + interactionList.size()
        );


        // HANDLE EMPTY INTERACTION HISTORY

        if (interactionList.isEmpty()) {

            noInteractionsLabel.setVisible(true);

            noInteractionsLabel.setManaged(true);

            interactionTable.setVisible(false);

            interactionTable.setManaged(false);

        } else {

            noInteractionsLabel.setVisible(false);

            noInteractionsLabel.setManaged(false);

            interactionTable.setVisible(true);

            interactionTable.setManaged(true);
        }
    }


    public void show() {

        stage.show();
    }
}