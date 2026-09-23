package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Deal;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.model.Task;
import com.clientsphere.crm.model.User;
import com.clientsphere.crm.repository.CustomerRepository;
import com.clientsphere.crm.repository.DealRepository;
import com.clientsphere.crm.repository.InteractionRepository;
import com.clientsphere.crm.repository.TaskRepository;
import com.clientsphere.crm.util.CurrentUser;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class MainLayout {

    private final BorderPane root;

    private final CustomerRepository customerRepository;
    private final DealRepository dealRepository;
    private final InteractionRepository interactionRepository;
    private final TaskRepository taskRepository;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

    // ============================================================
    // RESPONSIVE COMPONENTS
    // ============================================================

    private VBox sidebar;

    private HBox topBar;

    private Label welcomeLabel;

    private final List<Button> sidebarButtons =
            new ArrayList<>();

    private Button activeButton;

    private ScrollPane dashboardScrollPane;

    private VBox dashboardContent;

    private GridPane summaryCards;

    private GridPane dashboardSections;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================

    public MainLayout() {

        root = new BorderPane();

        customerRepository =
                new CustomerRepository();

        dealRepository =
                new DealRepository();

        interactionRepository =
                new InteractionRepository();

        taskRepository =
                new TaskRepository();

        createTopBar();

        createSidebar();

        showDashboard();

        setupResponsiveLayout();
    }

    // ============================================================
    // TOP BAR
    // ============================================================

    private void createTopBar() {

        Label appTitle =
                new Label("ClientSphere CRM");

        appTitle.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: white;"
        );

        String userName = "User";
        String roleText = "User";

        if (CurrentUser.isLoggedIn()) {
            User currentUser = CurrentUser.getUser();

            userName = currentUser.getFirstName();

            roleText = currentUser.getRole() == User.Role.ADMIN
                    ? "Administrator"
                    : "HR / User";
        }

        welcomeLabel =
                new Label(
                        "Welcome, " +
                                userName +
                                "  •  " +
                                roleText
                );

        welcomeLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: white;"
        );

        topBar =
                new HBox(20);

        topBar.setAlignment(
                Pos.CENTER_LEFT
        );

        topBar.setPadding(
                new Insets(15, 25, 15, 25)
        );

        topBar.setStyle(
                "-fx-background-color: #1E293B;"
        );

        topBar.getChildren().addAll(
                appTitle,
                welcomeLabel
        );

        root.setTop(topBar);
    }

    // ============================================================
    // SIDEBAR
    // ============================================================

    private void createSidebar() {

        Button dashboardButton =
                createSidebarButton("Dashboard");

        Button customersButton =
                createSidebarButton("Customers");

        Button interactionsButton =
                createSidebarButton("Interactions");

        Button tasksButton =
                createSidebarButton("Tasks");

        Button dealsButton =
                createSidebarButton("Deals");

        Button analyticsButton =
                createSidebarButton("Analytics");

        Button settingsButton =
                createSidebarButton("Settings");

        // --------------------------------------------------------
        // DASHBOARD
        // --------------------------------------------------------

        dashboardButton.setOnAction(event -> {

            showDashboard();

            setActiveButton(
                    dashboardButton
            );
        });

        // --------------------------------------------------------
        // CUSTOMERS
        // --------------------------------------------------------

        customersButton.setOnAction(event -> {

            CustomerView customerView =
                    new CustomerView();

            root.setCenter(
                    customerView.getRoot()
            );

            setActiveButton(
                    customersButton
            );
        });

        // --------------------------------------------------------
        // INTERACTIONS
        // --------------------------------------------------------

        interactionsButton.setOnAction(event -> {

            InteractionView interactionView =
                    new InteractionView();

            root.setCenter(
                    interactionView.getView()
            );

            setActiveButton(
                    interactionsButton
            );
        });

        // --------------------------------------------------------
        // TASKS
        // --------------------------------------------------------

        tasksButton.setOnAction(event -> {

            TaskView taskView =
                    new TaskView();

            root.setCenter(
                    taskView.getView()
            );

            setActiveButton(
                    tasksButton
            );
        });

        // --------------------------------------------------------
        // DEALS
        // --------------------------------------------------------

        dealsButton.setOnAction(event -> {

            DealView dealView =
                    new DealView();

            root.setCenter(
                    dealView.getView()
            );

            setActiveButton(
                    dealsButton
            );
        });

        // --------------------------------------------------------
        // ANALYTICS
        // --------------------------------------------------------

        analyticsButton.setOnAction(event -> {

            AnalyticsView analyticsView =
                    new AnalyticsView();

            root.setCenter(
                    analyticsView.getView()
            );

            setActiveButton(
                    analyticsButton
            );
        });

        /*
         * Settings intentionally has no action because
         * SettingsView does not exist yet.
         */

        // --------------------------------------------------------
        // SIDEBAR
        // --------------------------------------------------------

        sidebar =
                new VBox(10);

        sidebar.setPadding(
                new Insets(25, 15, 25, 15)
        );

        sidebar.setPrefWidth(190);

        sidebar.setMinWidth(140);

        sidebar.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        sidebar.getChildren().addAll(
                dashboardButton,
                customersButton,
                interactionsButton,
                tasksButton,
                dealsButton,
                analyticsButton,
                settingsButton
        );

        sidebarButtons.clear();

        sidebarButtons.addAll(
                List.of(
                        dashboardButton,
                        customersButton,
                        interactionsButton,
                        tasksButton,
                        dealsButton,
                        analyticsButton,
                        settingsButton
                )
        );

        root.setLeft(sidebar);

        setActiveButton(
                dashboardButton
        );
    }

    // ============================================================
    // SIDEBAR BUTTON
    // ============================================================

    private Button createSidebarButton(
            String text
    ) {

        Button button =
                new Button(text);

        button.setMaxWidth(
                Double.MAX_VALUE
        );

        button.setAlignment(
                Pos.CENTER_LEFT
        );

        button.setPadding(
                new Insets(12, 15, 12, 15)
        );

        applyInactiveStyle(button);

        return button;
    }

    // ============================================================
    // ACTIVE SIDEBAR BUTTON
    // ============================================================

    private void setActiveButton(
            Button button
    ) {

        if (activeButton != null) {

            applyInactiveStyle(
                    activeButton
            );
        }

        activeButton = button;

        applyActiveStyle(
                activeButton
        );
    }

    // ============================================================
    // ACTIVE STYLE
    // ============================================================

    private void applyActiveStyle(
            Button button
    ) {

        double width =
                root.getWidth();

        if (width > 0 && width < 700) {

            button.setStyle(
                    "-fx-background-color: #E2E8F0;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-font-size: 12px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #0F172A;" +
                            "-fx-cursor: hand;"
            );

        } else if (width > 0 && width < 1000) {

            button.setStyle(
                    "-fx-background-color: #E2E8F0;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #0F172A;" +
                            "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: #E2E8F0;" +
                            "-fx-background-radius: 10px;" +
                            "-fx-font-size: 14px;" +
                            "-fx-font-weight: bold;" +
                            "-fx-text-fill: #0F172A;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    // ============================================================
    // INACTIVE STYLE
    // ============================================================

    private void applyInactiveStyle(
            Button button
    ) {

        double width =
                root.getWidth();

        if (width > 0 && width < 700) {

            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-font-size: 12px;" +
                            "-fx-text-fill: #334155;" +
                            "-fx-cursor: hand;"
            );

        } else if (width > 0 && width < 1000) {

            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-font-size: 13px;" +
                            "-fx-text-fill: #334155;" +
                            "-fx-cursor: hand;"
            );

        } else {

            button.setStyle(
                    "-fx-background-color: transparent;" +
                            "-fx-font-size: 14px;" +
                            "-fx-text-fill: #334155;" +
                            "-fx-cursor: hand;"
            );
        }
    }

    // ============================================================
    // DASHBOARD
    // ============================================================

    private void showDashboard() {

        int totalCustomers =
                customerRepository
                        .findAll()
                        .size();

        int totalInteractions =
                interactionRepository
                        .findAll()
                        .size();

        List<Task> allTasks =
                taskRepository.findAll();

        List<Deal> allDeals =
                dealRepository.findAll();

        long pendingTasks =
                allTasks.stream()
                        .filter(task ->
                                task.getStatus() != null &&
                                        task.getStatus()
                                                .equalsIgnoreCase(
                                                        "Pending"
                                                )
                        )
                        .count();

        long completedTasks =
                allTasks.stream()
                        .filter(task ->
                                task.getStatus() != null &&
                                        task.getStatus()
                                                .equalsIgnoreCase(
                                                        "Completed"
                                                )
                        )
                        .count();

        long openDeals =
                allDeals.stream()
                        .filter(deal ->
                                deal.getStatus() != null &&
                                        deal.getStatus()
                                                .equalsIgnoreCase(
                                                        "Open"
                                                )
                        )
                        .count();

        double openDealValue =
                allDeals.stream()
                        .filter(deal ->
                                deal.getStatus() != null &&
                                        deal.getStatus()
                                                .equalsIgnoreCase(
                                                        "Open"
                                                )
                        )
                        .mapToDouble(
                                Deal::getValue
                        )
                        .sum();

        String formattedOpenDealValue =
                String.format(
                        Locale.US,
                        "₦%,.2f",
                        openDealValue
                );

        // --------------------------------------------------------
        // TITLE
        // --------------------------------------------------------

        Label title =
                new Label("Dashboard");

        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        Label subtitle =
                new Label(
                        "Overview of your customers, deals, interactions, and tasks"
                );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        // --------------------------------------------------------
        // SUMMARY CARDS
        // --------------------------------------------------------

        VBox totalCustomersCard =
                createStatCard(
                        "Total Customers",
                        String.valueOf(
                                totalCustomers
                        ),
                        "#2563EB"
                );

        VBox openDealsCard =
                createStatCard(
                        "Open Deals",
                        String.valueOf(
                                openDeals
                        ),
                        "#7C3AED"
                );

        VBox openDealValueCard =
                createStatCard(
                        "Open Deal Value",
                        formattedOpenDealValue,
                        "#0891B2"
                );

        VBox pendingTasksCard =
                createStatCard(
                        "Pending Tasks",
                        String.valueOf(
                                pendingTasks
                        ),
                        "#D97706"
                );

        VBox completedTasksCard =
                createStatCard(
                        "Completed Tasks",
                        String.valueOf(
                                completedTasks
                        ),
                        "#16A34A"
                );

        VBox totalInteractionsCard =
                createStatCard(
                        "Total Interactions",
                        String.valueOf(
                                totalInteractions
                        ),
                        "#EA580C"
                );

        summaryCards =
                new GridPane();

        summaryCards.setHgap(15);

        summaryCards.setVgap(15);

        summaryCards.setAlignment(
                Pos.TOP_LEFT
        );

        summaryCards.setMinWidth(0);

        summaryCards.setMaxWidth(
                Double.MAX_VALUE
        );

        summaryCards.add(
                totalCustomersCard,
                0,
                0
        );

        summaryCards.add(
                openDealsCard,
                1,
                0
        );

        summaryCards.add(
                openDealValueCard,
                2,
                0
        );

        summaryCards.add(
                pendingTasksCard,
                3,
                0
        );

        summaryCards.add(
                completedTasksCard,
                4,
                0
        );

        summaryCards.add(
                totalInteractionsCard,
                5,
                0
        );

        // --------------------------------------------------------
        // DASHBOARD SECTIONS
        // --------------------------------------------------------

        VBox upcomingTasksSection =
                createUpcomingTasksSection(
                        allTasks
                );

        VBox recentInteractionsSection =
                createRecentInteractionsSection();

        dashboardSections =
                new GridPane();

        dashboardSections.setHgap(20);

        dashboardSections.setVgap(20);

        dashboardSections.setAlignment(
                Pos.TOP_LEFT
        );

        dashboardSections.setMinWidth(0);

        dashboardSections.setMaxWidth(
                Double.MAX_VALUE
        );

        dashboardSections.add(
                upcomingTasksSection,
                0,
                0
        );

        dashboardSections.add(
                recentInteractionsSection,
                1,
                0
        );

        // --------------------------------------------------------
        // DASHBOARD CONTENT
        // --------------------------------------------------------

        dashboardContent =
                new VBox(25);

        dashboardContent.setPadding(
                new Insets(30)
        );

        dashboardContent.setFillWidth(true);

        dashboardContent.setMinWidth(0);

        dashboardContent.setMaxWidth(
                Double.MAX_VALUE
        );

        dashboardContent.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        dashboardContent.getChildren().addAll(
                title,
                subtitle,
                summaryCards,
                dashboardSections
        );

        VBox.setVgrow(
                dashboardSections,
                Priority.NEVER
        );

        // --------------------------------------------------------
        // SCROLL PANE
        // --------------------------------------------------------

        dashboardScrollPane =
                new ScrollPane(
                        dashboardContent
                );

        dashboardScrollPane.setFitToWidth(
                true
        );

        dashboardScrollPane.setFitToHeight(
                false
        );

        dashboardScrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        dashboardScrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        dashboardScrollPane.setPannable(
                false
        );

        dashboardScrollPane.setStyle(
                "-fx-background-color: #F8FAFC;"
        );

        root.setCenter(
                dashboardScrollPane
        );

        updateDashboardResponsiveLayout();

        if (!sidebarButtons.isEmpty()) {

            setActiveButton(
                    sidebarButtons.get(0)
            );
        }
    }

    // ============================================================
    // STAT CARD
    // ============================================================

    private VBox createStatCard(
            String title,
            String value,
            String valueColor
    ) {

        Label titleLabel =
                new Label(title);

        titleLabel.setWrapText(true);

        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label valueLabel =
                new Label(value);

        valueLabel.setWrapText(true);

        valueLabel.setMaxWidth(
                Double.MAX_VALUE
        );

        valueLabel.setStyle(
                "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " +
                        valueColor + ";"
        );

        VBox card =
                new VBox(10);

        card.setPadding(
                new Insets(20)
        );

        card.setPrefWidth(220);

        card.setMinWidth(0);

        card.setPrefHeight(120);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

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

    // ============================================================
    // UPCOMING TASKS
    // ============================================================

    private VBox createUpcomingTasksSection(
            List<Task> allTasks
    ) {

        Label sectionTitle =
                new Label("Upcoming Tasks");

        sectionTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        VBox taskList =
                new VBox(12);

        taskList.setMaxWidth(
                Double.MAX_VALUE
        );

        Map<String, String> customerNames =
                getCustomerNames();

        List<Task> upcomingTasks =
                allTasks.stream()
                        .filter(task ->
                                task.getDueDate() != null
                        )
                        .filter(task ->
                                !task.getDueDate()
                                        .isBefore(
                                                LocalDateTime.now()
                                        )
                        )
                        .filter(task ->
                                task.getStatus() == null ||
                                        !task.getStatus()
                                                .equalsIgnoreCase(
                                                        "Completed"
                                                )
                        )
                        .sorted(
                                Comparator.comparing(
                                        Task::getDueDate
                                )
                        )
                        .limit(5)
                        .collect(
                                Collectors.toList()
                        );

        if (upcomingTasks.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "No upcoming tasks."
                    );

            emptyLabel.setWrapText(true);

            emptyLabel.setStyle(
                    "-fx-text-fill: #64748B;"
            );

            taskList.getChildren().add(
                    emptyLabel
            );

        } else {

            for (Task task :
                    upcomingTasks) {

                String customerName =
                        customerNames.getOrDefault(
                                task.getCustomerId()
                                        .toHexString(),
                                "Unknown Customer"
                        );

                Label taskTitle =
                        new Label(
                                task.getTitle()
                        );

                taskTitle.setWrapText(true);

                taskTitle.setStyle(
                        "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: #0F172A;"
                );

                Label taskDetails =
                        new Label(
                                customerName +
                                        " • Due " +
                                        task.getDueDate()
                                                .format(
                                                        dateFormatter
                                                )
                        );

                taskDetails.setWrapText(true);

                taskDetails.setStyle(
                        "-fx-font-size: 12px;" +
                                "-fx-text-fill: #64748B;"
                );

                VBox taskItem =
                        new VBox(5);

                taskItem.setPadding(
                        new Insets(12)
                );

                taskItem.setMaxWidth(
                        Double.MAX_VALUE
                );

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

                taskList.getChildren().add(
                        taskItem
                );
            }
        }

        VBox section =
                new VBox(15);

        section.setPadding(
                new Insets(20)
        );

        section.setPrefWidth(420);

        section.setMinWidth(0);

        section.setMaxWidth(
                Double.MAX_VALUE
        );

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

    // ============================================================
    // RECENT INTERACTIONS
    // ============================================================

    private VBox createRecentInteractionsSection() {

        Label sectionTitle =
                new Label(
                        "Recent Interactions"
                );

        sectionTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        VBox interactionList =
                new VBox(12);

        interactionList.setMaxWidth(
                Double.MAX_VALUE
        );

        Map<String, String> customerNames =
                getCustomerNames();

        List<Interaction> recentInteractions =
                interactionRepository
                        .findAll()
                        .stream()
                        .filter(interaction ->
                                interaction
                                        .getInteractionDate()
                                        != null
                        )
                        .sorted(
                                Comparator.comparing(
                                        Interaction::
                                                getInteractionDate
                                ).reversed()
                        )
                        .limit(5)
                        .collect(
                                Collectors.toList()
                        );

        if (recentInteractions.isEmpty()) {

            Label emptyLabel =
                    new Label(
                            "No interactions recorded."
                    );

            emptyLabel.setWrapText(true);

            emptyLabel.setStyle(
                    "-fx-text-fill: #64748B;"
            );

            interactionList.getChildren().add(
                    emptyLabel
            );

        } else {

            for (
                    Interaction interaction :
                    recentInteractions
            ) {

                String customerName =
                        customerNames.getOrDefault(
                                interaction
                                        .getCustomerId()
                                        .toHexString(),
                                "Unknown Customer"
                        );

                Label subjectLabel =
                        new Label(
                                interaction.getSubject()
                        );

                subjectLabel.setWrapText(true);

                subjectLabel.setStyle(
                        "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-text-fill: #0F172A;"
                );

                Label interactionDetails =
                        new Label(
                                customerName +
                                        " • " +
                                        interaction.getType() +
                                        " • " +
                                        interaction
                                                .getInteractionDate()
                                                .format(
                                                        dateFormatter
                                                )
                        );

                interactionDetails.setWrapText(true);

                interactionDetails.setStyle(
                        "-fx-font-size: 12px;" +
                                "-fx-text-fill: #64748B;"
                );

                VBox interactionItem =
                        new VBox(5);

                interactionItem.setPadding(
                        new Insets(12)
                );

                interactionItem.setMaxWidth(
                        Double.MAX_VALUE
                );

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

                interactionList.getChildren().add(
                        interactionItem
                );
            }
        }

        VBox section =
                new VBox(15);

        section.setPadding(
                new Insets(20)
        );

        section.setPrefWidth(420);

        section.setMinWidth(0);

        section.setMaxWidth(
                Double.MAX_VALUE
        );

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

    // ============================================================
    // CUSTOMER NAMES
    // ============================================================

    private Map<String, String> getCustomerNames() {

        Map<String, String> customerNames =
                new HashMap<>();

        List<Customer> customers =
                customerRepository.findAll();

        for (Customer customer :
                customers) {

            String fullName =
                    customer.getFirstName()
                            + " "
                            + customer.getLastName();

            customerNames.put(
                    customer.getId()
                            .toHexString(),
                    fullName
            );
        }

        return customerNames;
    }

    // ============================================================
    // RESPONSIVE LAYOUT
    // ============================================================

    private void setupResponsiveLayout() {

        ChangeListener<Number> widthListener =
                (observable, oldValue, newValue) ->
                        updateResponsiveLayout();

        root.widthProperty().addListener(
                widthListener
        );

        root.heightProperty().addListener(
                (observable, oldValue, newValue) ->
                        updateResponsiveLayout()
        );

        updateResponsiveLayout();
    }

    private void updateResponsiveLayout() {

        double width =
                root.getWidth();

        if (width <= 0) {
            return;
        }

        updateSidebar(width);

        updateTopBar(width);

        updateDashboardResponsiveLayout();

        if (activeButton != null) {

            applyActiveStyle(
                    activeButton
            );
        }
    }

    // ============================================================
    // RESPONSIVE SIDEBAR
    // ============================================================

    private void updateSidebar(
            double width
    ) {

        if (sidebar == null) {
            return;
        }

        if (width < 700) {

            sidebar.setPrefWidth(145);

            sidebar.setPadding(
                    new Insets(
                            15,
                            8,
                            15,
                            8
                    )
            );

            sidebar.setSpacing(5);

        } else if (width < 1000) {

            sidebar.setPrefWidth(165);

            sidebar.setPadding(
                    new Insets(
                            20,
                            10,
                            20,
                            10
                    )
            );

            sidebar.setSpacing(7);

        } else {

            sidebar.setPrefWidth(190);

            sidebar.setPadding(
                    new Insets(
                            25,
                            15,
                            25,
                            15
                    )
            );

            sidebar.setSpacing(10);
        }

        for (Button button :
                sidebarButtons) {

            if (button == activeButton) {

                applyActiveStyle(button);

            } else {

                applyInactiveStyle(button);
            }
        }
    }

    // ============================================================
    // RESPONSIVE TOP BAR
    // ============================================================

    private void updateTopBar(
            double width
    ) {

        if (topBar == null) {
            return;
        }

        if (width < 700) {

            topBar.setPadding(
                    new Insets(
                            12,
                            15,
                            12,
                            15
                    )
            );

            topBar.setSpacing(10);

            welcomeLabel.setVisible(false);

            welcomeLabel.setManaged(false);

        } else if (width < 1000) {

            topBar.setPadding(
                    new Insets(
                            14,
                            18,
                            14,
                            18
                    )
            );

            topBar.setSpacing(15);

            welcomeLabel.setVisible(true);

            welcomeLabel.setManaged(true);

        } else {

            topBar.setPadding(
                    new Insets(
                            15,
                            25,
                            15,
                            25
                    )
            );

            topBar.setSpacing(20);

            welcomeLabel.setVisible(true);

            welcomeLabel.setManaged(true);
        }
    }

    // ============================================================
    // RESPONSIVE DASHBOARD
    // ============================================================

    private void updateDashboardResponsiveLayout() {

        if (
                dashboardContent == null ||
                        summaryCards == null ||
                        dashboardSections == null ||
                        sidebar == null
        ) {
            return;
        }

        double width =
                root.getWidth();

        if (width <= 0) {
            return;
        }

        double horizontalPadding;

        // --------------------------------------------------------
        // PAGE PADDING
        // --------------------------------------------------------

        if (width < 700) {

            horizontalPadding = 20;

            dashboardContent.setPadding(
                    new Insets(20)
            );

        } else if (width < 1000) {

            horizontalPadding = 25;

            dashboardContent.setPadding(
                    new Insets(25)
            );

        } else {

            horizontalPadding = 30;

            dashboardContent.setPadding(
                    new Insets(30)
            );
        }

        // --------------------------------------------------------
        // AVAILABLE CONTENT WIDTH
        // --------------------------------------------------------

        double availableWidth =
                width
                        - sidebar.getPrefWidth()
                        - (horizontalPadding * 2);

        if (availableWidth <= 0) {
            return;
        }

        // --------------------------------------------------------
        // SUMMARY CARDS
        // --------------------------------------------------------

        int cardColumns;

        if (availableWidth >= 1100) {

            // FULL DESKTOP
            // 6 CARDS IN ONE ROW

            cardColumns = 6;

        } else if (availableWidth >= 700) {

            // MEDIUM
            // 3 CARDS PER ROW

            cardColumns = 3;

        } else if (availableWidth >= 450) {

            // SMALL
            // 2 CARDS PER ROW

            cardColumns = 2;

        } else {

            // VERY SMALL
            // 1 CARD PER ROW

            cardColumns = 1;
        }

        updateSummaryGrid(
                availableWidth,
                cardColumns
        );

        // --------------------------------------------------------
        // UPCOMING + RECENT
        // --------------------------------------------------------

        if (availableWidth >= 750) {

            // SIDE-BY-SIDE

            updateDashboardSections(
                    availableWidth,
                    2
            );

        } else {

            // STACKED

            updateDashboardSections(
                    availableWidth,
                    1
            );
        }

        dashboardContent.setMinWidth(0);

        dashboardContent.setMaxWidth(
                Double.MAX_VALUE
        );
    }

    // ============================================================
    // SUMMARY GRID
    // ============================================================

    private void updateSummaryGrid(
            double availableWidth,
            int columns
    ) {

        if (
                summaryCards == null ||
                        availableWidth <= 0
        ) {
            return;
        }

        summaryCards
                .getColumnConstraints()
                .clear();

        double gap = 15;

        double cardWidth =
                (
                        availableWidth
                                -
                                (gap * (columns - 1))
                )
                        / columns;

        if (cardWidth < 0) {
            cardWidth = 0;
        }

        // --------------------------------------------------------
        // CREATE COLUMN WIDTHS
        // --------------------------------------------------------

        for (
                int column = 0;
                column < columns;
                column++
        ) {

            ColumnConstraints constraints =
                    new ColumnConstraints();

            constraints.setPrefWidth(
                    cardWidth
            );

            constraints.setMinWidth(0);

            constraints.setMaxWidth(
                    cardWidth
            );

            summaryCards
                    .getColumnConstraints()
                    .add(constraints);
        }

        // --------------------------------------------------------
        // PLACE CARDS
        // --------------------------------------------------------

        for (
                int i = 0;
                i < summaryCards
                        .getChildren()
                        .size();
                i++
        ) {

            Node node =
                    summaryCards
                            .getChildren()
                            .get(i);

            int column =
                    i % columns;

            int row =
                    i / columns;

            GridPane.setColumnIndex(
                    node,
                    column
            );

            GridPane.setRowIndex(
                    node,
                    row
            );

            if (node instanceof VBox card) {

                card.setPrefWidth(
                        cardWidth
                );

                card.setMinWidth(0);

                card.setMaxWidth(
                        cardWidth
                );
            }
        }
    }

    // ============================================================
    // DASHBOARD SECTIONS GRID
    // ============================================================

    private void updateDashboardSections(
            double availableWidth,
            int columns
    ) {

        if (
                dashboardSections == null ||
                        availableWidth <= 0
        ) {
            return;
        }

        dashboardSections
                .getColumnConstraints()
                .clear();

        double gap = 20;

        double sectionWidth;

        if (columns == 2) {

            sectionWidth =
                    (
                            availableWidth
                                    - gap
                    ) / 2;

        } else {

            sectionWidth =
                    availableWidth;
        }

        if (sectionWidth < 0) {
            sectionWidth = 0;
        }

        // --------------------------------------------------------
        // COLUMN WIDTHS
        // --------------------------------------------------------

        for (
                int column = 0;
                column < columns;
                column++
        ) {

            ColumnConstraints constraints =
                    new ColumnConstraints();

            constraints.setPrefWidth(
                    sectionWidth
            );

            constraints.setMinWidth(0);

            constraints.setMaxWidth(
                    sectionWidth
            );

            dashboardSections
                    .getColumnConstraints()
                    .add(constraints);
        }

        // --------------------------------------------------------
        // UPCOMING TASKS
        // --------------------------------------------------------

        if (
                dashboardSections
                        .getChildren()
                        .size() > 0
        ) {

            Node node =
                    dashboardSections
                            .getChildren()
                            .get(0);

            if (node instanceof VBox section) {

                section.setPrefWidth(
                        sectionWidth
                );

                section.setMinWidth(0);

                section.setMaxWidth(
                        sectionWidth
                );

                GridPane.setColumnIndex(
                        section,
                        0
                );

                GridPane.setRowIndex(
                        section,
                        0
                );
            }
        }

        // --------------------------------------------------------
        // RECENT INTERACTIONS
        // --------------------------------------------------------

        if (
                dashboardSections
                        .getChildren()
                        .size() > 1
        ) {

            Node node =
                    dashboardSections
                            .getChildren()
                            .get(1);

            if (node instanceof VBox section) {

                section.setPrefWidth(
                        sectionWidth
                );

                section.setMinWidth(0);

                section.setMaxWidth(
                        sectionWidth
                );

                if (columns == 2) {

                    GridPane.setColumnIndex(
                            section,
                            1
                    );

                    GridPane.setRowIndex(
                            section,
                            0
                    );

                } else {

                    GridPane.setColumnIndex(
                            section,
                            0
                    );

                    GridPane.setRowIndex(
                            section,
                            1
                    );
                }
            }
        }
    }

    // ============================================================
    // ROOT
    // ============================================================

    public BorderPane getRoot() {

        return root;
    }
}
