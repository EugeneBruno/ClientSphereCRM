package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.model.Task;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.InteractionRepository;
import com.clientsphere.crm.repository.TaskRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainLayout {

    private final BorderPane root;

    private final CustomerRepository customerRepository;
    private final InteractionRepository interactionRepository;
    private final TaskRepository taskRepository;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

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
        Button dealsButton = createSidebarButton("Deals");
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

        dealsButton.setOnAction(event -> {
            DealView dealView = new DealView();
            root.setCenter(dealView.getView());
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
                dealsButton,
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

        VBox upcomingTasksSection = createUpcomingTasksSection(allTasks);
        VBox recentInteractionsSection = createRecentInteractionsSection();

        HBox dashboardSections = new HBox(20);
        dashboardSections.setAlignment(Pos.TOP_LEFT);

        dashboardSections.getChildren().addAll(
                upcomingTasksSection,
                recentInteractionsSection
        );

        VBox dashboardContent = new VBox(25);
        dashboardContent.setPadding(new Insets(30));
        dashboardContent.setStyle("-fx-background-color: #F8FAFC;");

        dashboardContent.getChildren().addAll(
                title,
                subtitle,
                summaryCards,
                dashboardSections
        );

        ScrollPane scrollPane = new ScrollPane(dashboardContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: #F8FAFC;");

        root.setCenter(scrollPane);
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

    private VBox createUpcomingTasksSection(List<Task> allTasks) {
        Label sectionTitle = new Label("Upcoming Tasks");
        sectionTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        VBox taskList = new VBox(12);

        Map<String, String> customerNames = getCustomerNames();

        List<Task> upcomingTasks = allTasks.stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task ->
                        !task.getDueDate().isBefore(LocalDateTime.now())
                )
                .filter(task ->
                        task.getStatus() == null ||
                                !task.getStatus().equalsIgnoreCase("Completed")
                )
                .sorted(Comparator.comparing(Task::getDueDate))
                .limit(5)
                .collect(Collectors.toList());

        if (upcomingTasks.isEmpty()) {
            Label emptyLabel = new Label("No upcoming tasks.");
            emptyLabel.setStyle("-fx-text-fill: #64748B;");
            taskList.getChildren().add(emptyLabel);
        } else {
            for (Task task : upcomingTasks) {
                String customerName = customerNames.getOrDefault(
                        task.getCustomerId().toHexString(),
                        "Unknown Customer"
                );

                Label taskTitle = new Label(task.getTitle());
                taskTitle.setStyle(
                        "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: #0F172A;"
                );

                Label taskDetails = new Label(
                        customerName +
                                " • Due " +
                                task.getDueDate().format(dateFormatter)
                );
                taskDetails.setStyle(
                        "-fx-font-size: 12px;" +
                                "-fx-text-fill: #64748B;"
                );

                VBox taskItem = new VBox(5);
                taskItem.setPadding(new Insets(12));
                taskItem.setStyle(
                        "-fx-background-color: #F8FAFC;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-border-color: #E2E8F0;" +
                                "-fx-border-radius: 8px;"
                );

                taskItem.getChildren().addAll(
                        taskTitle,
                        taskDetails
                );

                taskList.getChildren().add(taskItem);
            }
        }

        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setPrefWidth(420);
        section.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 10px;"
        );

        section.getChildren().addAll(
                sectionTitle,
                taskList
        );

        return section;
    }

    private VBox createRecentInteractionsSection() {
        Label sectionTitle = new Label("Recent Interactions");
        sectionTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        VBox interactionList = new VBox(12);

        Map<String, String> customerNames = getCustomerNames();

        List<Interaction> recentInteractions = interactionRepository
                .findAll()
                .stream()
                .filter(interaction ->
                        interaction.getInteractionDate() != null
                )
                .sorted(
                        Comparator.comparing(
                                Interaction::getInteractionDate
                        ).reversed()
                )
                .limit(5)
                .collect(Collectors.toList());

        if (recentInteractions.isEmpty()) {
            Label emptyLabel = new Label("No interactions recorded.");
            emptyLabel.setStyle("-fx-text-fill: #64748B;");
            interactionList.getChildren().add(emptyLabel);
        } else {
            for (Interaction interaction : recentInteractions) {
                String customerName = customerNames.getOrDefault(
                        interaction.getCustomerId().toHexString(),
                        "Unknown Customer"
                );

                Label subjectLabel = new Label(
                        interaction.getSubject()
                );
                subjectLabel.setStyle(
                        "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: #0F172A;"
                );

                Label interactionDetails = new Label(
                        customerName +
                                " • " +
                                interaction.getType() +
                                " • " +
                                interaction.getInteractionDate()
                                        .format(dateFormatter)
                );
                interactionDetails.setStyle(
                        "-fx-font-size: 12px;" +
                                "-fx-text-fill: #64748B;"
                );

                VBox interactionItem = new VBox(5);
                interactionItem.setPadding(new Insets(12));
                interactionItem.setStyle(
                        "-fx-background-color: #F8FAFC;" +
                                "-fx-background-radius: 8px;" +
                                "-fx-border-color: #E2E8F0;" +
                                "-fx-border-radius: 8px;"
                );

                interactionItem.getChildren().addAll(
                        subjectLabel,
                        interactionDetails
                );

                interactionList.getChildren().add(interactionItem);
            }
        }

        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setPrefWidth(420);
        section.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 10px;"
        );

        section.getChildren().addAll(
                sectionTitle,
                interactionList
        );

        return section;
    }

    private Map<String, String> getCustomerNames() {
        Map<String, String> customerNames = new HashMap<>();

        List<Customer> customers = customerRepository.findAll();

        for (Customer customer : customers) {
            String fullName = customer.getFirstName()+" "+ customer.getLastName();
            customerNames.put(
                    customer.getId().toHexString(),
                    fullName
            );
        }

        return customerNames;
    }

    public BorderPane getRoot() {
        return root;
    }
}