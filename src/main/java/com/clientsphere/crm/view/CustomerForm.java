package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.repository.CustomerRepository;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class CustomerForm {

    private final Stage stage;

    private final CustomerRepository customerRepository;

    private Runnable onCustomerSaved;

    private Customer customerToEdit;

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField emailField;
    private TextField phoneField;
    private TextField companyField;

    private ComboBox<String> statusComboBox;


    // =========================
    // ADD CUSTOMER CONSTRUCTOR
    // =========================

    public CustomerForm() {

        stage = new Stage();

        customerRepository = new CustomerRepository();

        createForm();
    }


    // =========================
    // EDIT CUSTOMER CONSTRUCTOR
    // =========================

    public CustomerForm(Customer customer) {

        stage = new Stage();

        customerRepository = new CustomerRepository();

        customerToEdit = customer;

        createForm();
    }


    // =========================
    // CREATE FORM
    // =========================

    private void createForm() {

        Label title = new Label(
                customerToEdit == null
                        ? "Add New Customer"
                        : "Edit Customer"
        );

        title.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;"
        );


        // =========================
        // FIRST NAME
        // =========================

        Label firstNameLabel =
                new Label("First Name");

        firstNameField = new TextField();

        firstNameField.setPromptText(
                "Enter first name"
        );


        // =========================
        // LAST NAME
        // =========================

        Label lastNameLabel =
                new Label("Last Name");

        lastNameField = new TextField();

        lastNameField.setPromptText(
                "Enter last name"
        );


        // =========================
        // EMAIL
        // =========================

        Label emailLabel =
                new Label("Email");

        emailField = new TextField();

        emailField.setPromptText(
                "Enter email address"
        );


        // =========================
        // PHONE
        // =========================

        Label phoneLabel =
                new Label("Phone");

        phoneField = new TextField();

        phoneField.setPromptText(
                "Enter phone number"
        );


        // =========================
        // COMPANY
        // =========================

        Label companyLabel =
                new Label("Company");

        companyField = new TextField();

        companyField.setPromptText(
                "Enter company name"
        );


        // =========================
        // STATUS
        // =========================

        Label statusLabel =
                new Label("Status");

        statusComboBox = new ComboBox<>();

        statusComboBox.getItems().addAll(
                "Lead",
                "Prospect",
                "Active Customer",
                "Inactive"
        );

        statusComboBox.setValue("Lead");

        statusComboBox.setMaxWidth(
                Double.MAX_VALUE
        );


        // =========================
        // PRE-FILL FORM FOR EDITING
        // =========================

        if (customerToEdit != null) {

            firstNameField.setText(
                    customerToEdit.getFirstName()
            );

            lastNameField.setText(
                    customerToEdit.getLastName()
            );

            emailField.setText(
                    customerToEdit.getEmail()
            );

            phoneField.setText(
                    customerToEdit.getPhone()
            );

            companyField.setText(
                    customerToEdit.getCompany()
            );

            statusComboBox.setValue(
                    customerToEdit.getStatus()
            );
        }


        // =========================
        // FORM LAYOUT
        // =========================

        GridPane formGrid = new GridPane();

        formGrid.setHgap(15);
        formGrid.setVgap(15);


        formGrid.add(firstNameLabel, 0, 0);
        formGrid.add(firstNameField, 1, 0);

        formGrid.add(lastNameLabel, 0, 1);
        formGrid.add(lastNameField, 1, 1);

        formGrid.add(emailLabel, 0, 2);
        formGrid.add(emailField, 1, 2);

        formGrid.add(phoneLabel, 0, 3);
        formGrid.add(phoneField, 1, 3);

        formGrid.add(companyLabel, 0, 4);
        formGrid.add(companyField, 1, 4);

        formGrid.add(statusLabel, 0, 5);
        formGrid.add(statusComboBox, 1, 5);


        // =========================
        // BUTTONS
        // =========================

        Button saveButton = new Button(
                customerToEdit == null
                        ? "Save Customer"
                        : "Update Customer"
        );


        saveButton.setOnAction(event -> {
            saveCustomer();
        });


        Button cancelButton =
                new Button("Cancel");


        cancelButton.setOnAction(event -> {

            stage.close();

        });


        saveButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;"
        );


        cancelButton.setStyle(
                "-fx-background-color: #E2E8F0;"
        );


        HBox buttonBox = new HBox(
                10,
                cancelButton,
                saveButton
        );

        buttonBox.setAlignment(
                Pos.CENTER_RIGHT
        );


        // =========================
        // MAIN LAYOUT
        // =========================

        VBox root = new VBox(
                25,
                title,
                formGrid,
                buttonBox
        );

        root.setPadding(
                new Insets(30)
        );


        Scene scene = new Scene(
                root,
                500,
                450
        );


        stage.setTitle(
                customerToEdit == null
                        ? "Add Customer"
                        : "Edit Customer"
        );


        stage.setScene(scene);
    }


    // =========================
    // VALIDATE FORM
    // =========================

    private boolean validateForm() {

        if (firstNameField.getText().trim().isEmpty()) {

            showError("First name is required.");

            return false;
        }


        if (lastNameField.getText().trim().isEmpty()) {

            showError("Last name is required.");

            return false;
        }


        if (emailField.getText().trim().isEmpty()) {

            showError("Email is required.");

            return false;
        }


        String email = emailField.getText().trim();

        String emailPattern =
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";


        if (!Pattern.matches(emailPattern, email)) {

            showError(
                    "Please enter a valid email address."
            );

            return false;
        }


        if (phoneField.getText().trim().isEmpty()) {

            showError("Phone number is required.");

            return false;
        }


        if (companyField.getText().trim().isEmpty()) {

            showError("Company name is required.");

            return false;
        }


        return true;
    }


    // =========================
    // SHOW ERROR
    // =========================

    private void showError(String message) {

        Alert alert = new Alert(
                Alert.AlertType.ERROR
        );

        alert.setTitle("Validation Error");

        alert.setHeaderText(null);

        alert.setContentText(message);

        alert.showAndWait();
    }


    // =========================
    // SAVE / UPDATE CUSTOMER
    // =========================

    private void saveCustomer() {

        if (!validateForm()) {

            return;
        }


        // =========================
        // ADD NEW CUSTOMER
        // =========================

        if (customerToEdit == null) {

            Customer customer = new Customer();

            customer.setFirstName(
                    firstNameField.getText().trim()
            );

            customer.setLastName(
                    lastNameField.getText().trim()
            );

            customer.setEmail(
                    emailField.getText().trim()
            );

            customer.setPhone(
                    phoneField.getText().trim()
            );

            customer.setCompany(
                    companyField.getText().trim()
            );

            customer.setStatus(
                    statusComboBox.getValue()
            );

            customer.setCreatedAt(
                    LocalDateTime.now()
            );


            customerRepository.save(customer);


            System.out.println(
                    "Customer saved successfully!"
            );

        } else {

            // =========================
            // UPDATE CUSTOMER
            // =========================

            customerToEdit.setFirstName(
                    firstNameField.getText().trim()
            );

            customerToEdit.setLastName(
                    lastNameField.getText().trim()
            );

            customerToEdit.setEmail(
                    emailField.getText().trim()
            );

            customerToEdit.setPhone(
                    phoneField.getText().trim()
            );

            customerToEdit.setCompany(
                    companyField.getText().trim()
            );

            customerToEdit.setStatus(
                    statusComboBox.getValue()
            );


            customerRepository.update(
                    customerToEdit
            );


            System.out.println(
                    "Customer updated successfully!"
            );
        }


        // =========================
        // REFRESH TABLE
        // =========================

        if (onCustomerSaved != null) {

            onCustomerSaved.run();
        }


        stage.close();
    }


    // =========================
    // CALLBACK
    // =========================

    public void setOnCustomerSaved(
            Runnable onCustomerSaved
    ) {

        this.onCustomerSaved = onCustomerSaved;
    }


    // =========================
    // SHOW FORM
    // =========================

    public void show() {

        stage.show();
    }
}