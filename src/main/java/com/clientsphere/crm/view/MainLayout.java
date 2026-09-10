package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Task;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.InteractionRepository;
import com.clientsphere.crm.repository.TaskRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class MainLayout {

    private final BorderPane root;

    private final CustomerRepository customerRepository;
    private final InteractionRepository interactionRepository;
    private final TaskRepository taskRepository;

    public MainLayout() {
        root = new BorderPane();

        customerRepository = new CustomerRepository();
        interactionRepository = new InteractionRepository();
        taskRepository = new TaskRepository();

        createTopBar();
        createSidebar();
        showDashboard();
    }

    private void createTopBar() {
        Label appTitle = new Label("ClientSphere CRM");
        appTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        Label welcomeLabel = new Label("Welcome, Bruno");
        welcomeLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: white;"
        );

        HBox topBar = new HBox(20);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(15, 25, 15, 25));
        topBar.setStyle("-fx-background-color: #1E293B;");

        topBar.getChildren().addAll(appTitle, welcomeLabel);

        root.setTop(topBar);
    }

    private void createSidebar() {
        Button dashboardButton = createSidebarButton("Dashboard");
        Button customersButton = createSidebarButton("Customers");
        Button interactionsButton = createSidebarButton("Interactions");
        Button tasksButton = createSidebarButton("Tasks");
        Button analyticsButton = createSidebarButton("Analytics");
        Button settingsButton = createSidebarButton("Settings");

        dashboardButton.setOnAction(event -> showDashboard());

        customersButton.setOnAction(event -> {
            CustomerView customerView = new CustomerView();
            root.setCenter(customerView.getRoot());
        });

        interactionsButton.setOnAction(event -> {
            InteractionView interactionView = new InteractionView();
            root.setCenter(interactionView.getView());
        });

        tasksButton.setOnAction(event -> {
            TaskView taskView = new TaskView();
            root.setCenter(taskView.getView());
        });

        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(25, 15, 25, 15));
        sidebar.setPrefWidth(190);
        sidebar.setStyle("-fx-background-color: #F8FAFC;");

        sidebar.getChildren().addAll(
                dashboardButton,
                customersButton,
                interactionsButton,
                tasksButton,
                analyticsButton,
                settingsButton
        );

        root.setLeft(sidebar);
    }

    private Button createSidebarButton(String text) {
        Button button = new Button(text);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setPadding(new Insets(12, 15, 12, 15));

        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-font-size: 14px;" +
                        "-fx-text-fill: #334155;" +
                        "-fx-cursor: hand;"
        );

        return button;
    }

    private void showDashboard() {
        int totalCustomers = customerRepository.findAll().size();

        int totalInteractions = interactionRepository.findAll().size();

        List<Task> allTasks = taskRepository.findAll();

        long pendingTasks = allTasks.stream()
                .filter(task ->
                        task.getStatus() != null &&
                                task.getStatus().equalsIgnoreCase("Pending")
                )
                .count();

        long completedTasks = allTasks.stream()
                .filter(task ->
                        task.getStatus() != null &&
                                task.getStatus().equalsIgnoreCase("Completed")
                )
                .count();

        Label title = new Label("Dashboard");
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        Label subtitle = new Label(
                "Overview of your customers, interactions, and tasks"
        );
        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        HBox summaryCards = new HBox(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);

        summaryCards.getChildren().addAll(
                createStatCard(
                        "Total Customers",
                        String.valueOf(totalCustomers),
                        "#2563EB"
                ),
                createStatCard(
                        "Total Interactions",
                        String.valueOf(totalInteractions),
                        "#7C3AED"
                ),
                createStatCard(
                        "Pending Tasks",
                        String.valueOf(pendingTasks),
                        "#D97706"
                ),
                createStatCard(
                        "Completed Tasks",
                        String.valueOf(completedTasks),
                        "#16A34A"
                )
        );

        VBox content = new VBox(25);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #F8FAFC;");

        content.getChildren().addAll(
                title,
                subtitle,
                summaryCards
        );

        root.setCenter(content);
    }

    private VBox createStatCard(
            String title,
            String value,
            String valueColor
    ) {
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setStyle(
                "-fx-font-size: 30px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + valueColor + ";"
        );

        VBox card = new VBox(10);
        card.setPrefWidth(190);
        card.setPrefHeight(120);
        card.setPadding(new Insets(20));

        card.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 10px;"
        );

        card.getChildren().addAll(
                titleLabel,
                valueLabel
        );

        return card;
    }

    public BorderPane getRoot() {
        return root;
    }
}