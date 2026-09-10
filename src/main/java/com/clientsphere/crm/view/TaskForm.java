package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Task;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.TaskRepository;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class TaskForm {

    private final Stage stage;

    private final TaskRepository taskRepository;

    private final CustomerRepository customerRepository;

    private Runnable onTaskSaved;

    // =========================
    // EDIT MODE
    // =========================

    private final Task taskToEdit;

    private final boolean editMode;


    // =========================
    // FORM CONTROLS
    // =========================

    private ComboBox<Customer> customerComboBox;

    private TextField titleField;

    private TextArea descriptionArea;

    private DatePicker dueDatePicker;

    private ComboBox<String> priorityComboBox;

    private ComboBox<String> statusComboBox;


    // =========================
    // ADD TASK CONSTRUCTOR
    // =========================

    public TaskForm() {

        this(null);
    }


    // =========================
    // EDIT TASK CONSTRUCTOR
    // =========================

    public TaskForm(Task task) {

        stage = new Stage();

        taskRepository =
                new TaskRepository();

        customerRepository =
                new CustomerRepository();

        taskToEdit = task;

        editMode = task != null;

        createForm();
    }


    // =========================
    // CREATE FORM
    // =========================

    private void createForm() {

        // =========================
        // TITLE
        // =========================

        Label title =
                new Label(
                        editMode
                                ? "Edit Task"
                                : "Add New Task"
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
        // TASK TITLE
        // =========================

        Label taskTitleLabel =
                new Label("Title");

        titleField =
                new TextField();

        titleField.setPromptText(
                "Enter task title"
        );


        // =========================
        // DESCRIPTION
        // =========================

        Label descriptionLabel =
                new Label("Description");

        descriptionArea =
                new TextArea();

        descriptionArea.setPromptText(
                "Enter task details"
        );

        descriptionArea.setPrefRowCount(4);

        descriptionArea.setWrapText(true);


        // =========================
        // DUE DATE
        // =========================

        Label dueDateLabel =
                new Label("Due Date");

        dueDatePicker =
                new DatePicker();

        dueDatePicker.setPromptText(
                "Select due date"
        );

        dueDatePicker.setMaxWidth(
                Double.MAX_VALUE
        );


        // Allow today and future dates only

        dueDatePicker.setDayCellFactory(
                picker -> new javafx.scene.control.DateCell() {

                    @Override
                    public void updateItem(
                            LocalDate date,
                            boolean empty
                    ) {

                        super.updateItem(
                                date,
                                empty
                        );

                        if (date != null
                                && date.isBefore(
                                LocalDate.now()
                        )) {

                            setDisable(true);
                        }
                    }
                }
        );


        // =========================
        // PRIORITY
        // =========================

        Label priorityLabel =
                new Label("Priority");

        priorityComboBox =
                new ComboBox<>();

        priorityComboBox.getItems().addAll(
                "Low",
                "Medium",
                "High"
        );

        priorityComboBox.setPromptText(
                "Select priority"
        );

        priorityComboBox.setMaxWidth(
                Double.MAX_VALUE
        );


        // =========================
        // STATUS
        // =========================

        Label statusLabel =
                new Label("Status");

        statusComboBox =
                new ComboBox<>();

        statusComboBox.getItems().addAll(
                "Pending",
                "In Progress",
                "Completed"
        );

        statusComboBox.setValue(
                "Pending"
        );

        statusComboBox.setMaxWidth(
                Double.MAX_VALUE
        );


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
                taskTitleLabel,
                0,
                1
        );

        formGrid.add(
                titleField,
                1,
                1
        );


        formGrid.add(
                descriptionLabel,
                0,
                2
        );

        formGrid.add(
                descriptionArea,
                1,
                2
        );


        formGrid.add(
                dueDateLabel,
                0,
                3
        );

        formGrid.add(
                dueDatePicker,
                1,
                3
        );


        formGrid.add(
                priorityLabel,
                0,
                4
        );

        formGrid.add(
                priorityComboBox,
                1,
                4
        );


        formGrid.add(
                statusLabel,
                0,
                5
        );

        formGrid.add(
                statusComboBox,
                1,
                5
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
                                ? "Update Task"
                                : "Save Task"
                );

        saveButton.setOnAction(
                event -> saveTask()
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
                        20,
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
                        600,
                        550
                );


        stage.setTitle(
                editMode
                        ? "Edit Task"
                        : "Add Task"
        );

        stage.setScene(scene);


        // =========================
        // POPULATE EDIT FORM
        // =========================

        if (editMode) {

            populateForm();
        }
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
    // POPULATE EDIT FORM
    // =========================

    private void populateForm() {

        // CUSTOMER

        for (Customer customer :
                customerComboBox.getItems()) {

            if (customer.getId().equals(
                    taskToEdit.getCustomerId()
            )) {

                customerComboBox.setValue(
                        customer
                );

                break;
            }
        }


        // TITLE

        titleField.setText(
                taskToEdit.getTitle()
        );


        // DESCRIPTION

        descriptionArea.setText(
                taskToEdit.getDescription()
        );


        // DUE DATE

        if (taskToEdit.getDueDate() != null) {

            dueDatePicker.setValue(
                    taskToEdit.getDueDate().toLocalDate()
            );
        }


        // PRIORITY

        priorityComboBox.setValue(
                taskToEdit.getPriority()
        );


        // STATUS

        statusComboBox.setValue(
                taskToEdit.getStatus()
        );
    }


    // =========================
    // VALIDATION
    // =========================

    private boolean validateForm() {

        if (customerComboBox.getValue() == null) {

            showError(
                    "Please select a customer."
            );

            return false;
        }


        if (titleField.getText().trim().isEmpty()) {

            showError(
                    "Task title is required."
            );

            return false;
        }


        if (descriptionArea.getText().trim().isEmpty()) {

            showError(
                    "Description is required."
            );

            return false;
        }


        // DUE DATE

        if (dueDatePicker.getValue() == null) {

            showError(
                    "Due date is required."
            );

            return false;
        }


        // Prevent past dates

        if (dueDatePicker.getValue().isBefore(
                LocalDate.now()
        )) {

            showError(
                    "Due date cannot be in the past."
            );

            return false;
        }


        if (priorityComboBox.getValue() == null) {

            showError(
                    "Please select a priority."
            );

            return false;
        }


        if (statusComboBox.getValue() == null) {

            showError(
                    "Please select a status."
            );

            return false;
        }


        return true;
    }


    // =========================
    // SAVE / UPDATE TASK
    // =========================

    private void saveTask() {

        if (!validateForm()) {

            return;
        }


        Customer selectedCustomer =
                customerComboBox.getValue();


        Task task;


        if (editMode) {

            // UPDATE EXISTING TASK

            task = taskToEdit;

        } else {

            // CREATE NEW TASK

            task = new Task();

            task.setCreatedAt(
                    LocalDateTime.now()
            );
        }


        task.setCustomerId(
                selectedCustomer.getId()
        );


        task.setTitle(
                titleField.getText().trim()
        );


        task.setDescription(
                descriptionArea.getText().trim()
        );


        task.setDueDate(
                dueDatePicker.getValue().atStartOfDay()
        );


        task.setPriority(
                priorityComboBox.getValue()
        );


        task.setStatus(
                statusComboBox.getValue()
        );


        if (editMode) {

            taskRepository.update(
                    task
            );

            System.out.println(
                    "Task updated successfully!"
            );

        } else {

            taskRepository.save(
                    task
            );

            System.out.println(
                    "Task saved successfully!"
            );
        }


        if (onTaskSaved != null) {

            onTaskSaved.run();
        }


        stage.close();
    }


    // =========================
    // ERROR ALERT
    // =========================

    private void showError(String message) {

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

    public void setOnTaskSaved(
            Runnable onTaskSaved
    ) {

        this.onTaskSaved =
                onTaskSaved;
    }


    // =========================
    // SHOW FORM
    // =========================

    public void show() {

        stage.show();
    }
}