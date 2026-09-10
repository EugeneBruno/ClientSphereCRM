package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.repository.CustomerRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.geometry.Insets;

import javafx.beans.property.ReadOnlyObjectWrapper;
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
                """);


        Label subtitle = new Label(
                "Manage and organize your customers"
        );

        subtitle.setStyle("""
                -fx-font-size: 16px;
                -fx-text-fill: #64748b;
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
                -fx-background-color: #2563eb;
                -fx-text-fill: white;
                -fx-font-size: 14px;
                -fx-font-weight: bold;
                -fx-padding: 10 18 10 18;
                -fx-cursor: hand;
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
        // HEADER SECTION
        // =========================

        HBox header = new HBox();

        HBox spacer = new HBox();

        HBox.setHgrow(
                spacer,
                Priority.ALWAYS
        );


        header.getChildren().addAll(
                titleSection,
                spacer,
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


        searchField.setPrefWidth(350);


        searchField.textProperty().addListener(
                (observable, oldValue, newValue) -> {

                    searchCustomers(newValue);

                }
        );


        // =========================
        // CUSTOMER TABLE
        // =========================

        customerTable = new TableView<>();


        // FIRST NAME COLUMN

        TableColumn<Customer, String> firstNameColumn =
                new TableColumn<>("First Name");


        firstNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("firstName")
        );


        // LAST NAME COLUMN

        TableColumn<Customer, String> lastNameColumn =
                new TableColumn<>("Last Name");


        lastNameColumn.setCellValueFactory(
                new PropertyValueFactory<>("lastName")
        );


        // EMAIL COLUMN

        TableColumn<Customer, String> emailColumn =
                new TableColumn<>("Email");


        emailColumn.setCellValueFactory(
                new PropertyValueFactory<>("email")
        );


        // PHONE COLUMN

        TableColumn<Customer, String> phoneColumn =
                new TableColumn<>("Phone");


        phoneColumn.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );


        // COMPANY COLUMN

        TableColumn<Customer, String> companyColumn =
                new TableColumn<>("Company");


        companyColumn.setCellValueFactory(
                new PropertyValueFactory<>("company")
        );


        // STATUS COLUMN

        TableColumn<Customer, String> statusColumn =
                new TableColumn<>("Status");


        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );

        // =========================
// ACTIONS COLUMN
// =========================

        TableColumn<Customer, Customer> actionsColumn =
                new TableColumn<>("Actions");


        actionsColumn.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(
                        cellData.getValue()
                )
        );


        actionsColumn.setCellFactory(column -> {

            return new TableCell<>() {

                private final Button editButton =
                        new Button("Edit");

                private final Button deleteButton =
                        new Button("Delete");

                private final Button historyButton = new Button("History");

                private final HBox actionButtons =
                        new HBox(8);


                {
                    editButton.setStyle(
                            "-fx-background-color: #2563EB;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-cursor: hand;"
                    );

                    deleteButton.setStyle(
                            "-fx-background-color: #DC2626;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-font-size: 12px;" +
                                    "-fx-cursor: hand;"
                    );

                    historyButton.setStyle(
                            "-fx-background-color: #7C3AED;" +
                                    "-fx-text-fill: white;" +
                                    "-fx-cursor: hand;"
                    );

                    actionButtons.getChildren().addAll(
                            editButton,
                            deleteButton,
                            historyButton
                    );


                    editButton.setOnAction(event -> {

                        Customer customer = getItem();


                        if (customer != null) {

                            openEditForm(customer);

                        }
                    });


                    deleteButton.setOnAction(event -> {

                        Customer customer = getItem();


                        if (customer != null) {

                            deleteCustomer(customer);

                        }
                    });
                    historyButton.setOnAction(event -> {

                    Customer customer =
                            getTableView()
                                    .getItems()
                                    .get(getIndex());


                    CustomerInteractionHistory historyView =
                            new CustomerInteractionHistory(customer);


                    historyView.show();
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


        actionsColumn.setPrefWidth(170);

        actionsColumn.setMinWidth(170);


        actionsColumn.setPrefWidth(170);
        actionsColumn.setMinWidth(170);

        // ADD COLUMNS TO TABLE
        customerTable.getColumns().addAll(

                firstNameColumn,

                lastNameColumn,

                emailColumn,

                phoneColumn,

                companyColumn,

                statusColumn,

                actionsColumn
        );


        // Make table use available width
        customerTable.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );

        // LOAD CUSTOMERS
        loadCustomers();

        // MAIN CONTENT
        VBox content = new VBox(25);

        content.setPadding(
                new Insets(40)
        );

        VBox.setVgrow(
                customerTable,
                Priority.ALWAYS
        );

        content.getChildren().addAll(
                header,
                searchField,
                customerTable
        );
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
                keyword.toLowerCase();


        ObservableList<Customer> filteredCustomers =
                FXCollections.observableArrayList();


        for (Customer customer : allCustomers) {


            boolean matches =


                    customer.getFirstName()
                            .toLowerCase()
                            .contains(searchKeyword)


                            ||


                            customer.getLastName()
                                    .toLowerCase()
                                    .contains(searchKeyword)


                            ||


                            customer.getEmail()
                                    .toLowerCase()
                                    .contains(searchKeyword)


                            ||


                            customer.getPhone()
                                    .toLowerCase()
                                    .contains(searchKeyword)


                            ||


                            customer.getCompany()
                                    .toLowerCase()
                                    .contains(searchKeyword)


                            ||


                            customer.getStatus()
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
                        + customer.getFirstName()
                        + " "
                        + customer.getLastName()
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