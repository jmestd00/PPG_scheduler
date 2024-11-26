package org.ppg.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.ppg.model.*;

import java.lang.reflect.Type;
import java.time.LocalDate;

public class NewBatchController {
   // private static final DatabaseManager databaseManager;
    private ObservableList<Batch> batchData;
    private BatchesListController batchesListController;
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

    private DatabaseManager databaseManager;

    public void initialize() {
        // TODO
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
                    int paginationIndex = batchesListController.getPagination().getCurrentPageIndex();
                    String type = combo_box.getValue().toString();
                    Types typeBatch = Types.valueOf(type);
                    Batch batchToAdd = new Batch(Integer.parseInt(nBatchText.getText()), planningClassText.getText(), plantText.getText(), getItem(typeBatch) , Integer.parseInt(quantityText.getText()),
                            LocalDate.of(0, 1, 1), datePicker.getValue(), Statuses.EN_ESPERA, descriptionText.getText(), typeBatch, new Dilutor(0, "X", 0));
                    batchData.add(batchToAdd);

                    if (batchesListController != null) {
                        batchesListController.refreshTable();
                        batchesListController.getPagination().setCurrentPageIndex(paginationIndex);
                    }
                }
            } else {
                openError(new FXMLLoader(getClass().getResource("/fxml/errorInsertingDuplicate.fxml")));
            }
        }
    }

    private String getItem(Types type) {
        StringBuilder sb = new StringBuilder();
        sb.append(planningClassText.getText());
        sb.append("-");
        sb.append(plantText.getText());
        sb.append("-");
        sb.append(type.getValue());
        return sb.toString();
    }


    private boolean contains(ObservableList<Batch> batchData, int nBatch, int quantity) {
        for (Batch batch : batchData) {
            if (batch.nBatch() == nBatch && batch.quantity() == quantity) {
                return true;
            }
        }
        return false;
    }

    public void setBatchesListController(BatchesListController batchesListController) {
        this.batchesListController = batchesListController;
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
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
