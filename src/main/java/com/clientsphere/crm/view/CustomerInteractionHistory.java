package com.clientsphere.crm.view;

import com.clientsphere.crm.model.Customer;
import com.clientsphere.crm.model.Interaction;
import com.clientsphere.crm.repository.InteractionRepository;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

public class CustomerInteractionHistory {

    private final Customer customer;

    private final Stage stage;

    private final InteractionRepository interactionRepository;

    private TableView<Interaction> interactionTable;

    private ObservableList<Interaction> interactions;

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern(
                    "dd MMM yyyy, HH:mm"
            );


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public CustomerInteractionHistory(
            Customer customer
    ) {

        this.customer = customer;

        this.stage = new Stage();

        this.interactionRepository =
                new InteractionRepository();

        this.interactions =
                FXCollections.observableArrayList();

        createView();
    }


    // =========================================================
    // CREATE VIEW
    // =========================================================

    private void createView() {

        // =====================================================
        // TITLE
        // =====================================================

        Label title =
                new Label(
                        customer.getFirstName()
                                + " "
                                + customer.getLastName()
                                + "'s Interaction History"
                );

        title.setWrapText(true);

        title.setStyle(
                "-fx-font-size: 24px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #334155;"
        );


        // =====================================================
        // CUSTOMER DETAILS
        // =====================================================

        Label customerDetails =
                new Label(
                        "Customer: "
                                + customer.getFirstName()
                                + " "
                                + customer.getLastName()
                                + " | "
                                + customer.getEmail()
                );

        customerDetails.setWrapText(true);

        customerDetails.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-text-fill: #64748B;"
        );


        // =====================================================
        // INTERACTION COUNT
        // =====================================================

        Label interactionCountLabel =
                new Label();

        interactionCountLabel.setWrapText(true);

        interactionCountLabel.setStyle(
                "-fx-font-size: 14px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-text-fill: #2563EB;"
        );


        // =====================================================
        // NO INTERACTIONS MESSAGE
        // =====================================================

        Label noInteractionsLabel =
                new Label(
                        "No interactions found for this customer."
                );

        noInteractionsLabel.setWrapText(true);

        noInteractionsLabel.setStyle(
                "-fx-font-size: 16px;" +
                        "-fx-text-fill: #64748B;"
        );

        noInteractionsLabel.setVisible(false);

        noInteractionsLabel.setManaged(false);


        // =====================================================
        // TABLE
        // =====================================================

        interactionTable =
                createInteractionTable();

        interactionTable.setMinWidth(0);

        interactionTable.setMaxWidth(
                Double.MAX_VALUE
        );

        VBox.setVgrow(
                interactionTable,
                Priority.ALWAYS
        );


        // =====================================================
        // MAIN LAYOUT
        // =====================================================

        VBox root =
                new VBox(
                        15,
                        title,
                        customerDetails,
                        interactionCountLabel,
                        noInteractionsLabel,
                        interactionTable
                );

        root.setFillWidth(true);

        root.setMinWidth(0);

        root.setMaxWidth(
                Double.MAX_VALUE
        );

        root.setPadding(
                new Insets(30)
        );


        // =====================================================
        // SCENE
        // =====================================================

        Scene scene =
                new Scene(
                        root,
                        900,
                        550
                );


        stage.setTitle(
                "Customer Interaction History"
        );

        stage.setScene(
                scene
        );

        stage.setMinWidth(500);

        stage.setMinHeight(400);

        stage.setResizable(true);


        // =====================================================
        // RESPONSIVE ROOT PADDING
        // =====================================================

        scene.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    double width =
                            newWidth.doubleValue();

                    if (width <= 0) {
                        return;
                    }


                    if (width < 650) {

                        root.setPadding(
                                new Insets(15)
                        );

                    } else if (width < 900) {

                        root.setPadding(
                                new Insets(20)
                        );

                    } else {

                        root.setPadding(
                                new Insets(30)
                        );
                    }
                }
        );


        // =====================================================
        // LOAD INTERACTIONS
        // =====================================================

        loadInteractions(
                interactionCountLabel,
                noInteractionsLabel
        );
    }


    // =========================================================
    // CREATE INTERACTION TABLE
    // =========================================================

    private TableView<Interaction>
    createInteractionTable() {

        TableView<Interaction> table =
                new TableView<>();


        table.setMinWidth(0);

        table.setMaxWidth(
                Double.MAX_VALUE
        );


        // =====================================================
        // TYPE
        // =====================================================

        TableColumn<Interaction, String>
                typeColumn =
                new TableColumn<>("Type");

        typeColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "type"
                )
        );


        // =====================================================
        // SUBJECT
        // =====================================================

        TableColumn<Interaction, String>
                subjectColumn =
                new TableColumn<>("Subject");

        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "subject"
                )
        );


        // =====================================================
        // DESCRIPTION
        // =====================================================

        TableColumn<Interaction, String>
                descriptionColumn =
                new TableColumn<>("Description");

        descriptionColumn.setCellValueFactory(
                new PropertyValueFactory<>(
                        "description"
                )
        );


        // =====================================================
        // DATE
        // =====================================================

        TableColumn<Interaction, String>
                dateColumn =
                new TableColumn<>("Date");

        dateColumn.setCellValueFactory(
                cellData -> {

                    LocalDateTime date =
                            cellData.getValue()
                                    .getInteractionDate();

                    String formattedDate =
                            date != null
                                    ? date.format(
                                    dateFormatter
                            )
                                    : "";

                    return new SimpleStringProperty(
                            formattedDate
                    );
                }
        );


        // =====================================================
        // COLUMN WIDTHS
        // =====================================================

        typeColumn.setMinWidth(100);

        typeColumn.setPrefWidth(120);


        subjectColumn.setMinWidth(160);

        subjectColumn.setPrefWidth(220);


        descriptionColumn.setMinWidth(250);

        descriptionColumn.setPrefWidth(350);


        dateColumn.setMinWidth(160);

        dateColumn.setPrefWidth(190);


        // =====================================================
        // ADD COLUMNS
        // =====================================================

        table.getColumns().addAll(
                typeColumn,
                subjectColumn,
                descriptionColumn,
                dateColumn
        );


        // =====================================================
        // TABLE DATA
        // =====================================================

        table.setItems(
                interactions
        );


        // =====================================================
        // INITIAL RESIZE POLICY
        // =====================================================

        table.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );


        // =====================================================
        // RESPONSIVE TABLE RESIZE
        // =====================================================

        table.widthProperty().addListener(
                (observable, oldWidth, newWidth) -> {

                    double width =
                            newWidth.doubleValue();

                    if (width <= 0) {
                        return;
                    }


                    /*
                     * Large window:
                     *
                     * Columns share the available width.
                     */

                    if (width >= 850) {

                        table.setColumnResizePolicy(
                                TableView.CONSTRAINED_RESIZE_POLICY
                        );
                    }


                    /*
                     * Medium / small window:
                     *
                     * Keep the columns readable.
                     * The TableView itself provides the
                     * horizontal scrollbar.
                     */

                    else {

                        table.setColumnResizePolicy(
                                TableView.UNCONSTRAINED_RESIZE_POLICY
                        );
                    }
                }
        );


        return table;
    }


    // =========================================================
    // LOAD CUSTOMER INTERACTIONS
    // =========================================================

    private void loadInteractions(
            Label interactionCountLabel,
            Label noInteractionsLabel
    ) {

        List<Interaction> interactionList =
                interactionRepository.findByCustomerId(
                        customer.getId()
                );


        // =====================================================
        // SORT NEWEST FIRST
        // =====================================================

        interactionList.sort(
                Comparator.comparing(
                        Interaction::getInteractionDate,
                        Comparator.nullsLast(
                                Comparator.reverseOrder()
                        )
                )
        );


        // =====================================================
        // UPDATE TABLE DATA
        // =====================================================

        interactions.clear();

        interactions.addAll(
                interactionList
        );


        // =====================================================
        // UPDATE COUNT
        // =====================================================

        interactionCountLabel.setText(
                "Total Interactions: "
                        + interactionList.size()
        );


        // =====================================================
        // EMPTY STATE
        // =====================================================

        if (interactionList.isEmpty()) {

            noInteractionsLabel.setVisible(
                    true
            );

            noInteractionsLabel.setManaged(
                    true
            );

            interactionTable.setVisible(
                    false
            );

            interactionTable.setManaged(
                    false
            );

        } else {

            noInteractionsLabel.setVisible(
                    false
            );

            noInteractionsLabel.setManaged(
                    false
            );

            interactionTable.setVisible(
                    true
            );

            interactionTable.setManaged(
                    true
            );
        }
    }


    // =========================================================
    // SHOW WINDOW
    // =========================================================

    public void show() {

        stage.show();
    }
}