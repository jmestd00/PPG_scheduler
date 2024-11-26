package org.ppg.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Polygon;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import org.ppg.model.*;

public class EditBatchesController {
    private ObservableList<Batch> batchData;
    private BatchesListController batchesListController;
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
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setBatch(Batch batch) {
        nBatchField.setText(String.valueOf(batch.nBatch()));
        pClassField.setText(batch.planningClass());
        plantField.setText(batch.plant());
        itemField.setText(batch.item());
        quantityField.setText(String.valueOf(batch.quantity()));
        startDatePicker.setValue(batch.startDate());  // Asegúrate de convertir a cadena si es necesario
        needDateField.setText(batch.needDate().format(formatter));
        descriptionField.setText(batch.description());
        this.batch = batch;
    }

    public void initialize() {
        // Inicializa los campos de texto
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

    public void setBatchData(ObservableList<Batch> batchData) {
        this.batchData = batchData;
    }

    @FXML
    private void removeBatch() {
        batchData.remove(batchData.indexOf(batch));
        batchesListController.refreshTable();
        this.stage.hide();
    }

    public void setBatchesListController(BatchesListController batchesListController) {
        this.batchesListController = batchesListController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void modifyBatch() {
        if (startDatePicker.getValue().isBefore(LocalDate.parse(needDateField.getText(), formatter))) {
            if (startDatePicker.getValue().isBefore(LocalDate.now())) {
                openError(new FXMLLoader(getClass().getResource("/fxml/errorDate.fxml")));
            }
            int index = batchData.indexOf(batch);
            Batch modifiedBatch = new Batch(batch.nBatch(), pClassField.getText(), plantField.getText(), itemField.getText(),
                    Integer.parseInt(quantityField.getText()), startDatePicker.getValue(), LocalDate.parse(needDateField.getText(), formatter),
                    batch.status(), descriptionField.getText(), batch.type(), batch.dilutor());
            batchData.remove(index);
            batchData.add(index, modifiedBatch);
            this.stage.hide();
            batchesListController.refreshTable();
        } else {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorModifyPopup.fxml")));
        }
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
                double centerX = descriptionField.getScene().getWindow().getX() + descriptionField.getScene().getWindow().getWidth() / 2;
                double centerY = descriptionField.getScene().getWindow().getY() + descriptionField.getScene().getWindow().getHeight() / 2;
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