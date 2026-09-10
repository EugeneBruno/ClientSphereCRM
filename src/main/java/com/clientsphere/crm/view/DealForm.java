package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Deal;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.DealRepository;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class DealForm {

    private final Stage stage;

    private final DealRepository dealRepository;
    private final CustomerRepository customerRepository;

    private final Deal dealToEdit;
    private final boolean editMode;

    private ComboBox<Customer> customerComboBox;
    private TextField titleField;
    private TextArea descriptionArea;
    private TextField valueField;
    private ComboBox<String> statusComboBox;
    private DatePicker expectedCloseDatePicker;

    private Runnable onDealSaved;

    public DealForm() {
        this(null);
    }

    public DealForm(Deal deal) {
        stage = new Stage();

        dealRepository = new DealRepository();
        customerRepository = new CustomerRepository();

        dealToEdit = deal;
        editMode = deal != null;

        createForm();
    }

    private void createForm() {
        stage.setTitle(editMode ? "Edit Deal" : "Add Deal");
        stage.initModality(Modality.APPLICATION_MODAL);

        Label heading = new Label(
                editMode ? "Edit Deal" : "Create New Deal"
        );
        heading.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        customerComboBox = new ComboBox<>();
        customerComboBox.setMaxWidth(Double.MAX_VALUE);

        List<Customer> customers = customerRepository.findAll();

        customerComboBox.setItems(
                FXCollections.observableArrayList(customers)
        );

        customerComboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Customer customer) {
                if (customer == null) {
                    return "";
                }

                return customer.getFirstName()
                        + " "
                        + customer.getLastName();
            }

            @Override
            public Customer fromString(String string) {
                return null;
            }
        });

        titleField = new TextField();
        titleField.setPromptText("Enter deal title");

        descriptionArea = new TextArea();
        descriptionArea.setPromptText("Enter deal description");
        descriptionArea.setWrapText(true);
        descriptionArea.setPrefRowCount(4);

        valueField = new TextField();
        valueField.setPromptText("Enter deal value");

        statusComboBox = new ComboBox<>();
        statusComboBox.setItems(
                FXCollections.observableArrayList(
                        "Open",
                        "Won",
                        "Lost"
                )
        );
        statusComboBox.setMaxWidth(Double.MAX_VALUE);
        statusComboBox.setValue("Open");

        expectedCloseDatePicker = new DatePicker();
        expectedCloseDatePicker.setPromptText("Select expected close date");

        expectedCloseDatePicker.setDayCellFactory(
                picker -> new javafx.scene.control.DateCell() {
                    @Override
                    public void updateItem(
                            LocalDate date,
                            boolean empty
                    ) {
                        super.updateItem(date, empty);

                        if (date.isBefore(LocalDate.now())) {
                            setDisable(true);
                        }
                    }
                }
        );

        GridPane formGrid = new GridPane();
        formGrid.setHgap(15);
        formGrid.setVgap(15);

        formGrid.add(new Label("Customer:"), 0, 0);
        formGrid.add(customerComboBox, 1, 0);

        formGrid.add(new Label("Deal Title:"), 0, 1);
        formGrid.add(titleField, 1, 1);

        formGrid.add(new Label("Description:"), 0, 2);
        formGrid.add(descriptionArea, 1, 2);

        formGrid.add(new Label("Deal Value:"), 0, 3);
        formGrid.add(valueField, 1, 3);

        formGrid.add(new Label("Status:"), 0, 4);
        formGrid.add(statusComboBox, 1, 4);

        formGrid.add(new Label("Expected Close:"), 0, 5);
        formGrid.add(expectedCloseDatePicker, 1, 5);

        GridPane.setHgrow(customerComboBox, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(titleField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(descriptionArea, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(valueField, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(statusComboBox, javafx.scene.layout.Priority.ALWAYS);
        GridPane.setHgrow(expectedCloseDatePicker, javafx.scene.layout.Priority.ALWAYS);

        Button saveButton = new Button(
                editMode ? "Update Deal" : "Save Deal"
        );
        saveButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-cursor: hand;"
        );

        Button cancelButton = new Button("Cancel");
        cancelButton.setStyle(
                "-fx-background-color: #E2E8F0;" +
                        "-fx-text-fill: #334155;" +
                        "-fx-cursor: hand;"
        );

        saveButton.setOnAction(event -> saveDeal());
        cancelButton.setOnAction(event -> stage.close());

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.getChildren().addAll(cancelButton, saveButton);

        VBox root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setPrefWidth(600);
        root.setStyle("-fx-background-color: #F8FAFC;");

        root.getChildren().addAll(
                heading,
                formGrid,
                buttons
        );

        if (editMode) {
            populateForm();
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);
    }

    private void populateForm() {
        if (dealToEdit.getCustomerId() != null) {
            for (Customer customer : customerComboBox.getItems()) {
                if (customer.getId().equals(dealToEdit.getCustomerId())) {
                    customerComboBox.setValue(customer);
                    break;
                }
            }
        }

        titleField.setText(dealToEdit.getTitle());
        descriptionArea.setText(dealToEdit.getDescription());
        valueField.setText(String.valueOf(dealToEdit.getValue()));
        statusComboBox.setValue(dealToEdit.getStatus());

        if (dealToEdit.getExpectedCloseDate() != null) {
            expectedCloseDatePicker.setValue(
                    dealToEdit.getExpectedCloseDate().toLocalDate()
            );
        }
    }

    private void saveDeal() {
        Customer selectedCustomer = customerComboBox.getValue();
        String title = titleField.getText().trim();
        String description = descriptionArea.getText().trim();
        String valueText = valueField.getText().trim();
        String status = statusComboBox.getValue();
        LocalDate expectedCloseDate =
                expectedCloseDatePicker.getValue();

        if (selectedCustomer == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Please select a customer."
            );
            return;
        }

        if (title.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Please enter a deal title."
            );
            return;
        }

        if (valueText.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Please enter the deal value."
            );
            return;
        }

        double value;

        try {
            value = Double.parseDouble(valueText);

            if (value < 0) {
                showAlert(
                        Alert.AlertType.WARNING,
                        "Validation Error",
                        "Deal value cannot be negative."
                );
                return;
            }

        } catch (NumberFormatException exception) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Please enter a valid numeric deal value."
            );
            return;
        }

        if (status == null || status.isEmpty()) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Please select a deal status."
            );
            return;
        }

        if (expectedCloseDate == null) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Please select an expected close date."
            );
            return;
        }

        if (expectedCloseDate.isBefore(LocalDate.now())) {
            showAlert(
                    Alert.AlertType.WARNING,
                    "Validation Error",
                    "Expected close date cannot be in the past."
            );
            return;
        }

        Deal deal;

        if (editMode) {
            deal = dealToEdit;
        } else {
            deal = new Deal();
            deal.setCreatedAt(LocalDateTime.now());
        }

        deal.setCustomerId(selectedCustomer.getId());
        deal.setTitle(title);
        deal.setDescription(description);
        deal.setValue(value);
        deal.setStatus(status);
        deal.setExpectedCloseDate(
                expectedCloseDate.atStartOfDay()
        );

        if (editMode) {
            dealRepository.update(deal);
        } else {
            dealRepository.save(deal);
        }

        if (onDealSaved != null) {
            onDealSaved.run();
        }

        stage.close();
    }

    private void showAlert(
            Alert.AlertType alertType,
            String title,
            String message
    ) {
        Alert alert = new Alert(
                alertType,
                message,
                ButtonType.OK
        );

        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    public void setOnDealSaved(Runnable onDealSaved) {
        this.onDealSaved = onDealSaved;
    }

    public void show() {
        stage.show();
    }
}