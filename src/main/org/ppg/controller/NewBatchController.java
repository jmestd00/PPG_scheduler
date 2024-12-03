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

public class NewBatchController {
    private DatabaseManager databaseManager;
    private ObservableList<Batch> batchData;
    private ArrayList<String> allowedItems;
    private WeeklyBatchesListController WeeklyBatchesListController;
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
    
    public void initialize() {
        // TODO
        try {
            databaseManager = DatabaseManager.getInstance();
        } catch (PPGSchedulerException e) {
            e.printStackTrace();
        }
        //allowedItems.addAll(databaseManager.getAllowedItems());
        datePicker.setShowWeekNumbers(false);
        datePicker.getEditor().setContextMenu(new ContextMenu());
        nBatchText.setContextMenu(new ContextMenu());
        planningClassText.setContextMenu(new ContextMenu());
        plantText.setContextMenu(new ContextMenu());
        quantityText.setContextMenu(new ContextMenu());
        descriptionText.setContextMenu(new ContextMenu());
        
    }
    
    public void setBatchData(ObservableList<Batch> batchData) {
        this.batchData = batchData;
    }
    
    @FXML
    private void addBatch() throws PPGSchedulerException {
        if (nBatchText.getText().isEmpty() || planningClassText.getText().isEmpty() || plantText.getText().isEmpty() || quantityText.getText().isEmpty() || descriptionText.getText().isEmpty() || combo_box.getValue() == null || datePicker.getValue() == null) {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorNotFullBatch.fxml")));
        } else if (!allowedItem(itemText.getText())) {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorNotAllowedItem.fxml")));
        } else {
            if (!contains(batchData, Integer.parseInt(nBatchText.getText()), Integer.parseInt(quantityText.getText()))) {
                if (datePicker.getValue().isBefore(LocalDate.now())) {
                    openError(new FXMLLoader(getClass().getResource("/fxml/errorDate.fxml")));
                } else {
                    try {
                        databaseManager = DatabaseManager.getInstance();
                    } catch (PPGSchedulerException e) {
                        e.printStackTrace();
                    }
                    int paginationIndex = WeeklyBatchesListController.getPagination().getCurrentPageIndex();
                    String type = combo_box.getValue().toString();
                    Types typeBatch = Types.valueOf(type);
                    Batch batchToAdd = new Batch(Integer.parseInt(nBatchText.getText()), planningClassText.getText(), plantText.getText(), itemText.getText(), Integer.parseInt(quantityText.getText()), descriptionText.getText(), typeBatch, datePicker.getValue());
                    batchToAdd.setStatus(Statuses.FINALIZADO);
                    batchData.add(batchToAdd);
                    
                    if (WeeklyBatchesListController != null) {
                        WeeklyBatchesListController.refreshTable();
                        WeeklyBatchesListController.getPagination().setCurrentPageIndex(paginationIndex);
                    }
                }
            } else {
                openError(new FXMLLoader(getClass().getResource("/fxml/errorInsertingDuplicate.fxml")));
            }
        }
    }
    
    private boolean contains(ObservableList<Batch> batchData, int nBatch, int quantity) {
        for (Batch batch : batchData) {
            if (batch.getnBatch() == nBatch && batch.getQuantity() == quantity) {
                return true;
            }
        }
        return false;
    }
    
    public void setBatchesListController(WeeklyBatchesListController WeeklyBatchesListController) {
        this.WeeklyBatchesListController = WeeklyBatchesListController;
    }
    
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
    
    private boolean allowedItem(String item) {
        for (String allowedItem : allowedItems) {
            if (allowedItem.equals(item)) {
                return true;
            }
        }
        return false;
    }
}
