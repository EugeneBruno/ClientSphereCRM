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
import javafx.scene.layout.FlowPane;
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


    // =========================================================
    // MAIN VIEW
    // =========================================================

    public BorderPane getView() {

        BorderPane mainLayout =
                new BorderPane();


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


        // =====================================================
        // PAGE HEADER
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
                "Search tasks..."
        );

        searchField.setPrefHeight(40);

        searchField.setMinWidth(180);

        searchField.setMaxWidth(
                Double.MAX_VALUE
        );


        searchField.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        filterTasks(newValue)
        );


        // =====================================================
        // TASK TABLE
        // =====================================================

        taskTable =
                createTaskTable();


        taskTable.setMinWidth(0);

        taskTable.setMaxWidth(
                Double.MAX_VALUE
        );


        VBox.setVgrow(
                taskTable,
                Priority.ALWAYS
        );


        content.getChildren().addAll(
                header,
                searchField,
                taskTable
        );


        mainLayout.setCenter(
                content
        );


        // =====================================================
        // RESPONSIVE PAGE PADDING
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


        // =====================================================
        // RESPONSIVE SEARCH WIDTH
        // =====================================================

        content.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    double width =
                            newWidth.doubleValue();

                    if (width <= 0) {
                        return;
                    }


                    searchField.setPrefWidth(
                            Math.max(
                                    180,
                                    width
                            )
                    );
                }
        );


        loadTasks();


        return mainLayout;
    }


    // =========================================================
    // HEADER
    // =========================================================

    private FlowPane createHeader() {

        Label title =
                new Label("Tasks");

        title.setStyle(
                "-fx-font-size: 32px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );


        Label subtitle =
                new Label(
                        "Track and manage your customer tasks"
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
        // ADD TASK BUTTON
        // =====================================================

        Button addTaskButton =
                new Button("+ Add Task");

        addTaskButton.setPrefHeight(42);

        addTaskButton.setStyle(
                "-fx-background-color: #2563EB;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-padding: 10 18 10 18;" +
                        "-fx-cursor: hand;"
        );


        addTaskButton.setOnAction(event -> {

            TaskForm taskForm =
                    new TaskForm();

            taskForm.setOnTaskSaved(
                    this::loadTasks
            );

            taskForm.show();
        });


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

        header.setMaxWidth(
                Double.MAX_VALUE
        );


        header.getChildren().addAll(
                titleBox,
                addTaskButton
        );


        return header;
    }


    // =========================================================
    // TASK TABLE
    // =========================================================

    private TableView<Task> createTaskTable() {

        TableView<Task> table =
                new TableView<>();


        table.setMinWidth(0);

        table.setPlaceholder(
                new Label("No tasks found.")
        );


        // =====================================================
        // CUSTOMER
        // =====================================================

        TableColumn<Task, String>
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
        // TITLE
        // =====================================================

        TableColumn<Task, String>
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

        TableColumn<Task, String>
                descriptionColumn =
                new TableColumn<>("Description");


        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "description"
                )
        );


        // =====================================================
        // DUE DATE
        // =====================================================

        TableColumn<Task, LocalDateTime>
                dueDateColumn =
                new TableColumn<>("Due Date");


        dueDateColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "dueDate"
                )
        );


        dueDateColumn.setCellFactory(
                column ->
                        new TableCell<>() {

                            @Override
                            protected void updateItem(
                                    LocalDateTime date,
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
                                            dateFormatter.format(
                                                    date
                                            )
                                    );
                                }
                            }
                        }
        );


        // =====================================================
        // PRIORITY
        // =====================================================

        TableColumn<Task, String>
                priorityColumn =
                new TableColumn<>("Priority");


        priorityColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "priority"
                )
        );


        // =====================================================
        // STATUS
        // =====================================================

        TableColumn<Task, String>
                statusColumn =
                new TableColumn<>("Status");


        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "status"
                )
        );


        // =====================================================
        // ACTIONS
        // =====================================================

        TableColumn<Task, Void>
                actionsColumn =
                new TableColumn<>("Actions");


        actionsColumn.setCellFactory(
                column ->
                        new TableCell<>() {

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
                                // =================================
                                // EDIT BUTTON
                                // =================================

                                editButton.setStyle(
                                        "-fx-background-color: #2563EB;" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-cursor: hand;"
                                );


                                // =================================
                                // DELETE BUTTON
                                // =================================

                                deleteButton.setStyle(
                                        "-fx-background-color: #DC2626;" +
                                                "-fx-text-fill: white;" +
                                                "-fx-font-size: 12px;" +
                                                "-fx-cursor: hand;"
                                );


                                actionBox.setAlignment(
                                        Pos.CENTER
                                );


                                // =================================
                                // EDIT ACTION
                                // =================================

                                editButton.setOnAction(
                                        event -> {

                                            Task task =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );


                                            if (task == null) {
                                                return;
                                            }


                                            TaskForm taskForm =
                                                    new TaskForm(
                                                            task
                                                    );


                                            taskForm.setOnTaskSaved(
                                                    TaskView.this::loadTasks
                                            );


                                            taskForm.show();
                                        }
                                );


                                // =================================
                                // DELETE ACTION
                                // =================================

                                deleteButton.setOnAction(
                                        event -> {

                                            Task task =
                                                    getTableView()
                                                            .getItems()
                                                            .get(
                                                                    getIndex()
                                                            );


                                            if (task == null) {
                                                return;
                                            }


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
                                                    .ifPresent(
                                                            response -> {

                                                                if (
                                                                        response ==
                                                                                ButtonType.OK
                                                                ) {

                                                                    taskRepository.delete(
                                                                            task.getId()
                                                                    );


                                                                    loadTasks();
                                                                }
                                                            }
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
                                            actionBox
                                    );
                                }
                            }
                        }
        );


        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        customerColumn.setMinWidth(150);
        customerColumn.setPrefWidth(180);


        titleColumn.setMinWidth(150);
        titleColumn.setPrefWidth(200);


        descriptionColumn.setMinWidth(220);
        descriptionColumn.setPrefWidth(300);


        dueDateColumn.setMinWidth(170);
        dueDateColumn.setPrefWidth(180);


        priorityColumn.setMinWidth(110);
        priorityColumn.setPrefWidth(120);


        statusColumn.setMinWidth(110);
        statusColumn.setPrefWidth(120);


        actionsColumn.setMinWidth(150);
        actionsColumn.setPrefWidth(170);


        // =====================================================
        // ADD COLUMNS
        // =====================================================

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


        /*
         * When the table is wide enough, columns share the
         * available width.
         *
         * When the window becomes too small, JavaFX allows
         * the TableView itself to scroll horizontally rather
         * than cutting off the page.
         */

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


                    /*
                     * Total minimum width is approximately:
                     *
                     * 150 + 150 + 220 + 170
                     * + 110 + 110 + 150
                     *
                     * = 1060px
                     *
                     * Below this, the table will use its own
                     * horizontal scrollbar.
                     */

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
    // LOAD TASKS
    // =========================================================

    private void loadTasks() {

        // =====================================================
        // LOAD CUSTOMERS
        // =====================================================

        List<Customer> customers =
                customerRepository.findAll();


        customerNames.clear();


        for (
                Customer customer :
                customers
        ) {

            String fullName =
                    customer.getFirstName()
                            + " "
                            + customer.getLastName();


            customerNames.put(
                    customer.getId(),
                    fullName
            );
        }


        // =====================================================
        // LOAD TASKS
        // =====================================================

        List<Task> taskList =
                taskRepository.findAll();


        tasks.clear();


        tasks.addAll(
                taskList
        );


        // =====================================================
        // APPLY CURRENT SEARCH
        // =====================================================

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


    // =========================================================
    // SEARCH / FILTER
    // =========================================================

    private void filterTasks(
            String keyword
    ) {

        if (
                keyword == null
                        ||
                        keyword.isBlank()
        ) {

            filteredTasks.setAll(
                    tasks
            );

            return;
        }


        String searchKeyword =
                keyword
                        .toLowerCase()
                        .trim();


        filteredTasks.clear();


        for (
                Task task :
                tasks
        ) {

            String customerName =
                    customerNames.getOrDefault(
                                    task.getCustomerId(),
                                    ""
                            )
                            .toLowerCase();


            String title =
                    task.getTitle() != null
                            ? task.getTitle()
                            .toLowerCase()
                            : "";


            String description =
                    task.getDescription() != null
                            ? task.getDescription()
                            .toLowerCase()
                            : "";


            String priority =
                    task.getPriority() != null
                            ? task.getPriority()
                            .toLowerCase()
                            : "";


            String status =
                    task.getStatus() != null
                            ? task.getStatus()
                            .toLowerCase()
                            : "";


            if (
                    customerName.contains(
                            searchKeyword
                    )

                            ||

                            title.contains(
                                    searchKeyword
                            )

                            ||

                            description.contains(
                                    searchKeyword
                            )

                            ||

                            priority.contains(
                                    searchKeyword
                            )

                            ||

                            status.contains(
                                    searchKeyword
                            )
            ) {

                filteredTasks.add(
                        task
                );
            }
        }
    }
}