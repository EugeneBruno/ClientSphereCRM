package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.repository.CustomerRepository;

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
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;

public class CustomerView {

    private final BorderPane root;

    private final CustomerRepository customerRepository;

    private TableView<Customer> customerTable;

    private ObservableList<Customer> allCustomers;


    public CustomerView() {

        root = new BorderPane();

        customerRepository = new CustomerRepository();

        createCustomerPage();
    }


    private void createCustomerPage() {

        // =========================
        // PAGE TITLE
        // =========================

        Label title = new Label("Customers");

        title.setStyle("""
                -fx-font-size: 32px;
                -fx-font-weight: bold;
                -fx-text-fill: #0F172A;
                """);


        Label subtitle = new Label(
                "Manage and organize your customers"
        );

        subtitle.setWrapText(true);

        subtitle.setStyle("""
                -fx-font-size: 16px;
                -fx-text-fill: #64748B;
                """);


        VBox titleSection = new VBox(5);

        titleSection.getChildren().addAll(
                title,
                subtitle
        );


        // =========================
        // ADD CUSTOMER BUTTON
        // =========================

        Button addCustomerButton =
                new Button("+ Add Customer");

        addCustomerButton.setStyle("""
                -fx-background-color: #2563EB;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 10 18 10 18;
                -fx-cursor: hand;
                -fx-background-radius: 5px;
                """);


        addCustomerButton.setOnAction(event -> {

            CustomerForm customerForm =
                    new CustomerForm();


            customerForm.setOnCustomerSaved(() -> {

                loadCustomers();

            });


            customerForm.show();
        });


        // =========================
        // RESPONSIVE HEADER
        // =========================

        FlowPane header = new FlowPane();

        header.setHgap(20);
        header.setVgap(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPrefWrapLength(700);
        header.setMaxWidth(Double.MAX_VALUE);

        header.getChildren().addAll(
                titleSection,
                addCustomerButton
        );


        // =========================
        // SEARCH FIELD
        // =========================

        TextField searchField =
                new TextField();

        searchField.setPromptText(
                "Search customers..."
        );

        searchField.setMaxWidth(Double.MAX_VALUE);

        searchField.setPrefHeight(38);

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    searchCustomers(newValue);

                }
        );


        // =========================
        // CUSTOMER TABLE
        // =========================

        customerTable = new TableView<>();

        customerTable.setPlaceholder(
                new Label("No customers found.")
        );

        customerTable.setColumnResizePolicy(
                TableView.UNCONSTRAINED_RESIZE_POLICY
        );

        customerTable.setFixedCellSize(55);

        customerTable.setMinHeight(250);

        customerTable.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #CBD5E1;
                -fx-border-radius: 5px;
                """);


        // =========================
        // FIRST NAME COLUMN
        // =========================

        TableColumn<Customer, String> firstNameColumn =
                new TableColumn<>("First Name");

        firstNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("firstName")
        );

        firstNameColumn.setMinWidth(150);
        firstNameColumn.setPrefWidth(170);


        // =========================
        // LAST NAME COLUMN
        // =========================

        TableColumn<Customer, String> lastNameColumn =
                new TableColumn<>("Last Name");

        lastNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastName")
        );

        lastNameColumn.setMinWidth(150);
        lastNameColumn.setPrefWidth(170);


        // =========================
        // EMAIL COLUMN
        // =========================

        TableColumn<Customer, String> emailColumn =
                new TableColumn<>("Email");

        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );

        emailColumn.setMinWidth(230);
        emailColumn.setPrefWidth(250);


        // =========================
        // PHONE COLUMN
        // =========================

        TableColumn<Customer, String> phoneColumn =
                new TableColumn<>("Phone");

        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );

        phoneColumn.setMinWidth(150);
        phoneColumn.setPrefWidth(170);


        // =========================
        // COMPANY COLUMN
        // =========================

        TableColumn<Customer, String> companyColumn =
                new TableColumn<>("Company");

        companyColumn.setCellValueFactory(
                new PropertyValueFactory<>("company")
        );

        companyColumn.setMinWidth(180);
        companyColumn.setPrefWidth(200);


        // =========================
        // STATUS COLUMN
        // =========================

        TableColumn<Customer, String> statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        statusColumn.setMinWidth(180);
        statusColumn.setPrefWidth(200);


        // =========================
        // ACTIONS COLUMN
        // =========================

        TableColumn<Customer, Customer> actionsColumn =
                new TableColumn<>("Actions");

        actionsColumn.setCellValueFactory(cellData ->
                new javafx.beans.property.ReadOnlyObjectWrapper<>(
                        cellData.getValue()
                )
        );

        actionsColumn.setMinWidth(250);
        actionsColumn.setPrefWidth(270);


        actionsColumn.setCellFactory(column -> {

            return new TableCell<>() {

                private final Button editButton =
                        new Button("Edit");

                private final Button deleteButton =
                        new Button("Delete");

                private final Button historyButton =
                        new Button("History");

                private final HBox actionButtons =
                        new HBox(8);


                {
                    // EDIT BUTTON

                    editButton.setStyle("""
                            -fx-background-color: #2563EB;
                            -fx-text-fill: white;
                            -fx-font-size: 12px;
                            -fx-padding: 6 10 6 10;
                            -fx-cursor: hand;
                            -fx-background-radius: 4px;
                            """);


                    // DELETE BUTTON

                    deleteButton.setStyle("""
                            -fx-background-color: #DC2626;
                            -fx-text-fill: white;
                            -fx-font-size: 12px;
                            -fx-padding: 6 10 6 10;
                            -fx-cursor: hand;
                            -fx-background-radius: 4px;
                            """);


                    // HISTORY BUTTON

                    historyButton.setStyle("""
                            -fx-background-color: #7C3AED;
                            -fx-text-fill: white;
                            -fx-font-size: 12px;
                            -fx-padding: 6 10 6 10;
                            -fx-cursor: hand;
                            -fx-background-radius: 4px;
                            """);


                    actionButtons.setAlignment(Pos.CENTER_LEFT);

                    actionButtons.getChildren().addAll(
                            editButton,
                            deleteButton,
                            historyButton
                    );


                    // EDIT ACTION

                    editButton.setOnAction(event -> {

                        Customer customer = getItem();

                        if (customer != null) {

                            openEditForm(customer);

                        }
                    });


                    // DELETE ACTION

                    deleteButton.setOnAction(event -> {

                        Customer customer = getItem();

                        if (customer != null) {

                            deleteCustomer(customer);

                        }
                    });


                    // HISTORY ACTION

                    historyButton.setOnAction(event -> {

                        Customer customer = getItem();

                        if (customer != null) {

                            CustomerInteractionHistory historyView =
                                    new CustomerInteractionHistory(customer);

                            historyView.show();

                        }
                    });
                }


                @Override
                protected void updateItem(
                        Customer customer,
                        boolean empty
                ) {

                    super.updateItem(
                            customer,
                            empty
                    );


                    if (empty || customer == null) {

                        setGraphic(null);

                    } else {

                        setGraphic(actionButtons);
                    }
                }
            };
        });


        // =========================
        // ADD COLUMNS TO TABLE
        // =========================

        customerTable.getColumns().addAll(

                firstNameColumn,
                lastNameColumn,
                emailColumn,
                phoneColumn,
                companyColumn,
                statusColumn,
                actionsColumn
        );


        // =========================
        // LOAD CUSTOMERS
        // =========================

        loadCustomers();


        // =========================
        // MAIN CONTENT
        // =========================

        VBox content = new VBox(25);

        content.setPadding(
                new Insets(30)
        );

        content.setFillWidth(true);

        content.setMinWidth(0);

        content.setMaxWidth(Double.MAX_VALUE);

        content.setStyle("""
                -fx-background-color: #F8FAFC;
                """);


        VBox.setVgrow(
                customerTable,
                Priority.ALWAYS
        );


        content.getChildren().addAll(
                header,
                searchField,
                customerTable
        );


        // =========================
        // ROOT LAYOUT
        // =========================

        root.setCenter(content);
    }


    // =========================
    // LOAD CUSTOMERS FROM DATABASE
    // =========================

    private void loadCustomers() {

        List<Customer> customers =
                customerRepository.findAll();


        allCustomers =
                FXCollections.observableArrayList(
                        customers
                );


        customerTable.setItems(
                allCustomers
        );
    }


    // =========================
    // SEARCH CUSTOMERS
    // =========================

    private void searchCustomers(String keyword) {

        if (
                keyword == null ||
                        keyword.trim().isEmpty()
        ) {

            customerTable.setItems(
                    allCustomers
            );

            return;
        }


        String searchKeyword =
                keyword.trim().toLowerCase();


        ObservableList<Customer> filteredCustomers =
                FXCollections.observableArrayList();


        for (Customer customer : allCustomers) {

            boolean matches =

                    safeString(customer.getFirstName())
                            .toLowerCase()
                            .contains(searchKeyword)

                            ||

                            safeString(customer.getLastName())
                                    .toLowerCase()
                                    .contains(searchKeyword)

                            ||

                            safeString(customer.getEmail())
                                    .toLowerCase()
                                    .contains(searchKeyword)

                            ||

                            safeString(customer.getPhone())
                                    .toLowerCase()
                                    .contains(searchKeyword)

                            ||

                            safeString(customer.getCompany())
                                    .toLowerCase()
                                    .contains(searchKeyword)

                            ||

                            safeString(customer.getStatus())
                                    .toLowerCase()
                                    .contains(searchKeyword);


            if (matches) {

                filteredCustomers.add(
                        customer
                );
            }
        }


        customerTable.setItems(
                filteredCustomers
        );
    }


    // =========================
    // NULL-SAFE STRING HELPER
    // =========================

    private String safeString(String value) {

        return value == null ? "" : value;
    }


    // =========================
    // EDIT CUSTOMER
    // =========================

    private void openEditForm(Customer customer) {

        CustomerForm customerForm =
                new CustomerForm(customer);


        customerForm.setOnCustomerSaved(() -> {

            loadCustomers();

        });


        customerForm.show();
    }


    // =========================
    // DELETE CUSTOMER
    // =========================

    private void deleteCustomer(Customer customer) {

        Alert confirmation =
                new Alert(
                        Alert.AlertType.CONFIRMATION
                );


        confirmation.setTitle(
                "Delete Customer"
        );


        confirmation.setHeaderText(
                "Delete "
                        + safeString(customer.getFirstName())
                        + " "
                        + safeString(customer.getLastName())
                        + "?"
        );


        confirmation.setContentText(
                "Are you sure you want to delete this customer? "
                        + "This action cannot be undone."
        );


        Optional<ButtonType> result =
                confirmation.showAndWait();


        if (
                result.isPresent()
                        &&
                        result.get() == ButtonType.OK
        ) {

            customerRepository.delete(
                    customer.getId()
            );


            System.out.println(
                    "Customer deleted successfully!"
            );


            loadCustomers();
        }
    }


    // =========================
    // GET ROOT
    // =========================

    public BorderPane getRoot() {

        return root;
    }
}