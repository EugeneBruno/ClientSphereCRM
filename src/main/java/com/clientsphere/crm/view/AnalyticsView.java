package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Deal;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.model.Task;
import com.clientsphere.crm.repository.DealRepository;
import com.clientsphere.crm.repository.InteractionRepository;
import com.clientsphere.crm.repository.TaskRepository;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Locale;

public class AnalyticsView {

    private final BorderPane root;

    private final DealRepository dealRepository;
    private final TaskRepository taskRepository;
    private final InteractionRepository interactionRepository;

    public AnalyticsView() {
        root = new BorderPane();

        dealRepository = new DealRepository();
        taskRepository = new TaskRepository();
        interactionRepository = new InteractionRepository();

        createView();
    }

    private void createView() {
        List<Deal> allDeals = dealRepository.findAll();
        List<Task> allTasks = taskRepository.findAll();
        List<Interaction> allInteractions = interactionRepository.findAll();

        Label title = new Label("Analytics");
        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        Label subtitle = new Label(
                "Analyze deals, tasks, and customer interactions"
        );
        subtitle.setWrapText(true);
        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );

        VBox totalDealCard = createSummaryCard(
                "Total Deal Value",
                formatCurrency(getTotalDealValue(allDeals)),
                "#2563EB"
        );

        VBox wonDealCard = createSummaryCard(
                "Won Deal Value",
                formatCurrency(getDealValueByStatus(allDeals, "Won")),
                "#16A34A"
        );

        VBox lostDealCard = createSummaryCard(
                "Lost Deal Value",
                formatCurrency(getDealValueByStatus(allDeals, "Lost")),
                "#DC2626"
        );

        VBox totalDealsCard = createSummaryCard(
                "Total Deals",
                String.valueOf(allDeals.size()),
                "#7C3AED"
        );

        FlowPane summaryCards = new FlowPane();
        summaryCards.setHgap(15);
        summaryCards.setVgap(15);
        summaryCards.setAlignment(Pos.CENTER_LEFT);
        summaryCards.setMinWidth(0);
        summaryCards.setMaxWidth(Double.MAX_VALUE);

        summaryCards.getChildren().addAll(
                totalDealCard,
                wonDealCard,
                lostDealCard,
                totalDealsCard
        );

        PieChart dealsByStatusChart =
                createDealsByStatusChart(allDeals);

        BarChart<String, Number> taskStatusChart =
                createTaskStatusChart(allTasks);

        BarChart<String, Number> interactionTypeChart =
                createInteractionTypeChart(allInteractions);

        VBox dealsChartSection = createChartSection(
                "Deals by Status",
                dealsByStatusChart
        );

        VBox tasksChartSection = createChartSection(
                "Task Completion",
                taskStatusChart
        );

        VBox interactionsChartSection = createChartSection(
                "Interactions by Type",
                interactionTypeChart
        );

        FlowPane chartsContainer = new FlowPane();
        chartsContainer.setHgap(20);
        chartsContainer.setVgap(20);
        chartsContainer.setAlignment(Pos.TOP_LEFT);
        chartsContainer.setMinWidth(0);
        chartsContainer.setMaxWidth(Double.MAX_VALUE);

        chartsContainer.getChildren().addAll(
                dealsChartSection,
                tasksChartSection,
                interactionsChartSection
        );

        VBox content = new VBox(25);
        content.setPadding(new Insets(30));
        content.setFillWidth(true);
        content.setMinWidth(0);
        content.setMaxWidth(Double.MAX_VALUE);
        content.setStyle("-fx-background-color: #F8FAFC;");

        content.getChildren().addAll(
                title,
                subtitle,
                summaryCards,
                chartsContainer
        );

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );
        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );
        scrollPane.setPannable(true);
        scrollPane.setStyle("-fx-background-color: #F8FAFC;");

        /*
         * Keep the content width equal to the available viewport width.
         * This prevents the content from retaining an old larger width
         * when the window is manually resized.
         */
        content.prefWidthProperty().bind(
                Bindings.createDoubleBinding(
                        () -> scrollPane.getViewportBounds().getWidth(),
                        scrollPane.viewportBoundsProperty()
                )
        );

        /*
         * Responsive summary-card layout.
         */
        summaryCards.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {
                    updateSummaryCardWidths(
                            summaryCards,
                            totalDealCard,
                            wonDealCard,
                            lostDealCard,
                            totalDealsCard
                    );
                }
        );

        /*
         * Responsive chart layout.
         */
        chartsContainer.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {
                    updateChartSectionWidths(
                            chartsContainer,
                            dealsChartSection,
                            tasksChartSection,
                            interactionsChartSection
                    );
                }
        );

        /*
         * Run the first layout update after JavaFX calculates
         * the actual window and viewport dimensions.
         */
        Platform.runLater(() -> {
            updateSummaryCardWidths(
                    summaryCards,
                    totalDealCard,
                    wonDealCard,
                    lostDealCard,
                    totalDealsCard
            );

            updateChartSectionWidths(
                    chartsContainer,
                    dealsChartSection,
                    tasksChartSection,
                    interactionsChartSection
            );
        });

        root.setCenter(scrollPane);
    }

    private void updateSummaryCardWidths(
            FlowPane summaryCards,
            VBox... cards
    ) {
        double availableWidth = summaryCards.getWidth();

        if (availableWidth <= 0) {
            return;
        }

        double cardWidth;

        /*
         * Small screens:
         * One card per row.
         */
        if (availableWidth < 520) {
            cardWidth = Math.max(0, availableWidth - 5);
        }

        /*
         * Medium screens:
         * Two cards per row.
         */
        else if (availableWidth < 900) {
            cardWidth = (availableWidth - 15) / 2;
        }

        /*
         * Large screens:
         * Four cards can fit in one row.
         */
        else {
            cardWidth = (availableWidth - 45) / 4;
        }

        for (VBox card : cards) {
            card.setPrefWidth(cardWidth);
            card.setMinWidth(0);
            card.setMaxWidth(cardWidth);
        }
    }

    private void updateChartSectionWidths(
            FlowPane chartsContainer,
            VBox... sections
    ) {
        double availableWidth = chartsContainer.getWidth();

        if (availableWidth <= 0) {
            return;
        }

        double sectionWidth;

        /*
         * Large screens:
         * Two chart sections per row.
         */
        if (availableWidth >= 900) {
            sectionWidth = (availableWidth - 20) / 2;
        }

        /*
         * Medium and small screens:
         * One chart section per row.
         */
        else {
            sectionWidth = availableWidth;
        }

        for (VBox section : sections) {
            section.setPrefWidth(sectionWidth);
            section.setMinWidth(0);
            section.setMaxWidth(sectionWidth);
        }
    }

    private VBox createSummaryCard(
            String title,
            String value,
            String valueColor
    ) {
        Label titleLabel = new Label(title);
        titleLabel.setWrapText(true);
        titleLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748B;"
        );

        Label valueLabel = new Label(value);
        valueLabel.setWrapText(true);
        valueLabel.setStyle(
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " + valueColor + ";"
        );

        VBox card = new VBox(10);
        card.setPrefWidth(230);
        card.setMinWidth(0);
        card.setMaxWidth(Double.MAX_VALUE);
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

    private VBox createChartSection(
            String title,
            Node chart
    ) {
        Label sectionTitle = new Label(title);
        sectionTitle.setWrapText(true);
        sectionTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );

        VBox section = new VBox(15);
        section.setPadding(new Insets(20));
        section.setPrefWidth(500);
        section.setMinWidth(0);
        section.setMaxWidth(Double.MAX_VALUE);

        section.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 10px;" +
                        "-fx-border-color: #E2E8F0;" +
                        "-fx-border-radius: 10px;"
        );

        if (chart instanceof Region region) {
            region.setMinWidth(0);
            region.setMaxWidth(Double.MAX_VALUE);

            region.prefWidthProperty().bind(
                    section.widthProperty().subtract(40)
            );
        }

        section.getChildren().addAll(
                sectionTitle,
                chart
        );

        return section;
    }

    private PieChart createDealsByStatusChart(List<Deal> deals) {
        long openDeals = countDealsByStatus(deals, "Open");
        long wonDeals = countDealsByStatus(deals, "Won");
        long lostDeals = countDealsByStatus(deals, "Lost");

        PieChart chart = new PieChart(
                FXCollections.observableArrayList(
                        new PieChart.Data("Open", openDeals),
                        new PieChart.Data("Won", wonDeals),
                        new PieChart.Data("Lost", lostDeals)
                )
        );

        chart.setTitle("Deal Status");
        chart.setLegendVisible(true);
        chart.setLabelsVisible(false);
        chart.setMinWidth(0);
        chart.setPrefHeight(300);
        chart.setMaxHeight(350);

        return chart;
    }

    private BarChart<String, Number> createTaskStatusChart(
            List<Task> tasks
    ) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Status");
        yAxis.setLabel("Number of Tasks");

        BarChart<String, Number> chart =
                new BarChart<>(xAxis, yAxis);

        chart.setTitle("Tasks");
        chart.setLegendVisible(false);
        chart.setMinWidth(0);
        chart.setPrefHeight(300);
        chart.setMaxHeight(350);

        long pendingTasks = tasks.stream()
                .filter(task ->
                        task.getStatus() != null &&
                                task.getStatus().equalsIgnoreCase("Pending")
                )
                .count();

        long completedTasks = tasks.stream()
                .filter(task ->
                        task.getStatus() != null &&
                                task.getStatus().equalsIgnoreCase("Completed")
                )
                .count();

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        series.getData().add(
                new XYChart.Data<>("Pending", pendingTasks)
        );

        series.getData().add(
                new XYChart.Data<>("Completed", completedTasks)
        );

        chart.getData().add(series);

        return chart;
    }

    private BarChart<String, Number> createInteractionTypeChart(
            List<Interaction> interactions
    ) {
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Interaction Type");
        yAxis.setLabel("Number of Interactions");

        BarChart<String, Number> chart =
                new BarChart<>(xAxis, yAxis);

        chart.setTitle("Interactions");
        chart.setLegendVisible(false);
        chart.setMinWidth(0);
        chart.setPrefHeight(300);
        chart.setMaxHeight(350);

        long calls = countInteractionsByType(interactions, "Call");
        long meetings = countInteractionsByType(interactions, "Meeting");
        long emails = countInteractionsByType(interactions, "Email");
        long notes = countInteractionsByType(interactions, "Note");

        XYChart.Series<String, Number> series =
                new XYChart.Series<>();

        series.getData().add(
                new XYChart.Data<>("Call", calls)
        );

        series.getData().add(
                new XYChart.Data<>("Meeting", meetings)
        );

        series.getData().add(
                new XYChart.Data<>("Email", emails)
        );

        series.getData().add(
                new XYChart.Data<>("Note", notes)
        );

        chart.getData().add(series);

        return chart;
    }

    private long countDealsByStatus(
            List<Deal> deals,
            String status
    ) {
        return deals.stream()
                .filter(deal ->
                        deal.getStatus() != null &&
                                deal.getStatus().equalsIgnoreCase(status)
                )
                .count();
    }

    private double getDealValueByStatus(
            List<Deal> deals,
            String status
    ) {
        return deals.stream()
                .filter(deal ->
                        deal.getStatus() != null &&
                                deal.getStatus().equalsIgnoreCase(status)
                )
                .mapToDouble(Deal::getValue)
                .sum();
    }

    private double getTotalDealValue(List<Deal> deals) {
        return deals.stream()
                .mapToDouble(Deal::getValue)
                .sum();
    }

    private long countInteractionsByType(
            List<Interaction> interactions,
            String type
    ) {
        return interactions.stream()
                .filter(interaction ->
                        interaction.getType() != null &&
                                interaction.getType().equalsIgnoreCase(type)
                )
                .count();
    }

    private String formatCurrency(double amount) {
        return String.format(
                Locale.US,
                "₦%,.2f",
                amount
        );
    }

    public BorderPane getView() {
        return root;
    }
}