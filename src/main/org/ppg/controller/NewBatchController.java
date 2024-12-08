package org.ppg.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.ppg.model.*;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Controller class for managing the creation of new batches
 */
public class NewBatchController {
    private DatabaseManager databaseManager;
    private ArrayList<String> allowedItems;
    private ObservableList<Batch> batchData;
    private ObservableList<Batch> weeklyBatchData;
    private WeeklyBatchesListController weeklyBatchesListController;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField nBatchText;
    @FXML
    private TextField planningClassText;
    @FXML
    private TextField plantText;
    @FXML
    private TextField quantityText;
    @FXML
    private TextArea descriptionText;
    @FXML
    private ComboBox combo_box;
    @FXML
    private TextField itemText;

    /**
     * Initializes the controller by setting up components and loading initial data
     */
    public void initialize() {
        // TODO
        allowedItems = new ArrayList<String>();
        try {
            databaseManager = DatabaseManager.getInstance();
            allowedItems.addAll(databaseManager.getItems());
        } catch (PPGSchedulerException e) {
            e.printStackTrace();
        }
        datePicker.setShowWeekNumbers(false);
        datePicker.getEditor().setContextMenu(new ContextMenu());
        nBatchText.setContextMenu(new ContextMenu());
        planningClassText.setContextMenu(new ContextMenu());
        plantText.setContextMenu(new ContextMenu());
        quantityText.setContextMenu(new ContextMenu());
        descriptionText.setContextMenu(new ContextMenu());
        
    }

    /**
     * Sets the general and weekly batch lists for management
     *
     * @param batchData general batch list
     * @param weeklyBatchData weekly batch list
     */
    public void setBatchData(ObservableList<Batch> batchData, ObservableList<Batch> weeklyBatchData) {
        this.batchData = batchData;
        this.weeklyBatchData = weeklyBatchData;
    }

    /**
     * Adds a new batch after validating the input fields
     *
     * @throws PPGSchedulerException exception thrown when an error occurs while adding the batch to the database
     */
    @FXML
    private void addBatch() throws PPGSchedulerException {
        if (nBatchText.getText().isEmpty() || planningClassText.getText().isEmpty() || plantText.getText().isEmpty() || quantityText.getText().isEmpty() || descriptionText.getText().isEmpty() || combo_box.getValue() == null || datePicker.getValue() == null) {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorNotFullBatch.fxml")));
        } else if (!allowedItem(itemText.getText())) {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorNotAllowedItem.fxml")));
        } else {
            if (!contains(batchData, Integer.parseInt(nBatchText.getText()))) {
                if (datePicker.getValue().isBefore(LocalDate.now())) {
                    openError(new FXMLLoader(getClass().getResource("/fxml/errorDate.fxml")));
                } else {
                    try {
                        databaseManager = DatabaseManager.getInstance();
                    } catch (PPGSchedulerException e) {
                        e.printStackTrace();
                    }
                    int paginationIndex = weeklyBatchesListController.getPagination().getCurrentPageIndex();
                    String type = combo_box.getValue().toString();
                    Types typeBatch = Types.valueOf(type);
                    Batch batchToAdd = new Batch(Integer.parseInt(nBatchText.getText()), planningClassText.getText(), plantText.getText(), itemText.getText(), Integer.parseInt(quantityText.getText()), descriptionText.getText(), typeBatch, datePicker.getValue());
                    batchToAdd.setStatus(Statuses.EN_ESPERA);
                    batchToAdd.setStartDate(LocalDate.now());
                    batchData.add(batchToAdd);
                    weeklyBatchData.add(batchToAdd);
                    /*
                    try {
                    DESCOMENTAR ESTA LÍNEA PONE EN RIESGO LA INTEGRIDAD DE LA BBDD PUESTO QUE AÑADE EL LOTE SELECCIONADO
                        databaseManager.insertBatchDB(batchToAdd);
                    } catch (PPGSchedulerException e) {
                        e.printStackTrace();
                    }
                    */
                    if (weeklyBatchesListController != null) {
                        weeklyBatchesListController.refreshTable();
                        weeklyBatchesListController.getPagination().setCurrentPageIndex(paginationIndex);
                    }
                }
            } else {
                openError(new FXMLLoader(getClass().getResource("/fxml/errorInsertingDuplicate.fxml")));
            }
        }
    }

    /**
     * Checks if a batch with a specific identifier exists in the list
     *
     * @param batchData batch list
     * @param nBatch identifier of the batch to search
     * @return true if the batch exists, false otherwise
     */
    private boolean contains(ObservableList<Batch> batchData, int nBatch) {
        for (Batch batch : batchData) {
            if (batch.getnBatch() == nBatch) {
                return true;
            }
        }
        return false;
    }

    /**
     * Assings the weekly batch list controller to update the view
     *
     * @param WeeklyBatchesListController weekly batch list controller
     */
    public void setBatchesListController(WeeklyBatchesListController WeeklyBatchesListController) {
        this.weeklyBatchesListController = WeeklyBatchesListController;
    }

    /**
     * Opens a popup window to display specific error messages
     *
     * @param fxmlLoader FXMLLoader object to load the error window
     */
    private void openError(FXMLLoader fxmlLoader) {
        try {
            // Cargar el archivo FXML del popup
            Parent popupRoot = fxmlLoader.load();
            Stage popupStage = new Stage();
            popupStage.resizableProperty().setValue(Boolean.FALSE);
            popupStage.setTitle("ERROR");
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquear la ventana principal
            popupStage.setScene(new Scene(popupRoot));
            popupStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/warn_icon.png")));
            popupStage.setOnShown(event -> {
                // Obtener dimensiones de la ventana principal o pantalla
                double centerX = nBatchText.getScene().getWindow().getX() + nBatchText.getScene().getWindow().getWidth() / 2;
                double centerY = nBatchText.getScene().getWindow().getY() + nBatchText.getScene().getWindow().getHeight() / 2;
                // Calcular posición para centrar el popup
                popupStage.setX(centerX - popupStage.getWidth() / 2);
                popupStage.setY(centerY - popupStage.getHeight() / 2);
            });
            popupStage.show();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), // Duración antes de ejecutar la acción
                    event -> popupStage.close() // Acción para cerrar la ventana
            ));
            timeline.setCycleCount(1); // Ejecutar solo una vez
            timeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifies if an item is allowed to be added to the database
     *
     * @param item item to validate
     * @return true if the item is valid, false otherwise
     */
    private boolean allowedItem(String item) {
        for (String allowedItem : allowedItems) {
            if (allowedItem.equals(item)) {
                return true;
            }
        }
        return false;
    }
}
