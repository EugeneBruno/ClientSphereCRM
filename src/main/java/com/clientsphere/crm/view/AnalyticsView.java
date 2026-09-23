package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Deal;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.model.Task;
import com.clientsphere.crm.repository.DealRepository;
import com.clientsphere.crm.repository.InteractionRepository;
import com.clientsphere.crm.repository.TaskRepository;

import javafx.application.Platform;
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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
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

        dealRepository =
                new DealRepository();

        taskRepository =
                new TaskRepository();

        interactionRepository =
                new InteractionRepository();

        createView();
    }

    // =========================================================
    // MAIN VIEW
    // =========================================================

    private void createView() {

        List<Deal> allDeals =
                dealRepository.findAll();

        List<Task> allTasks =
                taskRepository.findAll();

        List<Interaction> allInteractions =
                interactionRepository.findAll();


        // =====================================================
        // TITLE
        // =====================================================

        Label title =
                new Label("Analytics");

        title.setStyle(
                "-fx-font-size: 28px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );


        Label subtitle =
                new Label(
                        "Analyze deals, tasks, and customer interactions"
                );

        subtitle.setWrapText(true);

        subtitle.setStyle(
                "-fx-font-size: 15px;" +
                        "-fx-text-fill: #64748B;"
        );


        // =====================================================
        // SUMMARY CARDS
        // =====================================================

        VBox totalDealCard =
                createSummaryCard(
                        "Total Deal Value",
                        formatCurrency(
                                getTotalDealValue(allDeals)
                        ),
                        "#2563EB"
                );


        VBox wonDealCard =
                createSummaryCard(
                        "Won Deal Value",
                        formatCurrency(
                                getDealValueByStatus(
                                        allDeals,
                                        "Won"
                                )
                        ),
                        "#16A34A"
                );


        VBox lostDealCard =
                createSummaryCard(
                        "Lost Deal Value",
                        formatCurrency(
                                getDealValueByStatus(
                                        allDeals,
                                        "Lost"
                                )
                        ),
                        "#DC2626"
                );


        VBox totalDealsCard =
                createSummaryCard(
                        "Total Deals",
                        String.valueOf(
                                allDeals.size()
                        ),
                        "#7C3AED"
                );


        /*
         * GRID FOR SUMMARY CARDS
         *
         * Large  = 4 columns
         * Medium = 2 columns
         * Small  = 1 column
         */

        GridPane summaryCards =
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
                totalDealCard,
                0,
                0
        );

        summaryCards.add(
                wonDealCard,
                1,
                0
        );

        summaryCards.add(
                lostDealCard,
                2,
                0
        );

        summaryCards.add(
                totalDealsCard,
                3,
                0
        );


        // =====================================================
        // CHARTS
        // =====================================================

        PieChart dealsByStatusChart =
                createDealsByStatusChart(
                        allDeals
                );


        BarChart<String, Number>
                taskStatusChart =
                createTaskStatusChart(
                        allTasks
                );


        BarChart<String, Number>
                interactionTypeChart =
                createInteractionTypeChart(
                        allInteractions
                );


        VBox dealsChartSection =
                createChartSection(
                        "Deals by Status",
                        dealsByStatusChart
                );


        VBox tasksChartSection =
                createChartSection(
                        "Task Completion",
                        taskStatusChart
                );


        VBox interactionsChartSection =
                createChartSection(
                        "Interactions by Type",
                        interactionTypeChart
                );


        /*
         * GRID FOR CHARTS
         *
         * This is intentional.
         *
         * We do NOT use FlowPane here because FlowPane
         * can retain an incorrect wrap width when the
         * window is manually resized.
         */

        GridPane chartsContainer =
                new GridPane();

        chartsContainer.setHgap(20);

        chartsContainer.setVgap(20);

        chartsContainer.setAlignment(
                Pos.TOP_LEFT
        );

        chartsContainer.setMinWidth(0);

        chartsContainer.setMaxWidth(
                Double.MAX_VALUE
        );


        chartsContainer.add(
                dealsChartSection,
                0,
                0
        );

        chartsContainer.add(
                tasksChartSection,
                1,
                0
        );

        chartsContainer.add(
                interactionsChartSection,
                0,
                1
        );


        // =====================================================
        // MAIN CONTENT
        // =====================================================

        VBox content =
                new VBox(25);

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


        content.getChildren().addAll(
                title,
                subtitle,
                summaryCards,
                chartsContainer
        );


        // =====================================================
        // SCROLL PANE
        // =====================================================

        ScrollPane scrollPane =
                new ScrollPane(
                        content
                );

        scrollPane.setFitToWidth(true);

        scrollPane.setFitToHeight(false);

        scrollPane.setHbarPolicy(
                ScrollPane.ScrollBarPolicy.NEVER
        );

        scrollPane.setVbarPolicy(
                ScrollPane.ScrollBarPolicy.AS_NEEDED
        );

        scrollPane.setPannable(true);

        scrollPane.setStyle(
                "-fx-background-color: #F8FAFC;"
        );


        root.setCenter(
                scrollPane
        );


        // =====================================================
        // RESPONSIVE LISTENER
        // =====================================================

        scrollPane.viewportBoundsProperty()
                .addListener(
                        (observable, oldBounds, newBounds) ->
                                updateResponsiveLayout(
                                        scrollPane,
                                        content,
                                        summaryCards,
                                        chartsContainer,

                                        totalDealCard,
                                        wonDealCard,
                                        lostDealCard,
                                        totalDealsCard,

                                        dealsChartSection,
                                        tasksChartSection,
                                        interactionsChartSection
                                )
                );


        root.widthProperty().addListener(
                (observable, oldWidth, newWidth) ->
                        updateResponsiveLayout(
                                scrollPane,
                                content,
                                summaryCards,
                                chartsContainer,

                                totalDealCard,
                                wonDealCard,
                                lostDealCard,
                                totalDealsCard,

                                dealsChartSection,
                                tasksChartSection,
                                interactionsChartSection
                        )
        );


        Platform.runLater(() ->
                updateResponsiveLayout(
                        scrollPane,
                        content,
                        summaryCards,
                        chartsContainer,

                        totalDealCard,
                        wonDealCard,
                        lostDealCard,
                        totalDealsCard,

                        dealsChartSection,
                        tasksChartSection,
                        interactionsChartSection
                )
        );
    }


    // =========================================================
    // RESPONSIVE LAYOUT
    // =========================================================

    private void updateResponsiveLayout(
            ScrollPane scrollPane,
            VBox content,
            GridPane summaryCards,
            GridPane chartsContainer,

            VBox totalDealCard,
            VBox wonDealCard,
            VBox lostDealCard,
            VBox totalDealsCard,

            VBox dealsChartSection,
            VBox tasksChartSection,
            VBox interactionsChartSection
    ) {

        double viewportWidth =
                scrollPane.getViewportBounds()
                        .getWidth();


        if (viewportWidth <= 0) {
            return;
        }


        // =====================================================
        // CONTENT PADDING
        // =====================================================

        double padding;


        if (viewportWidth < 600) {

            padding = 15;

        } else if (viewportWidth < 1000) {

            padding = 25;

        } else {

            padding = 30;
        }


        content.setPadding(
                new Insets(padding)
        );


        content.setPrefWidth(
                viewportWidth
        );


        // =====================================================
        // AVAILABLE WIDTH
        // =====================================================

        double availableWidth =
                Math.max(
                        0,
                        viewportWidth -
                                (padding * 2)
                );


        // =====================================================
        // SUMMARY CARDS
        // =====================================================

        updateSummaryCards(
                summaryCards,

                totalDealCard,
                wonDealCard,
                lostDealCard,
                totalDealsCard,

                availableWidth
        );


        // =====================================================
        // CHARTS
        // =====================================================

        updateCharts(
                chartsContainer,

                dealsChartSection,
                tasksChartSection,
                interactionsChartSection,

                availableWidth
        );
    }


    // =========================================================
    // SUMMARY CARDS
    // =========================================================

    private void updateSummaryCards(
            GridPane summaryCards,

            VBox totalDealCard,
            VBox wonDealCard,
            VBox lostDealCard,
            VBox totalDealsCard,

            double availableWidth
    ) {

        summaryCards
                .getColumnConstraints()
                .clear();


        // =====================================================
        // SMALL
        // =====================================================

        if (availableWidth < 600) {

            addColumn(
                    summaryCards,
                    100
            );


            moveCard(
                    totalDealCard,
                    0,
                    0
            );

            moveCard(
                    wonDealCard,
                    0,
                    1
            );

            moveCard(
                    lostDealCard,
                    0,
                    2
            );

            moveCard(
                    totalDealsCard,
                    0,
                    3
            );


            double cardWidth =
                    availableWidth;


            setCardWidth(
                    totalDealCard,
                    cardWidth
            );

            setCardWidth(
                    wonDealCard,
                    cardWidth
            );

            setCardWidth(
                    lostDealCard,
                    cardWidth
            );

            setCardWidth(
                    totalDealsCard,
                    cardWidth
            );
        }


        // =====================================================
        // MEDIUM
        // =====================================================

        else if (availableWidth < 1000) {

            addColumn(
                    summaryCards,
                    50
            );

            addColumn(
                    summaryCards,
                    50
            );


            moveCard(
                    totalDealCard,
                    0,
                    0
            );

            moveCard(
                    wonDealCard,
                    1,
                    0
            );

            moveCard(
                    lostDealCard,
                    0,
                    1
            );

            moveCard(
                    totalDealsCard,
                    1,
                    1
            );


            double cardWidth =
                    (availableWidth - 15) / 2;


            setCardWidth(
                    totalDealCard,
                    cardWidth
            );

            setCardWidth(
                    wonDealCard,
                    cardWidth
            );

            setCardWidth(
                    lostDealCard,
                    cardWidth
            );

            setCardWidth(
                    totalDealsCard,
                    cardWidth
            );
        }


        // =====================================================
        // LARGE
        // =====================================================

        else {

            addColumn(
                    summaryCards,
                    25
            );

            addColumn(
                    summaryCards,
                    25
            );

            addColumn(
                    summaryCards,
                    25
            );

            addColumn(
                    summaryCards,
                    25
            );


            moveCard(
                    totalDealCard,
                    0,
                    0
            );

            moveCard(
                    wonDealCard,
                    1,
                    0
            );

            moveCard(
                    lostDealCard,
                    2,
                    0
            );

            moveCard(
                    totalDealsCard,
                    3,
                    0
            );


            double cardWidth =
                    (availableWidth - 45) / 4;


            setCardWidth(
                    totalDealCard,
                    cardWidth
            );

            setCardWidth(
                    wonDealCard,
                    cardWidth
            );

            setCardWidth(
                    lostDealCard,
                    cardWidth
            );

            setCardWidth(
                    totalDealsCard,
                    cardWidth
            );
        }
    }


    // =========================================================
    // CHART RESPONSIVENESS
    // =========================================================

    private void updateCharts(
            GridPane chartsContainer,

            VBox dealsChartSection,
            VBox tasksChartSection,
            VBox interactionsChartSection,

            double availableWidth
    ) {

        chartsContainer
                .getColumnConstraints()
                .clear();


        // =====================================================
        // SMALL
        // =====================================================

        if (availableWidth < 950) {

            /*
             * One chart per row.
             */

            addColumn(
                    chartsContainer,
                    100
            );


            moveChart(
                    dealsChartSection,
                    0,
                    0
            );

            moveChart(
                    tasksChartSection,
                    0,
                    1
            );

            moveChart(
                    interactionsChartSection,
                    0,
                    2
            );


            setChartWidth(
                    dealsChartSection,
                    availableWidth
            );

            setChartWidth(
                    tasksChartSection,
                    availableWidth
            );

            setChartWidth(
                    interactionsChartSection,
                    availableWidth
            );
        }


        // =====================================================
        // LARGE
        // =====================================================

        else {

            /*
             * Two charts per row.
             */

            addColumn(
                    chartsContainer,
                    50
            );

            addColumn(
                    chartsContainer,
                    50
            );


            moveChart(
                    dealsChartSection,
                    0,
                    0
            );

            moveChart(
                    tasksChartSection,
                    1,
                    0
            );

            moveChart(
                    interactionsChartSection,
                    0,
                    1
            );


            double chartWidth =
                    (availableWidth - 20) / 2;


            setChartWidth(
                    dealsChartSection,
                    chartWidth
            );

            setChartWidth(
                    tasksChartSection,
                    chartWidth
            );

            setChartWidth(
                    interactionsChartSection,
                    chartWidth
            );
        }
    }


    // =========================================================
    // ADD GRID COLUMN
    // =========================================================

    private void addColumn(
            GridPane grid,
            double percentage
    ) {

        ColumnConstraints column =
                new ColumnConstraints();

        column.setPercentWidth(
                percentage
        );

        column.setHgrow(
                Priority.ALWAYS
        );

        grid.getColumnConstraints()
                .add(column);
    }


    // =========================================================
    // MOVE SUMMARY CARD
    // =========================================================

    private void moveCard(
            VBox card,
            int column,
            int row
    ) {

        GridPane.setColumnIndex(
                card,
                column
        );

        GridPane.setRowIndex(
                card,
                row
        );
    }


    // =========================================================
    // MOVE CHART
    // =========================================================

    private void moveChart(
            VBox chart,
            int column,
            int row
    ) {

        GridPane.setColumnIndex(
                chart,
                column
        );

        GridPane.setRowIndex(
                chart,
                row
        );
    }


    // =========================================================
    // CARD WIDTH
    // =========================================================

    private void setCardWidth(
            VBox card,
            double width
    ) {

        card.setPrefWidth(
                width
        );

        card.setMinWidth(0);

        card.setMaxWidth(
                width
        );

        GridPane.setHgrow(
                card,
                Priority.ALWAYS
        );

        GridPane.setFillWidth(
                card,
                true
        );
    }


    // =========================================================
    // CHART WIDTH
    // =========================================================

    private void setChartWidth(
            VBox section,
            double width
    ) {

        section.setPrefWidth(
                width
        );

        section.setMinWidth(0);

        section.setMaxWidth(
                width
        );

        GridPane.setHgrow(
                section,
                Priority.ALWAYS
        );

        GridPane.setFillWidth(
                section,
                true
        );
    }


    // =========================================================
    // SUMMARY CARD
    // =========================================================

    private VBox createSummaryCard(
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
                "-fx-font-size: 22px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: " +
                        valueColor + ";"
        );


        VBox card =
                new VBox(10);

        card.setPrefWidth(230);

        card.setMinWidth(0);

        card.setMaxWidth(
                Double.MAX_VALUE
        );

        card.setPrefHeight(120);

        card.setPadding(
                new Insets(20)
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


    // =========================================================
    // CHART SECTION
    // =========================================================

    private VBox createChartSection(
            String title,
            Node chart
    ) {

        Label sectionTitle =
                new Label(title);

        sectionTitle.setWrapText(true);

        sectionTitle.setStyle(
                "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #0F172A;"
        );


        VBox section =
                new VBox(15);

        section.setPadding(
                new Insets(20)
        );

        section.setPrefWidth(500);

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


        if (chart instanceof Region region) {

            region.setMinWidth(0);

            region.setMaxWidth(
                    Double.MAX_VALUE
            );

            region.prefWidthProperty().bind(
                    section.widthProperty()
                            .subtract(40)
            );
        }


        section.getChildren().addAll(
                sectionTitle,
                chart
        );


        return section;
    }


    // =========================================================
    // DEAL STATUS CHART
    // =========================================================

    private PieChart createDealsByStatusChart(
            List<Deal> deals
    ) {

        long openDeals =
                countDealsByStatus(
                        deals,
                        "Open"
                );

        long wonDeals =
                countDealsByStatus(
                        deals,
                        "Won"
                );

        long lostDeals =
                countDealsByStatus(
                        deals,
                        "Lost"
                );


        PieChart chart =
                new PieChart(
                        FXCollections.observableArrayList(
                                new PieChart.Data(
                                        "Open",
                                        openDeals
                                ),
                                new PieChart.Data(
                                        "Won",
                                        wonDeals
                                ),
                                new PieChart.Data(
                                        "Lost",
                                        lostDeals
                                )
                        )
                );


        chart.setTitle(
                "Deal Status"
        );

        chart.setLegendVisible(true);

        chart.setLabelsVisible(false);

        chart.setMinWidth(0);

        chart.setPrefHeight(300);

        chart.setMaxHeight(350);


        return chart;
    }


    // =========================================================
    // TASK STATUS CHART
    // =========================================================

    private BarChart<String, Number>
    createTaskStatusChart(
            List<Task> tasks
    ) {

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis();


        xAxis.setLabel(
                "Status"
        );

        yAxis.setLabel(
                "Number of Tasks"
        );


        BarChart<String, Number>
                chart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );


        chart.setTitle(
                "Tasks"
        );

        chart.setLegendVisible(false);

        chart.setMinWidth(0);

        chart.setPrefHeight(300);

        chart.setMaxHeight(350);


        long pendingTasks =
                tasks.stream()
                        .filter(task ->
                                task.getStatus() != null &&
                                        task.getStatus()
                                                .equalsIgnoreCase(
                                                        "Pending"
                                                )
                        )
                        .count();


        long completedTasks =
                tasks.stream()
                        .filter(task ->
                                task.getStatus() != null &&
                                        task.getStatus()
                                                .equalsIgnoreCase(
                                                        "Completed"
                                                )
                        )
                        .count();


        XYChart.Series<String, Number>
                series =
                new XYChart.Series<>();


        series.getData().add(
                new XYChart.Data<>(
                        "Pending",
                        pendingTasks
                )
        );


        series.getData().add(
                new XYChart.Data<>(
                        "Completed",
                        completedTasks
                )
        );


        chart.getData().add(
                series
        );


        return chart;
    }


    // =========================================================
    // INTERACTION TYPE CHART
    // =========================================================

    private BarChart<String, Number>
    createInteractionTypeChart(
            List<Interaction> interactions
    ) {

        CategoryAxis xAxis =
                new CategoryAxis();

        NumberAxis yAxis =
                new NumberAxis();


        xAxis.setLabel(
                "Interaction Type"
        );

        yAxis.setLabel(
                "Number of Interactions"
        );


        BarChart<String, Number>
                chart =
                new BarChart<>(
                        xAxis,
                        yAxis
                );


        chart.setTitle(
                "Interactions"
        );

        chart.setLegendVisible(false);

        chart.setMinWidth(0);

        chart.setPrefHeight(300);

        chart.setMaxHeight(350);


        long calls =
                countInteractionsByType(
                        interactions,
                        "Call"
                );

        long meetings =
                countInteractionsByType(
                        interactions,
                        "Meeting"
                );

        long emails =
                countInteractionsByType(
                        interactions,
                        "Email"
                );

        long notes =
                countInteractionsByType(
                        interactions,
                        "Note"
                );


        XYChart.Series<String, Number>
                series =
                new XYChart.Series<>();


        series.getData().add(
                new XYChart.Data<>(
                        "Call",
                        calls
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Meeting",
                        meetings
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Email",
                        emails
                )
        );

        series.getData().add(
                new XYChart.Data<>(
                        "Note",
                        notes
                )
        );


        chart.getData().add(
                series
        );


        return chart;
    }


    // =========================================================
    // DEAL HELPERS
    // =========================================================

    private long countDealsByStatus(
            List<Deal> deals,
            String status
    ) {

        return deals.stream()
                .filter(deal ->
                        deal.getStatus() != null &&
                                deal.getStatus()
                                        .equalsIgnoreCase(
                                                status
                                        )
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
                                deal.getStatus()
                                        .equalsIgnoreCase(
                                                status
                                        )
                )
                .mapToDouble(
                        Deal::getValue
                )
                .sum();
    }


    private double getTotalDealValue(
            List<Deal> deals
    ) {

        return deals.stream()
                .mapToDouble(
                        Deal::getValue
                )
                .sum();
    }


    // =========================================================
    // INTERACTION HELPERS
    // =========================================================

    private long countInteractionsByType(
            List<Interaction> interactions,
            String type
    ) {

        return interactions.stream()
                .filter(interaction ->
                        interaction.getType() != null &&
                                interaction.getType()
                                        .equalsIgnoreCase(
                                                type
                                        )
                )
                .count();
    }


    // =========================================================
    // CURRENCY
    // =========================================================

    private String formatCurrency(
            double amount
    ) {

        return String.format(
                Locale.US,
                "₦%,.2f",
                amount
        );
    }


    // =========================================================
    // ROOT
    // =========================================================

    public BorderPane getView() {
        return root;
    }
}