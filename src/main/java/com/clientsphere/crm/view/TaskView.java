package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Task;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.TaskRepository;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskView {

    private final TaskRepository taskRepository;
    private final CustomerRepository customerRepository;

    private final Map<ObjectId, String> customerNames;

    private final ObservableList<Task> tasks;
    private final ObservableList<Task> filteredTasks;

    private TableView<Task> taskTable;
    private TextField searchField;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, HH:mm"
            );


    public TaskView() {

        taskRepository =
                new TaskRepository();

        customerRepository =
                new CustomerRepository();

        customerNames =
                new HashMap<>();

        tasks =
                FXCollections.observableArrayList();

        filteredTasks =
                FXCollections.observableArrayList();
    }


    public BorderPane getView() {

        BorderPane mainLayout =
                new BorderPane();

        VBox content =
                new VBox(25);

        content.setPadding(
                new Insets(50)
        );


        // =========================
        // PAGE HEADER
        // =========================

        HBox header =
                createHeader();


        // =========================
        // SEARCH BAR
        // =========================

        searchField =
                new TextField();

        searchField.setPromptText(
                "Search tasks..."
        );

        searchField.setPrefHeight(35);

        searchField.setMaxWidth(
                Double.MAX_VALUE
        );

        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        filterTasks(newValue)
        );


        // =========================
        // TABLE
        // =========================

        taskTable =
                createTaskTable();


        content.getChildren().addAll(
                header,
                searchField,
                taskTable
        );


        VBox.setVgrow(
                taskTable,
                Priority.ALWAYS
        );


        mainLayout.setCenter(
                content
        );


        loadTasks();


        return mainLayout;
    }


    // =========================
    // HEADER
    // =========================

    private HBox createHeader() {

        Label title =
                new Label("Tasks");

        title.setStyle(
                "-fx-font-size: 36px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );


        Label subtitle =
                new Label(
                        "Track and manage your customer tasks"
                );

        subtitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-text-fill: #64748B;"
        );


        VBox titleBox =
                new VBox(8);

        titleBox.getChildren().addAll(
                title,
                subtitle
        );


        Button addTaskButton =
                new Button("+ Add Task");

        addTaskButton.setOnAction(event -> {

            TaskForm taskForm =
                    new TaskForm();

            taskForm.setOnTaskSaved(
                    this::loadTasks
            );

            taskForm.show();
        });


        addTaskButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 12px 20px;"
        );


        HBox header =
                new HBox();

        header.setAlignment(
                Pos.CENTER_LEFT
        );


        HBox.setHgrow(
                titleBox,
                Priority.ALWAYS
        );


        header.getChildren().addAll(
                titleBox,
                addTaskButton
        );


        return header;
    }


    // =========================
    // TASK TABLE
    // =========================

    private TableView<Task> createTaskTable() {

        TableView<Task> table =
                new TableView<>();


        // =========================
        // CUSTOMER
        // =========================

        TableColumn<Task, String>
                customerColumn =
                new TableColumn<>("Customer");

        customerColumn.setCellValueFactory(
                cellData -> {

                    ObjectId customerId =
                            cellData.getValue().getCustomerId();

                    String customerName =
                            customerNames.get(customerId);

                    return new SimpleStringProperty(
                            customerName != null
                                    ? customerName
                                    : "Unknown Customer"
                    );
                }
        );


        // =========================
        // TITLE
        // =========================

        TableColumn<Task, String>
                titleColumn =
                new TableColumn<>("Title");

        titleColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "title"
                )
        );


        // =========================
        // DESCRIPTION
        // =========================

        TableColumn<Task, String>
                descriptionColumn =
                new TableColumn<>("Description");

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "description"
                )
        );


        // =========================
        // DUE DATE
        // =========================

        TableColumn<Task, LocalDateTime>
                dueDateColumn =
                new TableColumn<>("Due Date");

        dueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "dueDate"
                )
        );


        dueDateColumn.setCellFactory(
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


        // =========================
        // PRIORITY
        // =========================

        TableColumn<Task, String>
                priorityColumn =
                new TableColumn<>("Priority");

        priorityColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "priority"
                )
        );


        // =========================
        // STATUS
        // =========================

        TableColumn<Task, String>
                statusColumn =
                new TableColumn<>("Status");

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );


        // =========================
        // ACTIONS
        // =========================

        TableColumn<Task, Void>
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


                        // EDIT ACTION

                        editButton.setOnAction(event -> {

                            Task task =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());


                            TaskForm taskForm =
                                    new TaskForm(task);


                            taskForm.setOnTaskSaved(
                                    TaskView.this::loadTasks
                            );


                            taskForm.show();
                        });


                        // DELETE ACTION

                        deleteButton.setOnAction(event -> {

                            Task task =
                                    getTableView()
                                            .getItems()
                                            .get(getIndex());


                            Alert confirmation =
                                    new Alert(
                                            Alert.AlertType.CONFIRMATION
                                    );

                            confirmation.setTitle(
                                    "Delete Task"
                            );

                            confirmation.setHeaderText(
                                    "Delete this task?"
                            );

                            confirmation.setContentText(
                                    "Are you sure you want to delete \""
                                            + task.getTitle()
                                            + "\"?"
                            );


                            confirmation.showAndWait()
                                    .ifPresent(response -> {

                                        if (response ==
                                                ButtonType.OK) {

                                            taskRepository.delete(
                                                    task.getId()
                                            );

                                            loadTasks();
                                        }
                                    });
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
                }
        );


        // =========================
        // COLUMN WIDTHS
        // =========================

        customerColumn.setPrefWidth(180);

        titleColumn.setPrefWidth(200);

        descriptionColumn.setPrefWidth(300);

        dueDateColumn.setPrefWidth(180);

        priorityColumn.setPrefWidth(120);

        statusColumn.setPrefWidth(120);

        actionsColumn.setPrefWidth(180);


        table.getColumns().addAll(
                customerColumn,
                titleColumn,
                descriptionColumn,
                dueDateColumn,
                priorityColumn,
                statusColumn,
                actionsColumn
        );


        table.setItems(
                filteredTasks
        );


        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        return table;
    }


    // =========================
    // LOAD TASKS
    // =========================

    private void loadTasks() {

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


        List<Task> taskList =
                taskRepository.findAll();


        tasks.clear();

        tasks.addAll(
                taskList
        );


        if (searchField != null) {

            filterTasks(
                    searchField.getText()
            );

        } else {

            filteredTasks.setAll(
                    tasks
            );
        }
    }


    // =========================
    // SEARCH / FILTER
    // =========================

    private void filterTasks(String keyword) {

        if (keyword == null || keyword.isBlank()) {

            filteredTasks.setAll(
                    tasks
            );

            return;
        }


        String searchKeyword =
                keyword.toLowerCase().trim();


        filteredTasks.clear();


        for (Task task : tasks) {

            String customerName =
                    customerNames.getOrDefault(
                            task.getCustomerId(),
                            ""
                    ).toLowerCase();


            String title =
                    task.getTitle() != null
                            ? task.getTitle().toLowerCase()
                            : "";


            String description =
                    task.getDescription() != null
                            ? task.getDescription().toLowerCase()
                            : "";


            String priority =
                    task.getPriority() != null
                            ? task.getPriority().toLowerCase()
                            : "";


            String status =
                    task.getStatus() != null
                            ? task.getStatus().toLowerCase()
                            : "";


            if (customerName.contains(searchKeyword)
                    || title.contains(searchKeyword)
                    || description.contains(searchKeyword)
                    || priority.contains(searchKeyword)
                    || status.contains(searchKeyword)) {

                filteredTasks.add(
                        task
                );
            }
        }
    }
}