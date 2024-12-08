package org.ppg.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.ppg.model.Batch;
import org.ppg.model.PPGSchedulerException;
import org.ppg.model.Statuses;

public class BatchesListController {
    private final int ROWS_PER_PAGE = 11;
    Image logoPPG = new Image(String.valueOf(getClass().getResource("/images/PPG_Logo512_512.png")));
    private WeeklyBatchesListController WeeklyBatchesListController;
    private ObservableList<Batch> batchData = FXCollections.observableArrayList();
    private ObservableList<Batch> weeklyBatchData = FXCollections.observableArrayList();

    @FXML
    private TableView<Batch> tableView;
    @FXML
    private TableColumn<Batch, String> pClassCol;
    @FXML
    private TableColumn<Batch, String> plantCol;
    @FXML
    private TableColumn<Batch, String> itemCol;
    @FXML
    private TableColumn<Batch, String> startDateCol;
    @FXML
    private TableColumn<Batch, String> needDateCol;
    @FXML
    private TableColumn<Batch, String> nBatchCol;
    @FXML
    private TableColumn<Batch, String> quantityCol;
    @FXML
    private TableColumn<Batch, Void> colorCol;
    @FXML
    private TableColumn<Batch, String> statusCol;
    @FXML
    private TableColumn<Batch, Void> editCol;
    @FXML
    private Pagination pagination;
    @FXML
    private Label titleLabel;

    /**
     * Initializes the controller and sets up the table view and pagination. Configures the columns with specific
     * renderers, cell factories, and event handlers.
     *
     * @throws PPGSchedulerException if there is an error during initialization.
     */
    public void initialize() throws PPGSchedulerException {
        tableView.setSelectionModel(null);
        // Configures the columns of the table view
        nBatchCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[0];
            }
        });
        nBatchCol.setReorderable(false);
        pClassCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[1];
            }
        });
        pClassCol.setReorderable(false);
        plantCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[2];
            }
        });
        plantCol.setReorderable(false);
        itemCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[3];
            }
        });
        itemCol.setReorderable(false);
        quantityCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[4];
            }
        });
        quantityCol.setReorderable(false);
        startDateCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[5];
            }
        });
        startDateCol.setReorderable(false);
        needDateCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[6];
            }
        });
        needDateCol.setReorderable(false);
        addTooltipToCells(nBatchCol, 8);
        colorCol.setReorderable(false);
        colorCol.setCellFactory(param -> new TableCell<Batch, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                // If the row is empty or the cell is empty (null), nothing is rendered
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);  // Do not show graph
                    setText(null);      // Do not show text
                } else {
                    // If the row has a valid Batch object, we render the graph
                    Batch selectedBatch = getTableView().getItems().get(getIndex());

                    // Only show the graph if the Batch has a valid color in its state
                    if (selectedBatch != null && selectedBatch.getStatus() != null) {
                        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web(selectedBatch.getStatus().getHexColorPrimary())), new Stop(1, Color.web(selectedBatch.getStatus().getHexColorSecondary())));

                        Circle circle = new Circle(15);  // circle radius
                        circle.setFill(gradient);        // Fill Color
                        circle.setStroke(Color.BLACK);   // Border Color

                        // Set the chart in the cell
                        setGraphic(circle);
                        setText(null);  // Make sure you don't show text in the cell
                    } else {
                        setGraphic(null);  // If it does not have a valid state, no graph is displayed
                        setText(null);      // Don´t show the graf
                    }
                }
            }
        });

        // Setting the button to buttonNameColumn
        statusCol.setCellFactory(param -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    Batch selectedBatch = getTableView().getItems().get(getIndex());
                    if (selectedBatch == null) {
                        setText(null);
                    } else {
                        setText(selectedBatch.getStatus().getValue());
                    }
                    setStyle("-fx-alignment: center_left;-fx-font-weight: normal; -fx-font-size: 24px; -fx-font-family: 'Futura medium';");
                }
            }
        });
        statusCol.setReorderable(false);

        editCol.setCellFactory(param -> new TableCell<Batch, Void>() {
            private final Button btn = new Button("");

            {
                // Defines the action of the button
                btn.setOnAction(event -> {
                    Batch selectedBatch = getTableView().getItems().get(getIndex());
                    if (selectedBatch != null) {
                        openEditBatch(selectedBatch);
                    }
                });
                btn.setStyle("/*-fx-background-color: #ffffff;*/ -fx-background-radius: 0; -fx-alignment: center;" + " -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" + " -fx-background-image: url('/images/Engine128_128.png');" + " -fx-background-size: 60px 60px;" + " -fx-background-color: transparent;" + " -fx-background-position: center;" + " -fx-border-color: black;");

                //Styles for button on mouseover
                btn.setOnMouseEntered(event -> btn.setStyle("-fx-background-radius: 0; -fx-alignment: center;" + " -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" + " -fx-background-image: url('/images/Engine128_128.png');" + " -fx-background-size: 60px 60px;" + " -fx-background-color: transparent;" + " -fx-background-position: center;" + " -fx-border-color: black; -fx-scale-x: 1.05;" + " -fx-scale-y: 1.05;"));

                btn.setOnMouseExited(event -> btn.setStyle("-fx-background-radius: 0; -fx-alignment: center;" + " -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" + " -fx-background-image: url('/images/Engine128_128.png');" + " -fx-background-size: 60px 60px;" + " -fx-background-color: transparent;" + " -fx-background-position: center;" + " -fx-border-color: black; "));

                btn.setPrefHeight(Double.MAX_VALUE);
            }

            /**
             * Updates the graphical representation of a table cell based on the state of the batch data.
             *
             * @param item   Represents the data of the cell.
             * @param empty  Indicates whether this cell is empty (true) or contains data (false).
             */
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                // Check if the row is empty or if the Batch is null
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);  // Don't show button if empty
                } else {
                    Batch selectedBatch = getTableView().getItems().get(getIndex());
                    if (selectedBatch != null) {
                        setGraphic(btn);  // Show the button if the Batch is valid
                        setAlignment(Pos.CENTER);  // Align the button
                    } else {
                        setGraphic(null);  // Don't show the button if the Batch is null
                    }
                }
            }
        });

        editCol.setReorderable(false);

        // Pagination settings
        int totalPage = (int) (Math.ceil(batchData.size() * 1.0 / ROWS_PER_PAGE));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            changeTableView(newValue.intValue(), ROWS_PER_PAGE);
        });
        refreshTable();
        tableView.refresh();
    }

    /**
     * Updates the table view to display a specific page of batch data, ensuring correct pagination.
     *
     * @param index The index of the page to be displayed.
     * @param limit The number of rows to be displayed per page.
     */

    private void changeTableView(int index, int limit) {
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, batchData.size());
        int minIndex = Math.min(toIndex, batchData.size());

        //Create a batch list for the current page
        ObservableList<Batch> pageData = FXCollections.observableArrayList(batchData.subList(fromIndex, minIndex));

        // If there is space left on the page, add empty rows
        int remainingRows = limit - pageData.size();
        for (int i = 0; i < remainingRows; i++) {
            pageData.add(null);  //Adds an empty row represented by "null"
        }

        // Create a SortedList to ensure they are sorted correctly
        SortedList<Batch> sortedData = new SortedList<>(pageData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());

        tableView.setItems(sortedData);
    }

    /**
     * Adds tooltips to the cells of a specified table column.
     *
     * @param column        The table column to which tooltips should be added.
     * @param propertyIndex The index of the property in the batch data model used for the tooltip.
     */

    private void addTooltipToCells(TableColumn<Batch, String> column, int propertyIndex) {
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Batch, String> call(TableColumn<Batch, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            setText(item);
                            // Create and configure the Tooltip with the cell value
                            Batch batch = getTableView().getItems().get(getIndex());
                            StringProperty tooltipText;
                            if (batch != null) {
                                tooltipText = batch.getProperties()[propertyIndex];
                            } else {
                                tooltipText = new SimpleStringProperty("");
                            }
                            String text = divideText(tooltipText.getValue(), 50);
                            Tooltip tooltip = new Tooltip(text);
                            setTooltip(tooltip);
                            tooltip.setStyle("-fx-font-size: 20px; -fx-font-family: Futura medium;" + "-fx-background-color: #ffffff; -fx-text-fill: black; " + "-fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 0px;");
                        }
                    }
                };
            }
        });
    }

    /**
     * Divides a text into multiple lines with a specified maximum line length.
     *
     * @param text         The text to divide.
     * @param maxLineLength The maximum length of each line.
     * @return The text formatted with line breaks.
     */
    private String divideText(String text, int maxLineLength) {
        StringBuilder wrappedText = new StringBuilder();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxLineLength, text.length());
            // If the boundary does not fall on a space, adjust so as not to cut off words
            if (end < text.length() && text.charAt(end) != ' ') {
                while (end > start && text.charAt(end) != ' ') {
                    end--;
                }
            }
            //If you don't find space, cut the line directly
            if (end == start) {
                end = Math.min(start + maxLineLength, text.length());
            }
            wrappedText.append(text, start, end).append("\n");
            start = end + 1; // Advance to the next segment, jumping space
        }
        return wrappedText.toString().trim();
    }

    /**
     * Update the data table and pagination.
     */
    public void refreshTable() {
        int totalPage = (int) Math.ceil(batchData.size() * 1.0 / ROWS_PER_PAGE);
        pagination.setPageCount(totalPage);

        // Reload table data
        changeTableView(pagination.getCurrentPageIndex(), ROWS_PER_PAGE);
        tableView.refresh();
    }

    /**
     * Opens a popup window to edit a specific batch.
     * @param sampleBatch The object to be edited.
     */
    @FXML
    private void openEditBatch(Batch sampleBatch) {
        if (sampleBatch.getStatus() != Statuses.EN_ESPERA) {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorBatchInProgress.fxml")));
        } else {
            try {
                // Load the FXML file from the popup
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/EditBatches.fxml"));
                Parent popupRoot = fxmlLoader.load();
                EditBatchesController editBatches = fxmlLoader.getController();
                // Pass the batch object to the edit window controller
                editBatches.setBatch(sampleBatch);
                editBatches.setBatchData(batchData, weeklyBatchData);
                editBatches.setBatchesListController(this, WeeklyBatchesListController);
                // Create a new window for the popup
                Stage popupStage = new Stage();
                popupStage.resizableProperty().setValue(Boolean.FALSE);
                popupStage.setTitle("Editar lote");
                popupStage.getIcons().add(logoPPG);
                popupStage.initModality(Modality.APPLICATION_MODAL); // Lock the main window
                popupStage.setScene(new Scene(popupRoot));
                popupStage.setOnShown(event -> {
                    // Get dimensions of the main window or screen
                    double centerX = pagination.getScene().getWindow().getX() + pagination.getScene().getWindow().getWidth() / 2;
                    double centerY = pagination.getScene().getWindow().getY() + pagination.getScene().getWindow().getHeight() / 2;
                    // Calculate position to center the popup
                    popupStage.setX(centerX - popupStage.getWidth() / 2);
                    popupStage.setY(centerY - popupStage.getHeight() / 2);
                });
                editBatches.setStage(popupStage);
                // Show the popup
                popupStage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens a popup window to display an error message.
     * @param fxmlLoader
     */
    private void openError(FXMLLoader fxmlLoader) {
        try {
            // Load the FXML file from the popup
            Parent popupRoot = fxmlLoader.load();
            Stage popupStage = new Stage();
            popupStage.resizableProperty().setValue(Boolean.FALSE);
            popupStage.setTitle("ERROR");
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquear la ventana principal
            popupStage.setScene(new Scene(popupRoot));
            popupStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/warn_icon.png")));
            popupStage.setOnShown(event -> {
                // Get dimensions of the main window or screen
                double centerX = pagination.getScene().getWindow().getX() + pagination.getScene().getWindow().getWidth() / 2;
                double centerY = pagination.getScene().getWindow().getY() + pagination.getScene().getWindow().getHeight() / 2;
                // Calculate position to center the popup
                popupStage.setX(centerX - popupStage.getWidth() / 2);
                popupStage.setY(centerY - popupStage.getHeight() / 2);
            });
            popupStage.show();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), // Duración antes de ejecutar la acción
                    event -> popupStage.close() // Action to close the window
            ));
            timeline.setCycleCount(1); // Run only once
            timeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the lists of batches to be displayed in the table.
     * @param batchData The list of all batches to be displayed.
     * @param weeklyBatchData The list of batches for the current week to be displayed.
     */
    public void setBatchesList(ObservableList<Batch> batchData, ObservableList<Batch> weeklyBatchData) {
        this.batchData = batchData;
        this.weeklyBatchData = weeklyBatchData;
        this.refreshTable();

    }

    /**
     * Sets the controller for the weekly batches list.
     * @param WeeklyBatchesListController The controller that handles the interaction with the weekly batches list.
     */
    public void setBatchesListController(WeeklyBatchesListController WeeklyBatchesListController) {
        this.WeeklyBatchesListController = WeeklyBatchesListController;
    }


    /**
     * Checks whether the specified batch exists in the list of batches.
     * @param batch The batch to check for existence in the list.
     * @return {@code true} if the batch is present in the list, {@code false} otherwise.
     */
    public boolean contains(Batch batch) {
        for (int i = 0; i < batchData.size(); i++) {
            if (batchData.get(i).getnBatch() == batch.getnBatch()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the index of the specified batch in the list of batches.
     * @param batch The batch whose index is to be found.
     * @return The index of the batch in the list.
     */
    public int getIndex(Batch batch) {
        for (int i = 0; i < batchData.size(); i++) {
            if (batchData.get(i).getnBatch() == batch.getnBatch()) {
                return i;
            }
        }
        return -1;
    }
}
