package com.clientsphere.crm.view;

import org.bson.types.ObjectId;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.InteractionRepository;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InteractionView {

    private final InteractionRepository interactionRepository;
    private final CustomerRepository customerRepository;

    private final Map<ObjectId, String> customerNames;

    private TableView<Interaction> interactionTable;

    private ObservableList<Interaction> interactions;
    private ObservableList<Interaction> filteredInteractions;

    private TextField searchField;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, HH:mm"
            );

    public InteractionView() {

        interactionRepository =
                new InteractionRepository();

        customerRepository =
                new CustomerRepository();

        interactions =
                FXCollections.observableArrayList();

        filteredInteractions =
                FXCollections.observableArrayList();

        customerNames =
                new HashMap<>();
    }

    // =========================================================
    // MAIN VIEW
    // =========================================================

    public BorderPane getView() {

        BorderPane mainLayout =
                new BorderPane();

        VBox content =
                new VBox(25);

        content.setPadding(
                new Insets(30)
        );

        content.setFillWidth(true);

        content.setMinWidth(0);

        content.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // HEADER
        // =====================================================

        FlowPane header =
                createHeader();

        header.setMaxWidth(
                Double.MAX_VALUE
        );

        // =====================================================
        // SEARCH FIELD
        // =====================================================

        searchField =
                new TextField();

        searchField.setPromptText(
                "Search interactions..."
        );

        searchField.setPrefHeight(40);

        searchField.setMaxWidth(
                Double.MAX_VALUE
        );

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        filterInteractions(newValue)
        );

        // =====================================================
        // TABLE
        // =====================================================

        interactionTable =
                createInteractionTable();

        interactionTable.setMaxWidth(
                Double.MAX_VALUE
        );

        interactionTable.setMinWidth(0);

        VBox.setVgrow(
                interactionTable,
                Priority.ALWAYS
        );

        content.getChildren().addAll(
                header,
                searchField,
                interactionTable
        );

        mainLayout.setCenter(
                content
        );

        // =====================================================
        // RESPONSIVE CONTENT PADDING
        // =====================================================

        mainLayout.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    double width =
                            newWidth.doubleValue();

                    if (width <= 0) {
                        return;
                    }

                    if (width < 700) {

                        content.setPadding(
                                new Insets(20)
                        );

                    } else if (width < 1000) {

                        content.setPadding(
                                new Insets(25)
                        );

                    } else {

                        content.setPadding(
                                new Insets(30)
                        );
                    }
                }
        );

        loadInteractions();

        return mainLayout;
    }

    // =========================================================
    // HEADER
    // =========================================================

    private FlowPane createHeader() {

        Label title =
                new Label("Interactions");

        title.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        Label subtitle =
                new Label(
                        "Track and manage customer interactions"
                );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #64748B;"
        );

        VBox titleBox =
                new VBox(5);

        titleBox.getChildren().addAll(
                title,
                subtitle
        );

        // =====================================================
        // ADD INTERACTION BUTTON
        // =====================================================

        Button addInteractionButton =
                new Button("+ Add Interaction");

        addInteractionButton.setPrefHeight(42);

        addInteractionButton.setMinWidth(
                Region.USE_PREF_SIZE
        );

        addInteractionButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );

        addInteractionButton.setOnAction(
                event -> {

                    InteractionForm interactionForm =
                            new InteractionForm();

                    interactionForm.setOnInteractionSaved(
                            this::loadInteractions
                    );

                    interactionForm.show();
                }
        );

        // =====================================================
        // RESPONSIVE HEADER
        // =====================================================

        FlowPane header =
                new FlowPane();

        header.setHgap(20);

        header.setVgap(15);

        header.setAlignment(
                Pos.CENTER_LEFT
        );

        header.setPrefWrapLength(850);

        header.setMaxWidth(
                Double.MAX_VALUE
        );

        header.getChildren().addAll(
                titleBox,
                addInteractionButton
        );

        return header;
    }

    // =========================================================
    // INTERACTION TABLE
    // =========================================================

    private TableView<Interaction>
    createInteractionTable() {

        TableView<Interaction> table =
                new TableView<>();

        // =====================================================
        // CUSTOMER COLUMN
        // =====================================================

        TableColumn<Interaction, String>
                customerColumn =
                new TableColumn<>("Customer");

        customerColumn.setCellValueFactory(
                cellData -> {

                    ObjectId customerId =
                            cellData.getValue()
                                    .getCustomerId();

                    String customerName =
                            customerNames.get(
                                    customerId
                            );

                    return new SimpleStringProperty(
                            customerName != null
                                    ? customerName
                                    : "Unknown Customer"
                    );
                }
        );

        // =====================================================
        // TYPE COLUMN
        // =====================================================

        TableColumn<Interaction, String>
                typeColumn =
                new TableColumn<>("Type");

        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "type"
                )
        );

        // =====================================================
        // SUBJECT COLUMN
        // =====================================================

        TableColumn<Interaction, String>
                subjectColumn =
                new TableColumn<>("Subject");

        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "subject"
                )
        );

        // =====================================================
        // DESCRIPTION COLUMN
        // =====================================================

        TableColumn<Interaction, String>
                descriptionColumn =
                new TableColumn<>("Description");

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "description"
                )
        );

        // =====================================================
        // DATE COLUMN
        // =====================================================

        TableColumn<Interaction, LocalDateTime>
                dateColumn =
                new TableColumn<>("Date");

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "interactionDate"
                )
        );

        /*
         * Format the LocalDateTime instead of displaying the
         * raw Java LocalDateTime value.
         *
         * Example:
         *
         * 2026-09-06T23:00:29.638752500
         *
         * becomes:
         *
         * 06 Sep 2026, 23:00
         */
        dateColumn.setCellFactory(
                column -> new TableCell<>() {

                    @Override
                    protected void updateItem(
                            LocalDateTime date,
                            boolean empty
                    ) {

                        super.updateItem(
                                date,
                                empty
                        );

                        if (empty || date == null) {

                            setText(null);

                        } else {

                            setText(
                                    dateFormatter.format(date)
                            );
                        }
                    }
                }
        );

        // =====================================================
        // ACTIONS COLUMN
        // =====================================================

        TableColumn<Interaction, Void>
                actionsColumn =
                new TableColumn<>("Actions");

        actionsColumn.setCellFactory(
                column -> new TableCell<>() {

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
                        // =============================
                        // EDIT BUTTON
                        // =============================

                        editButton.setStyle(
                                "-fx-background-color: #2563EB;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-size: 12px;" +
                                        "-fx-cursor: hand;"
                        );

                        // =============================
                        // DELETE BUTTON
                        // =============================

                        deleteButton.setStyle(
                                "-fx-background-color: #DC2626;" +
                                        "-fx-text-fill: white;" +
                                        "-fx-font-size: 12px;" +
                                        "-fx-cursor: hand;"
                        );

                        actionBox.setAlignment(
                                Pos.CENTER
                        );

                        // =============================
                        // EDIT ACTION
                        // =============================

                        editButton.setOnAction(
                                event -> {

                                    Interaction interaction =
                                            getTableView()
                                                    .getItems()
                                                    .get(getIndex());

                                    if (interaction == null) {
                                        return;
                                    }

                                    InteractionForm
                                            interactionForm =
                                            new InteractionForm(
                                                    interaction
                                            );

                                    interactionForm
                                            .setOnInteractionSaved(
                                                    this::refreshInteractions
                                            );

                                    interactionForm.show();
                                }
                        );

                        // =============================
                        // DELETE ACTION
                        // =============================

                        deleteButton.setOnAction(
                                event -> {

                                    Interaction interaction =
                                            getTableView()
                                                    .getItems()
                                                    .get(getIndex());

                                    if (interaction == null) {
                                        return;
                                    }

                                    deleteInteraction(
                                            interaction
                                    );
                                }
                        );
                    }

                    private void refreshInteractions() {
                        loadInteractions();
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

                            setGraphic(
                                    actionBox
                            );
                        }
                    }
                }
        );

        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        customerColumn.setMinWidth(160);
        customerColumn.setPrefWidth(180);

        typeColumn.setMinWidth(100);
        typeColumn.setPrefWidth(120);

        subjectColumn.setMinWidth(160);
        subjectColumn.setPrefWidth(180);

        descriptionColumn.setMinWidth(250);
        descriptionColumn.setPrefWidth(300);

        dateColumn.setMinWidth(170);
        dateColumn.setPrefWidth(180);

        actionsColumn.setMinWidth(160);
        actionsColumn.setPrefWidth(180);

        // =====================================================
        // ADD COLUMNS
        // =====================================================

        table.getColumns().addAll(
                customerColumn,
                typeColumn,
                subjectColumn,
                descriptionColumn,
                dateColumn,
                actionsColumn
        );

        // =====================================================
        // TABLE DATA
        // =====================================================

        table.setItems(
                filteredInteractions
        );

        // =====================================================
        // RESPONSIVE TABLE WIDTH
        // =====================================================

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        table.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    double width =
                            newWidth.doubleValue();

                    if (width <= 0) {
                        return;
                    }

                    if (width >= 1100) {

                        table.setColumnResizePolicy(
                                TableView.CONSTRAINED_RESIZE_POLICY
                        );

                    } else {

                        table.setColumnResizePolicy(
                                TableView.UNCONSTRAINED_RESIZE_POLICY
                        );
                    }
                }
        );

        return table;
    }

    // =========================================================
    // LOAD INTERACTIONS
    // =========================================================

    private void loadInteractions() {

        List<Customer> customers =
                customerRepository.findAll();

        customerNames.clear();

        for (Customer customer : customers) {

            String fullName =
                    customer.getFirstName()
                            + " "
                            + customer.getLastName();

            customerNames.put(
                    customer.getId(),
                    fullName
            );
        }

        List<Interaction> interactionList =
                interactionRepository.findAll();

        interactions.clear();

        interactions.addAll(
                interactionList
        );

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

    // =========================================================
    // DELETE INTERACTION
    // =========================================================

    private void deleteInteraction(
            Interaction interaction
    ) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
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

        confirmation.showAndWait().ifPresent(
                response -> {

                    if (response ==
                            ButtonType.OK) {

                        interactionRepository.delete(
                                interaction.getId()
                        );

                        System.out.println(
                                "Interaction deleted successfully!"
                        );

                        loadInteractions();
                    }
                }
        );
    }

    // =========================================================
    // SEARCH / FILTER
    // =========================================================

    private void filterInteractions(
            String keyword
    ) {

        if (
                keyword == null ||
                        keyword.isBlank()
        ) {

            filteredInteractions.setAll(
                    interactions
            );

            return;
        }

        String searchKeyword =
                keyword.toLowerCase()
                        .trim();

        filteredInteractions.clear();

        for (
                Interaction interaction :
                interactions
        ) {

            String customerName =
                    customerNames.getOrDefault(
                            interaction.getCustomerId(),
                            ""
                    ).toLowerCase();

            String type =
                    interaction.getType() != null
                            ? interaction.getType()
                            .toLowerCase()
                            : "";

            String subject =
                    interaction.getSubject() != null
                            ? interaction.getSubject()
                            .toLowerCase()
                            : "";

            String description =
                    interaction.getDescription() != null
                            ? interaction.getDescription()
                            .toLowerCase()
                            : "";

            if (
                    customerName.contains(
                            searchKeyword
                    )
                            ||
                            type.contains(
                                    searchKeyword
                            )
                            ||
                            subject.contains(
                                    searchKeyword
                            )
                            ||
                            description.contains(
                                    searchKeyword
                            )
            ) {

                filteredInteractions.add(
                        interaction
                );
            }
        }
    }
}