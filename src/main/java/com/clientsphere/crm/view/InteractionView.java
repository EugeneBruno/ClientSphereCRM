package com.clientsphere.crm.view;

import org.bson.types.ObjectId;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.repository.InteractionRepository;
import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.repository.CustomerRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InteractionView {
    private final InteractionRepository interactionRepository;
    private final CustomerRepository customerRepository;
    private Map<ObjectId, String> customerNames;
    private TableView<Interaction> interactionTable;
    private ObservableList<Interaction> interactions;
    private ObservableList<Interaction> filteredInteractions;
    private TextField searchField;

    public InteractionView() {
        interactionRepository = new InteractionRepository();
        customerRepository = new CustomerRepository();
        interactions = FXCollections.observableArrayList();
        filteredInteractions = FXCollections.observableArrayList();

        customerNames = new HashMap<>();
    }

    public BorderPane getView() {
        BorderPane mainLayout = new BorderPane();
        VBox content = new VBox(25);

        content.setPadding(new Insets(50));

        // PAGE HEADER
        HBox header = createHeader();

        // SEARCH BAR
        searchField = new TextField();

        searchField.setPromptText("Search interactions...");
        searchField.setPrefHeight(35);
        searchField.setMaxWidth(Double.MAX_VALUE);
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        filterInteractions(newValue)
        );

        // TABLE
        interactionTable = createInteractionTable();
        content.getChildren().addAll(
                header,
                searchField,
                interactionTable
        );

        VBox.setVgrow(
                interactionTable,
                Priority.ALWAYS
        );

        mainLayout.setCenter(content);

        loadInteractions();

        return mainLayout;
    }

    private HBox createHeader() {
        Label title = new Label("Interactions");

        title.setStyle(
                "-fx-font-size: 36px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );

        Label subtitle =
                new Label("Track and manage customer interactions");

        subtitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-text-fill: #64748B;"
        );


        VBox titleBox = new VBox(8);

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        Button addInteractionButton = new Button("+ Add Interaction");

        addInteractionButton.setOnAction(event -> {
            InteractionForm interactionForm = new InteractionForm();

            interactionForm.setOnInteractionSaved(this::loadInteractions);

            interactionForm.show();
        });

        addInteractionButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12px 20px;"
        );

        HBox header = new HBox();

        header.setAlignment(Pos.CENTER_LEFT);

        HBox.setHgrow(
                titleBox,
                Priority.ALWAYS
        );

        header.getChildren().addAll(
                titleBox,
                addInteractionButton
        );

        return header;
    }

    private TableView<Interaction>
    createInteractionTable() {

        TableView<Interaction> table =
                new TableView<>();


        // CUSTOMER ID

        TableColumn<Interaction, String>
                customerColumn = new TableColumn<>("Customer");

        customerColumn.setCellValueFactory(cellData -> {
            ObjectId customerID = cellData.getValue().getCustomerId();
            String customerName = customerNames.get(customerID);

            return  new javafx.beans.property.SimpleStringProperty(
                    customerName != null
                            ? customerName
                            : "Unknown Customer"
            );
                });

        // TYPE
        TableColumn<Interaction, String>
                typeColumn =
                new TableColumn<>("Type");

        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "type"
                )
        );


        // SUBJECT

        TableColumn<Interaction, String>
                subjectColumn =
                new TableColumn<>("Subject");

        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "subject"
                )
        );


        // DESCRIPTION

        TableColumn<Interaction, String>
                descriptionColumn =
                new TableColumn<>("Description");

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "description"
                )
        );


        // INTERACTION DATE
        TableColumn<Interaction, LocalDateTime>
                dateColumn =
                new TableColumn<>("Date");

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "interactionDate"
                )
        );

        // ACTIONS
        TableColumn<Interaction, Void>
                actionsColumn =
                new TableColumn<>("Actions");

        actionsColumn.setCellFactory(column -> {

            return new javafx.scene.control.TableCell<>() {

                private final Button editButton =
                        new Button("Edit");

                private final Button deleteButton =
                        new Button("Delete");


                private final HBox actionBox =
                        new HBox(
                                8,
                                editButton,
                                deleteButton
                        );


                {
                    editButton.setStyle(
                            "-fx-background-color: #2563EB;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-cursor: hand;"
                    );


                    deleteButton.setStyle(
                            "-fx-background-color: #DC2626;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-cursor: hand;"
                    );


                    actionBox.setAlignment(
                            Pos.CENTER
                    );


                    // EDIT INTERACTION

                    editButton.setOnAction(event -> {

                        Interaction interaction =
                                getTableView()
                                        .getItems()
                                        .get(getIndex());


                        InteractionForm interactionForm =
                                new InteractionForm(
                                        interaction
                                );


                        interactionForm.setOnInteractionSaved(
                                () -> loadInteractions()
                        );


                        interactionForm.show();
                    });


                    // DELETE INTERACTION

                    deleteButton.setOnAction(event -> {

                        Interaction interaction =
                                getTableView()
                                        .getItems()
                                        .get(getIndex());


                        deleteInteraction(interaction);
                    });
                }


                @Override
                protected void updateItem(
                        Void item,
                        boolean empty
                ) {

                    super.updateItem(
                            item,
                            empty
                    );


                    if (empty) {

                        setGraphic(null);

                    } else {

                        setGraphic(actionBox);
                    }
                }
            };
        });

        customerColumn.setPrefWidth(180);
        typeColumn.setPrefWidth(120);
        subjectColumn.setPrefWidth(180);
        descriptionColumn.setPrefWidth(300);
        dateColumn.setPrefWidth(180);
        actionsColumn.setPrefWidth(180);


        table.getColumns().addAll(
                customerColumn,
                typeColumn,
                subjectColumn,
                descriptionColumn,
                dateColumn,
                actionsColumn
        );


        table.setItems(
                filteredInteractions
        );


        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        return table;
    }

    private void loadInteractions() {
        List<Customer> customers = customerRepository.findAll();

        customerNames.clear();
        for(Customer customer : customers){
            String fullName =
                    customer.getFirstName()
                            + " "
                            + customer.getLastName();

            customerNames.put(
                    customer.getId(),
                    fullName
            );
        };

        List<Interaction> interactionList = interactionRepository.findAll();

        interactions.clear();

        interactions.addAll(interactionList);
        if (searchField != null) {
            filterInteractions(
                    searchField.getText()
            );
        } else {
            filteredInteractions.setAll(
                    interactions
            );
        }
    }

    private void deleteInteraction(
            Interaction interaction
    ) {

        javafx.scene.control.Alert confirmation =
                new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Delete Interaction"
        );


        confirmation.setHeaderText(
                "Are you sure you want to delete this interaction?"
        );


        confirmation.setContentText(
                "This action cannot be undone."
        );


        confirmation.showAndWait().ifPresent(response -> {

            if (response ==
                    javafx.scene.control.ButtonType.OK) {

                interactionRepository.delete(
                        interaction.getId()
                );


                System.out.println(
                        "Interaction deleted successfully!"
                );


                loadInteractions();
            }
        });
    }

    private void filterInteractions(String keyword) {

        if (keyword == null || keyword.isBlank()) {

            filteredInteractions.setAll(interactions);

            return;
        }


        String searchKeyword =
                keyword.toLowerCase().trim();


        filteredInteractions.clear();


        for (Interaction interaction : interactions) {

            String customerName =
                    customerNames.getOrDefault(
                            interaction.getCustomerId(),
                            ""
                    ).toLowerCase();


            String type =
                    interaction.getType() != null
                            ? interaction.getType().toLowerCase()
                            : "";


            String subject =
                    interaction.getSubject() != null
                            ? interaction.getSubject().toLowerCase()
                            : "";


            String description =
                    interaction.getDescription() != null
                            ? interaction.getDescription().toLowerCase()
                            : "";


            if (customerName.contains(searchKeyword)
                    || type.contains(searchKeyword)
                    || subject.contains(searchKeyword)
                    || description.contains(searchKeyword)) {

                filteredInteractions.add(interaction);
            }
        }
    }
}