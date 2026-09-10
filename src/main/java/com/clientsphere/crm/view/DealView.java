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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

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

        dealRepository = new DealRepository();
        customerRepository = new CustomerRepository();

        dealTable = new TableView<>();
        dealList = FXCollections.observableArrayList();

        searchField = new TextField();
        statusFilter = new ComboBox<>();

        createView();
        loadDeals();
    }

    private void createView() {
        Label title = new Label("Deals");
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        Label subtitle = new Label(
                "Manage sales opportunities and track deal progress"
        );
        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        searchField.setPromptText(
                "Search by customer, title, or description..."
        );
        searchField.setPrefWidth(350);
        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> filterDeals()
        );

        statusFilter.setItems(
                FXCollections.observableArrayList(
                        "All Statuses",
                        "Open",
                        "Won",
                        "Lost"
                )
        );
        statusFilter.setValue("All Statuses");
        statusFilter.setPrefWidth(150);
        statusFilter.setOnAction(event -> filterDeals());

        Button addButton = new Button("Add Deal");
        addButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        addButton.setOnAction(event -> {
            DealForm dealForm = new DealForm();
            dealForm.setOnDealSaved(this::loadDeals);
            dealForm.show();
        });

        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.getChildren().addAll(
                searchField,
                statusFilter,
                addButton
        );

        HBox.setHgrow(searchField, Priority.ALWAYS);

        createTableColumns();

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #F8FAFC;");

        content.getChildren().addAll(
                title,
                subtitle,
                searchBar,
                dealTable
        );

        VBox.setVgrow(dealTable, Priority.ALWAYS);

        root.setCenter(content);
    }

    private void createTableColumns() {
        TableColumn<Deal, Object> customerColumn =
                new TableColumn<>("Customer");

        customerColumn.setCellValueFactory(
                new PropertyValueFactory<>("customerId")
        );

        Map<String, String> customerNames = getCustomerNames();

        customerColumn.setCellFactory(column ->
                new TableCell<>() {
                    @Override
                    protected void updateItem(
                            Object customerId,
                            boolean empty
                    ) {
                        super.updateItem(customerId, empty);

                        if (empty || customerId == null) {
                            setText(null);
                        } else {
                            setText(
                                    customerNames.getOrDefault(
                                            customerId.toString(),
                                            "Unknown Customer"
                                    )
                            );
                        }
                    }
                }
        );

        TableColumn<Deal, String> titleColumn =
                new TableColumn<>("Title");

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>("title")
        );

        TableColumn<Deal, String> descriptionColumn =
                new TableColumn<>("Description");

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>("description")
        );

        TableColumn<Deal, Double> valueColumn =
                new TableColumn<>("Value");

        valueColumn.setCellValueFactory(
                new PropertyValueFactory<>("value")
        );

        valueColumn.setCellFactory(column ->
                new TableCell<>() {
                    @Override
                    protected void updateItem(
                            Double value,
                            boolean empty
                    ) {
                        super.updateItem(value, empty);

                        if (empty || value == null) {
                            setText(null);
                        } else {
                            setText(String.format("₦%,.2f", value));
                        }
                    }
                }
        );

        TableColumn<Deal, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        TableColumn<Deal, Object> closeDateColumn =
                new TableColumn<>("Expected Close");

        closeDateColumn.setCellValueFactory(
                new PropertyValueFactory<>("expectedCloseDate")
        );

        closeDateColumn.setCellFactory(column ->
                new TableCell<>() {
                    @Override
                    protected void updateItem(
                            Object date,
                            boolean empty
                    ) {
                        super.updateItem(date, empty);

                        if (empty || date == null) {
                            setText(null);
                        } else {
                            setText(
                                    ((java.time.LocalDateTime) date)
                                            .format(dateFormatter)
                            );
                        }
                    }
                }
        );

        TableColumn<Deal, Void> actionsColumn =
                new TableColumn<>("Actions");

        actionsColumn.setCellFactory(column ->
                new TableCell<>() {

                    private final Button editButton =
                            new Button("Edit");

                    private final Button deleteButton =
                            new Button("Delete");

                    private final HBox actionButtons =
                            new HBox(8, editButton, deleteButton);

                    {
                        editButton.setStyle(
                                "-fx-background-color: #DBEAFE;" +
                                        "-fx-text-fill: #1D4ED8;" +
                                        "-fx-cursor: hand;"
                        );

                        deleteButton.setStyle(
                                "-fx-background-color: #FEE2E2;" +
                                        "-fx-text-fill: #B91C1C;" +
                                        "-fx-cursor: hand;"
                        );

                        editButton.setOnAction(event -> {
                            Deal deal = getTableView()
                                    .getItems()
                                    .get(getIndex());

                            DealForm dealForm = new DealForm(deal);
                            dealForm.setOnDealSaved(
                                    DealView.this::loadDeals
                            );
                            dealForm.show();
                        });

                        deleteButton.setOnAction(event -> {
                            Deal deal = getTableView()
                                    .getItems()
                                    .get(getIndex());

                            confirmDelete(deal);
                        });
                    }

                    @Override
                    protected void updateItem(
                            Void item,
                            boolean empty
                    ) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(actionButtons);
                        }
                    }
                }
        );

        customerColumn.setPrefWidth(150);
        titleColumn.setPrefWidth(160);
        descriptionColumn.setPrefWidth(220);
        valueColumn.setPrefWidth(120);
        statusColumn.setPrefWidth(100);
        closeDateColumn.setPrefWidth(150);
        actionsColumn.setPrefWidth(150);

        dealTable.getColumns().addAll(
                customerColumn,
                titleColumn,
                descriptionColumn,
                valueColumn,
                statusColumn,
                closeDateColumn,
                actionsColumn
        );

        dealTable.setItems(dealList);
        dealTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }

    private void loadDeals() {
        dealList.setAll(dealRepository.findAll());
    }

    private void filterDeals() {
        String searchText = searchField.getText()
                .trim()
                .toLowerCase();

        String selectedStatus = statusFilter.getValue();

        Map<String, String> customerNames = getCustomerNames();

        List<Deal> filteredDeals = dealRepository.findAll()
                .stream()
                .filter(deal -> {
                    String customerName = customerNames.getOrDefault(
                            deal.getCustomerId().toString(),
                            ""
                    );

                    String searchableText =
                            customerName + " " +
                                    deal.getTitle() + " " +
                                    deal.getDescription();

                    boolean matchesSearch =
                            searchableText.toLowerCase()
                                    .contains(searchText);

                    boolean matchesStatus =
                            selectedStatus == null ||
                                    selectedStatus.equals("All Statuses") ||
                                    selectedStatus.equalsIgnoreCase(
                                            deal.getStatus()
                                    );

                    return matchesSearch && matchesStatus;
                })
                .collect(Collectors.toList());

        dealList.setAll(filteredDeals);
    }

    private Map<String, String> getCustomerNames() {
        Map<String, String> customerNames = new HashMap<>();

        List<Customer> customers = customerRepository.findAll();

        for (Customer customer : customers) {
            String fullName = customer.getFirstName()
                    + " "
                    + customer.getLastName();

            customerNames.put(
                    customer.getId().toString(),
                    fullName
            );
        }

        return customerNames;
    }

    private void confirmDelete(Deal deal) {
        Alert alert = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this deal?",
                ButtonType.YES,
                ButtonType.NO
        );

        alert.setTitle("Delete Deal");
        alert.setHeaderText(deal.getTitle());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                dealRepository.delete(deal.getId());
                loadDeals();
            }
        });
    }

    public BorderPane getView() {
        return root;
    }
}