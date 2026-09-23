package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Deal;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.DealRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
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

import java.util.stream.Collectors;


public class DealView {

    private final BorderPane root;

    private final DealRepository dealRepository;
    private final CustomerRepository customerRepository;

    private final TableView<Deal> dealTable;
    private final ObservableList<Deal> dealList;

    private final TextField searchField;
    private final ComboBox<String> statusFilter;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy");


    public DealView() {

        root = new BorderPane();

        dealRepository =
                new DealRepository();

        customerRepository =
                new CustomerRepository();

        dealTable =
                new TableView<>();

        dealList =
                FXCollections.observableArrayList();

        searchField =
                new TextField();

        statusFilter =
                new ComboBox<>();

        createView();

        loadDeals();
    }


    // =========================================================
    // MAIN VIEW
    // =========================================================

    private void createView() {

        // =====================================================
        // TITLE
        // =====================================================

        Label title =
                new Label("Deals");

        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );


        // =====================================================
        // SUBTITLE
        // =====================================================

        Label subtitle =
                new Label(
                        "Manage sales opportunities and track deal progress"
                );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );


        VBox titleSection =
                new VBox(5);

        titleSection.getChildren().addAll(
                title,
                subtitle
        );


        // =====================================================
        // SEARCH FIELD
        // =====================================================

        searchField.setPromptText(
                "Search by customer, title, or description..."
        );

        searchField.setPrefHeight(40);

        searchField.setPrefWidth(350);

        searchField.setMinWidth(180);

        searchField.setMaxWidth(
                Double.MAX_VALUE
        );


        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        filterDeals()
        );


        // =====================================================
        // STATUS FILTER
        // =====================================================

        statusFilter.setItems(
                FXCollections.observableArrayList(
                        "All Statuses",
                        "Open",
                        "Won",
                        "Lost"
                )
        );

        statusFilter.setValue(
                "All Statuses"
        );

        statusFilter.setPrefWidth(150);

        statusFilter.setMinWidth(140);

        statusFilter.setPrefHeight(40);

        statusFilter.setOnAction(
                event -> filterDeals()
        );


        // =====================================================
        // ADD DEAL BUTTON
        // =====================================================

        Button addButton =
                new Button("Add Deal");

        addButton.setPrefHeight(40);

        addButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );


        addButton.setOnAction(
                event -> {

                    DealForm dealForm =
                            new DealForm();

                    dealForm.setOnDealSaved(
                            this::loadDeals
                    );

                    dealForm.show();
                }
        );


        // =====================================================
        // RESPONSIVE SEARCH / FILTER BAR
        // =====================================================

        FlowPane searchBar =
                new FlowPane();

        searchBar.setHgap(10);

        searchBar.setVgap(10);

        searchBar.setAlignment(
                Pos.CENTER_LEFT
        );

        searchBar.setMaxWidth(
                Double.MAX_VALUE
        );


        searchBar.getChildren().addAll(
                searchField,
                statusFilter,
                addButton
        );


        // =====================================================
        // TABLE
        // =====================================================

        createTableColumns();

        dealTable.setItems(
                dealList
        );

        dealTable.setMinWidth(0);

        dealTable.setMaxWidth(
                Double.MAX_VALUE
        );


        VBox.setVgrow(
                dealTable,
                Priority.ALWAYS
        );


        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content =
                new VBox(20);

        content.setPadding(
                new Insets(30)
        );

        content.setFillWidth(true);

        content.setMinWidth(0);

        content.setMaxWidth(
                Double.MAX_VALUE
        );

        content.setStyle(
                "-fx-background-color: #F8FAFC;"
        );


        content.getChildren().addAll(
                titleSection,
                searchBar,
                dealTable
        );


        root.setCenter(
                content
        );


        // =====================================================
        // RESPONSIVE PADDING
        // =====================================================

        root.widthProperty().addListener(
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


        // =====================================================
        // RESPONSIVE SEARCH FIELD
        // =====================================================

        searchBar.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    double width =
                            newWidth.doubleValue();

                    if (width <= 0) {
                        return;
                    }


                    /*
                     * On larger screens the search field
                     * gets most of the available toolbar width.
                     *
                     * On smaller screens it becomes a
                     * full-width row and the other controls
                     * wrap underneath it.
                     */

                    if (width >= 700) {

                        double searchWidth =
                                width
                                        - 150
                                        - 100
                                        - 30;

                        searchField.setPrefWidth(
                                Math.max(
                                        300,
                                        searchWidth
                                )
                        );

                    } else {

                        searchField.setPrefWidth(
                                Math.max(
                                        180,
                                        width
                                )
                        );
                    }
                }
        );
    }


    // =========================================================
    // TABLE COLUMNS
    // =========================================================

    private void createTableColumns() {

        // =====================================================
        // CUSTOMER
        // =====================================================

        TableColumn<Deal, Object>
                customerColumn =
                new TableColumn<>("Customer");


        customerColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "customerId"
                )
        );


        Map<String, String> customerNames =
                getCustomerNames();


        customerColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    Object customerId,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        customerId,
                                        empty
                                );


                                if (
                                        empty ||
                                                customerId == null
                                ) {

                                    setText(null);

                                } else {

                                    setText(
                                            customerNames
                                                    .getOrDefault(
                                                            customerId
                                                                    .toString(),
                                                            "Unknown Customer"
                                                    )
                                    );
                                }
                            }
                        }
        );


        // =====================================================
        // TITLE
        // =====================================================

        TableColumn<Deal, String>
                titleColumn =
                new TableColumn<>("Title");


        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "title"
                )
        );


        // =====================================================
        // DESCRIPTION
        // =====================================================

        TableColumn<Deal, String>
                descriptionColumn =
                new TableColumn<>("Description");


        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "description"
                )
        );


        // =====================================================
        // VALUE
        // =====================================================

        TableColumn<Deal, Double>
                valueColumn =
                new TableColumn<>("Value");


        valueColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "value"
                )
        );


        valueColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    Double value,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        value,
                                        empty
                                );


                                if (
                                        empty ||
                                                value == null
                                ) {

                                    setText(null);

                                } else {

                                    setText(
                                            String.format(
                                                    "₦%,.2f",
                                                    value
                                            )
                                    );
                                }
                            }
                        }
        );


        // =====================================================
        // STATUS
        // =====================================================

        TableColumn<Deal, String>
                statusColumn =
                new TableColumn<>("Status");


        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );


        // =====================================================
        // EXPECTED CLOSE
        // =====================================================

        TableColumn<Deal, Object>
                closeDateColumn =
                new TableColumn<>("Expected Close");


        closeDateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "expectedCloseDate"
                )
        );


        closeDateColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    Object date,
                                    boolean empty
                            ) {

                                super.updateItem(
                                        date,
                                        empty
                                );


                                if (
                                        empty ||
                                                date == null
                                ) {

                                    setText(null);

                                } else {

                                    setText(
                                            (
                                                    (LocalDateTime) date
                                            ).format(
                                                    dateFormatter
                                            )
                                    );
                                }
                            }
                        }
        );


        // =====================================================
        // ACTIONS
        // =====================================================

        TableColumn<Deal, Void>
                actionsColumn =
                new TableColumn<>("Actions");


        actionsColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            private final Button editButton =
                                    new Button("Edit");

                            private final Button deleteButton =
                                    new Button("Delete");

                            private final HBox actionButtons =
                                    new HBox(
                                            8,
                                            editButton,
                                            deleteButton
                                    );


                            {
                                // =================================
                                // EDIT BUTTON
                                // =================================

                                editButton.setStyle(
                                        "-fx-background-color: #DBEAFE;" +
                                                "-fx-text-fill: #1D4ED8;" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-cursor: hand;"
                                );


                                // =================================
                                // DELETE BUTTON
                                // =================================

                                deleteButton.setStyle(
                                        "-fx-background-color: #FEE2E2;" +
                                                "-fx-text-fill: #B91C1C;" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-cursor: hand;"
                                );


                                actionButtons.setAlignment(
                                        Pos.CENTER
                                );


                                // =================================
                                // EDIT ACTION
                                // =================================

                                editButton.setOnAction(
                                        event -> {

                                            Deal deal =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );


                                            if (deal == null) {
                                                return;
                                            }


                                            DealForm dealForm =
                                                    new DealForm(
                                                            deal
                                                    );


                                            dealForm.setOnDealSaved(
                                                    DealView.this::loadDeals
                                            );


                                            dealForm.show();
                                        }
                                );


                                // =================================
                                // DELETE ACTION
                                // =================================

                                deleteButton.setOnAction(
                                        event -> {

                                            Deal deal =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );


                                            if (deal == null) {
                                                return;
                                            }


                                            confirmDelete(
                                                    deal
                                            );
                                        }
                                );
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
                                            actionButtons
                                    );
                                }
                            }
                        }
        );


        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        customerColumn.setMinWidth(150);
        customerColumn.setPrefWidth(170);

        titleColumn.setMinWidth(150);
        titleColumn.setPrefWidth(170);

        descriptionColumn.setMinWidth(220);
        descriptionColumn.setPrefWidth(250);

        valueColumn.setMinWidth(120);
        valueColumn.setPrefWidth(140);

        statusColumn.setMinWidth(100);
        statusColumn.setPrefWidth(110);

        closeDateColumn.setMinWidth(150);
        closeDateColumn.setPrefWidth(160);

        actionsColumn.setMinWidth(150);
        actionsColumn.setPrefWidth(160);


        // =====================================================
        // ADD COLUMNS
        // =====================================================

        dealTable.getColumns().addAll(
                customerColumn,
                titleColumn,
                descriptionColumn,
                valueColumn,
                statusColumn,
                closeDateColumn,
                actionsColumn
        );


        // =====================================================
        // RESPONSIVE TABLE RESIZING
        // =====================================================

        dealTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        dealTable.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    double width =
                            newWidth.doubleValue();

                    if (width <= 0) {
                        return;
                    }


                    /*
                     * Large screen:
                     * All columns share the available width.
                     *
                     * Medium/small screen:
                     * Keep each column's minimum width.
                     * The TableView itself handles horizontal
                     * scrolling.
                     */

                    if (width >= 1150) {

                        dealTable.setColumnResizePolicy(
                                TableView.CONSTRAINED_RESIZE_POLICY
                        );

                    } else {

                        dealTable.setColumnResizePolicy(
                                TableView.UNCONSTRAINED_RESIZE_POLICY
                        );
                    }
                }
        );
    }


    // =========================================================
    // LOAD DEALS
    // =========================================================

    private void loadDeals() {

        dealList.setAll(
                dealRepository.findAll()
        );


        /*
         * Keep the current search/filter state after
         * adding or editing a deal.
         */

        if (
                !searchField.getText()
                        .trim()
                        .isEmpty()
                        ||
                        (
                                statusFilter.getValue() != null
                                        &&
                                        !statusFilter
                                                .getValue()
                                                .equals(
                                                        "All Statuses"
                                                )
                        )
        ) {

            filterDeals();
        }
    }


    // =========================================================
    // FILTER DEALS
    // =========================================================

    private void filterDeals() {

        String searchText =
                searchField.getText()
                        .trim()
                        .toLowerCase();


        String selectedStatus =
                statusFilter.getValue();


        Map<String, String> customerNames =
                getCustomerNames();


        List<Deal> filteredDeals =
                dealRepository.findAll()
                        .stream()
                        .filter(deal -> {

                            String customerName =
                                    deal.getCustomerId() != null
                                            ? customerNames.getOrDefault(
                                            deal.getCustomerId()
                                                    .toString(),
                                            ""
                                    )
                                            : "";


                            String title =
                                    deal.getTitle() != null
                                            ? deal.getTitle()
                                            : "";


                            String description =
                                    deal.getDescription() != null
                                            ? deal.getDescription()
                                            : "";


                            String searchableText =
                                    customerName
                                            + " "
                                            + title
                                            + " "
                                            + description;


                            boolean matchesSearch =
                                    searchableText
                                            .toLowerCase()
                                            .contains(
                                                    searchText
                                            );


                            boolean matchesStatus =
                                    selectedStatus == null
                                            ||
                                            selectedStatus.equals(
                                                    "All Statuses"
                                            )
                                            ||
                                            (
                                                    deal.getStatus() != null
                                                            &&
                                                            selectedStatus
                                                                    .equalsIgnoreCase(
                                                                            deal.getStatus()
                                                                    )
                                            );


                            return matchesSearch
                                    &&
                                    matchesStatus;
                        })
                        .collect(
                                Collectors.toList()
                        );


        dealList.setAll(
                filteredDeals
        );
    }


    // =========================================================
    // CUSTOMER NAMES
    // =========================================================

    private Map<String, String>
    getCustomerNames() {

        Map<String, String>
                customerNames =
                new HashMap<>();


        List<Customer> customers =
                customerRepository.findAll();


        for (
                Customer customer :
                customers
        ) {

            String fullName =
                    customer.getFirstName()
                            + " "
                            + customer.getLastName();


            customerNames.put(
                    customer.getId().toString(),
                    fullName
            );
        }


        return customerNames;
    }


    // =========================================================
    // DELETE DEAL
    // =========================================================

    private void confirmDelete(
            Deal deal
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.CONFIRMATION,
                        "Are you sure you want to delete this deal?",
                        ButtonType.YES,
                        ButtonType.NO
                );


        alert.setTitle(
                "Delete Deal"
        );


        alert.setHeaderText(
                deal.getTitle()
        );


        alert.showAndWait().ifPresent(
                response -> {

                    if (
                            response ==
                                    ButtonType.YES
                    ) {

                        dealRepository.delete(
                                deal.getId()
                        );


                        loadDeals();
                    }
                }
        );
    }


    // =========================================================
    // GET VIEW
    // =========================================================

    public BorderPane getView() {

        return root;
    }
}