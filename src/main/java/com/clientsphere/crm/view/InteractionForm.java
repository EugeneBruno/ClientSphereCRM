package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.InteractionRepository;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.List;


public class InteractionForm {

    private final Stage stage;

    private final InteractionRepository interactionRepository;

    private final CustomerRepository customerRepository;

    private Runnable onInteractionSaved;

    private ComboBox<Customer> customerComboBox;

    private ComboBox<String> typeComboBox;

    private TextField subjectField;

    private TextArea descriptionArea;

    private Interaction interactionToEdit;

    private boolean editMode = false;


    // =========================
    // ADD INTERACTION CONSTRUCTOR
    // =========================

    public InteractionForm() {

        stage = new Stage();

        interactionRepository =
                new InteractionRepository();

        customerRepository =
                new CustomerRepository();

        createForm();
    }


    // =========================
    // EDIT INTERACTION CONSTRUCTOR
    // =========================

    public InteractionForm(
            Interaction interaction
    ) {

        stage = new Stage();

        interactionRepository =
                new InteractionRepository();

        customerRepository =
                new CustomerRepository();

        this.interactionToEdit =
                interaction;

        this.editMode = true;

        createForm();

        populateForm();
    }


    // =========================
    // CREATE FORM
    // =========================

    private void createForm() {

        Label title = new Label(
                editMode
                        ? "Edit Interaction"
                        : "Add New Interaction"
        );


        title.setStyle(

                "-fx-font-size: 22px;" +

                        "-fx-font-weight: bold;"
        );


        // =========================
        // CUSTOMER
        // =========================

        Label customerLabel =
                new Label("Customer");


        customerComboBox =
                new ComboBox<>();


        customerComboBox.setPromptText(
                "Select customer"
        );


        customerComboBox.setMaxWidth(
                Double.MAX_VALUE
        );


        loadCustomers();


        // =========================
        // INTERACTION TYPE
        // =========================

        Label typeLabel =
                new Label("Interaction Type");


        typeComboBox =
                new ComboBox<>();


        typeComboBox.getItems().addAll(

                "Call",

                "Email",

                "Meeting",

                "Follow-up",

                "Other"
        );


        typeComboBox.setPromptText(
                "Select interaction type"
        );


        typeComboBox.setMaxWidth(
                Double.MAX_VALUE
        );


        // =========================
        // SUBJECT
        // =========================

        Label subjectLabel =
                new Label("Subject");


        subjectField =
                new TextField();


        subjectField.setPromptText(
                "Enter interaction subject"
        );


        // =========================
        // DESCRIPTION
        // =========================

        Label descriptionLabel =
                new Label("Description");


        descriptionArea =
                new TextArea();


        descriptionArea.setPromptText(
                "Enter interaction details"
        );


        descriptionArea.setPrefRowCount(5);


        descriptionArea.setWrapText(true);


        // =========================
        // FORM GRID
        // =========================

        GridPane formGrid =
                new GridPane();


        formGrid.setHgap(15);

        formGrid.setVgap(15);


        formGrid.add(
                customerLabel,
                0,
                0
        );


        formGrid.add(
                customerComboBox,
                1,
                0
        );


        formGrid.add(
                typeLabel,
                0,
                1
        );


        formGrid.add(
                typeComboBox,
                1,
                1
        );


        formGrid.add(
                subjectLabel,
                0,
                2
        );


        formGrid.add(
                subjectField,
                1,
                2
        );


        formGrid.add(
                descriptionLabel,
                0,
                3
        );


        formGrid.add(
                descriptionArea,
                1,
                3
        );


        // =========================
        // BUTTONS
        // =========================

        Button cancelButton =
                new Button("Cancel");


        cancelButton.setOnAction(
                event -> stage.close()
        );


        Button saveButton =
                new Button(

                        editMode
                                ? "Update Interaction"
                                : "Save Interaction"
                );


        saveButton.setOnAction(
                event -> saveInteraction()
        );


        saveButton.setStyle(

                "-fx-background-color: #2563EB;" +

                        "-fx-text-fill: white;" +

                        "-fx-font-weight: bold;"
        );


        cancelButton.setStyle(

                "-fx-background-color: #E2E8F0;"
        );


        HBox buttonBox =
                new HBox(

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

        VBox root =
                new VBox(

                        25,

                        title,

                        formGrid,

                        buttonBox
                );


        root.setPadding(
                new Insets(30)
        );


        Scene scene =
                new Scene(

                        root,

                        550,

                        450
                );


        stage.setTitle(

                editMode
                        ? "Edit Interaction"
                        : "Add Interaction"
        );


        stage.setScene(scene);

        if (editMode){
            populateForm();
        }
    }


    // =========================
    // POPULATE EDIT FORM
    // =========================

    private void populateForm() {

        if (!editMode || interactionToEdit == null) {

            return;
        }


        // SELECT CUSTOMER

        for (Customer customer :
                customerComboBox.getItems()) {

            if (customer.getId().equals(
                    interactionToEdit.getCustomerId()
            )) {

                customerComboBox.setValue(customer);

                break;
            }
        }


        // TYPE

        typeComboBox.setValue(
                interactionToEdit.getType()
        );


        // SUBJECT

        subjectField.setText(
                interactionToEdit.getSubject()
        );


        // DESCRIPTION

        descriptionArea.setText(
                interactionToEdit.getDescription()
        );
    }

    // =========================
    // LOAD CUSTOMERS
    // =========================

    private void loadCustomers() {

        List<Customer> customers =
                customerRepository.findAll();


        customerComboBox.getItems().addAll(
                customers
        );
    }


    // =========================
    // VALIDATE FORM
    // =========================

    private boolean validateForm() {

        if (customerComboBox.getValue() == null) {

            showError(
                    "Please select a customer."
            );


            return false;
        }


        if (typeComboBox.getValue() == null) {

            showError(
                    "Please select an interaction type."
            );


            return false;
        }


        if (subjectField.getText().trim().isEmpty()) {

            showError(
                    "Subject is required."
            );


            return false;
        }


        if (descriptionArea.getText().trim().isEmpty()) {

            showError(
                    "Description is required."
            );


            return false;
        }


        return true;
    }


    // =========================
    // SAVE OR UPDATE INTERACTION
    // =========================

    private void saveInteraction() {

        if (!validateForm()) {

            return;
        }


        Customer selectedCustomer =
                customerComboBox.getValue();


        Interaction interaction;


        if (editMode) {

            interaction = interactionToEdit;

        } else {

            interaction = new Interaction();

            interaction.setCreatedAt(
                    LocalDateTime.now()
            );
        }


        interaction.setCustomerId(
                selectedCustomer.getId()
        );


        interaction.setType(
                typeComboBox.getValue()
        );


        interaction.setSubject(
                subjectField.getText().trim()
        );


        interaction.setDescription(
                descriptionArea.getText().trim()
        );


        interaction.setInteractionDate(
                LocalDateTime.now()
        );


        if (editMode) {

            interactionRepository.update(
                    interaction
            );


            System.out.println(
                    "Interaction updated successfully!"
            );

        } else {

            interactionRepository.save(
                    interaction
            );


            System.out.println(
                    "Interaction saved successfully!"
            );
        }


        if (onInteractionSaved != null) {

            onInteractionSaved.run();
        }


        stage.close();
    }

    // =========================
    // SHOW VALIDATION ERROR
    // =========================

    private void showError(
            String message
    ) {

        Alert alert =
                new Alert(
                        Alert.AlertType.ERROR
                );


        alert.setTitle(
                "Validation Error"
        );


        alert.setHeaderText(
                null
        );


        alert.setContentText(
                message
        );


        alert.showAndWait();
    }


    // =========================
    // CALLBACK
    // =========================

    public void setOnInteractionSaved(
            Runnable onInteractionSaved
    ) {

        this.onInteractionSaved =
                onInteractionSaved;
    }


    // =========================
    // SHOW FORM
    // =========================

    public void show() {

        stage.show();
    }
}