package org.ppg.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.ppg.model.Batch;
import org.ppg.model.DatabaseManager;
import org.ppg.model.PPGSchedulerException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class EditBatchesController {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ObservableList<Batch> batchData;
    private ObservableList<Batch> weeklyBatchData;
    private BatchesListController batchesListController;
    private WeeklyBatchesListController weeklyBatchesListController;
    private Stage stage;
    private Batch batch;
    @FXML
    private TextField nBatchField;
    @FXML
    private TextField pClassField;
    @FXML
    private TextField plantField;
    @FXML
    private TextField itemField;
    @FXML
    private TextField quantityField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private TextField needDateField;
    @FXML
    private TextArea descriptionField;
    private DatabaseManager databaseManager;

    /**
     * Sets the values of the fields in the batch edit form based on the properties of the provided.
     * @param batch
     */
    public void setBatch(Batch batch) {
        nBatchField.setText(String.valueOf(batch.getnBatch()));
        pClassField.setText(batch.getPlanningClass());
        plantField.setText(batch.getPlant());
        itemField.setText(batch.getItem());
        quantityField.setText(String.valueOf(batch.getQuantity()));
        startDatePicker.setValue(batch.getStartDate());
        needDateField.setText(batch.getNeedDate().format(formatter));
        descriptionField.setText(batch.getDescription());
        this.batch = batch;
    }

    /**
     * Initialize text fields.
     */
    public void initialize() {
        try {
            databaseManager = databaseManager.getInstance();
        } catch (PPGSchedulerException e) {
            e.printStackTrace();
        }
        startDatePicker.setShowWeekNumbers(false);
        startDatePicker.getEditor().setContextMenu(new ContextMenu());
        nBatchField.setContextMenu(new ContextMenu());
        pClassField.setContextMenu(new ContextMenu());
        plantField.setContextMenu(new ContextMenu());
        itemField.setContextMenu(new ContextMenu());
        quantityField.setContextMenu(new ContextMenu());
        needDateField.setContextMenu(new ContextMenu());
        descriptionField.setContextMenu(new ContextMenu());
    }

    /**
     * Sets the data for the batches.
     * @param batchData All batches to be used in the application.
     * @param weeklyBatchData The batches specifically for the current week.
     */
    public void setBatchData(ObservableList<Batch> batchData, ObservableList<Batch> weeklyBatchData) {
        this.batchData = batchData;
        this.weeklyBatchData = weeklyBatchData;
    }

    /**
     * Sets the controllers for the batches list.
     * @param batchesListController The controller that manages the general batches list view.
     * @param WeeklyBatchesListController The controller that manages the weekly batches list view.
     */
    public void setBatchesListController(BatchesListController batchesListController, WeeklyBatchesListController WeeklyBatchesListController) {
        this.batchesListController = batchesListController;
        this.weeklyBatchesListController = WeeklyBatchesListController;
    }

    /**
     * Sets the stage for the current window.
     * @param stage The object representing the window for this controller.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     *  Updates the Batch with the new start date.
     * @param batch
     */
    private void updateBatch(Batch batch) {
        batch.setStartDate(startDatePicker.getValue());
    }

    /**
     * Removes the selected batch in the UI.
     */
    @FXML
    private void removeBatch() {
        this.stage.close();
        batchData.remove(batchData.indexOf(batch));
        if (weeklyBatchesListController.contains(batch, weeklyBatchData)) {
            int index = weeklyBatchesListController.getIndex(batch, weeklyBatchData);
            weeklyBatchData.remove(index);
        }
        batchesListController.refreshTable();
        weeklyBatchesListController.refreshTable();
            /*
        try {
           UNCOMMENTING THIS LINE PUT THE INTEGRITY OF THE DB AT RISK SINCE IT ELIMINATES THE SELECTED LOT
            databaseManager.deleteBatch(batch.getnBatch());
        } catch (PPGSchedulerException e) {
            e.printStackTrace();
        }
             */
    }

    /**
     * Modifies the selected batch based on the input values in the UI.
     */
    @FXML
    private void modifyBatch() {
        if (startDatePicker.getValue().isBefore(LocalDate.parse(needDateField.getText(), formatter))) {
            if (startDatePicker.getValue().isBefore(LocalDate.now())) {
                openError(new FXMLLoader(getClass().getResource("/fxml/errorDate.fxml")));
            } else {
                updateBatch(batch);
                /*
                try {
            UNCOMMENTING THIS LINE PUT THE INTEGRITY OF THE DB AT RISK SINCE IT MODIFIES THE SELECTED LOT
                    databaseManager.updateBatchDB(batch);
                } catch (PPGSchedulerException e) {
                    e.printStackTrace();
                }
                */
                this.stage.hide();
                if (weeklyBatchesListController.contains(batch, weeklyBatchData)) {
                    int index = weeklyBatchesListController.getIndex(batch, weeklyBatchData);
                    weeklyBatchData.set(index, batch);
                }
                weeklyBatchesListController.refreshTable();
                batchesListController.refreshTable();
            }
        } else {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorModifyPopup.fxml")));
        }
    }

    /**
     *  Opens an error popup window with a specific message.
     * @param fxmlLoader The object that loads the FXML file for the popup content.
     */
    private void openError(FXMLLoader fxmlLoader) {
        try {
            // Load the FXML file from the popup
            Parent popupRoot = fxmlLoader.load();
            Stage popupStage = new Stage();
            popupStage.resizableProperty().setValue(Boolean.FALSE);
            popupStage.setTitle("ERROR");
            popupStage.initModality(Modality.APPLICATION_MODAL); // Lock the main window
            popupStage.setScene(new Scene(popupRoot));
            popupStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/warn_icon.png")));
            popupStage.setOnShown(event -> {
                // Get dimensions of the main window or screen
                double centerX = descriptionField.getScene().getWindow().getX() + descriptionField.getScene().getWindow().getWidth() / 2;
                double centerY = descriptionField.getScene().getWindow().getY() + descriptionField.getScene().getWindow().getHeight() / 2;
                // Calculate position to center the popup
                popupStage.setX(centerX - popupStage.getWidth() / 2);
                popupStage.setY(centerY - popupStage.getHeight() / 2);
            });
            popupStage.show();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), // Duration before action is executed
                    event -> popupStage.close() // Action to close the window
            ));
            timeline.setCycleCount(1); // Run only once
            timeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}