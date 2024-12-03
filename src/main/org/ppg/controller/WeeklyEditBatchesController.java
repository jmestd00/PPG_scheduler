package org.ppg.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.ppg.model.*;

import javax.xml.crypto.Data;


public class WeeklyEditBatchesController {
    private ObservableList<Batch> batchData;
    private WeeklyBatchesListController WeeklyBatchesListController;
    private Stage stage;
    private Batch batch;
    private DatabaseManager databaseManager;

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

    /**
     * Method that stablish the batch to be modified stablishing the fields of the batch
     * @param batch
     */
    public void setBatch(Batch batch) {
        nBatchField.setText(String.valueOf(batch.getnBatch()));
        pClassField.setText(batch.getPlannigClass());
        plantField.setText(batch.getPlant());
        itemField.setText(batch.getItem());
        quantityField.setText(String.valueOf(batch.getQuantity()));
        startDatePicker.setValue(batch.getStartDate());  // Asegúrate de convertir a cadena si es necesario
        needDateField.setText(batch.getNeedDate().format(formatter));
        descriptionField.setText(batch.getDescription());
        this.batch = batch;
    }

    /**
     * Method that initialize this view.
     * It removes the context menu from the fields and set the date picker to not show the week numbers
     */
    public void initialize() {
        // Inicializa los campos de texto
        try{
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

    public void setBatchData(ObservableList<Batch> batchData) {
        this.batchData = batchData;
    }

    @FXML
    private void removeBatch() {
        this.stage.close();
        batchData.remove(batchData.indexOf(batch));
        // CAUTION: uncomment this line to removeBatches from the bbdd
        //databaseManager.removeBatch(batch);
        WeeklyBatchesListController.refreshTable();
    }

    public void setBatchesListController(WeeklyBatchesListController WeeklyBatchesListController) {
        this.WeeklyBatchesListController = WeeklyBatchesListController;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


    @FXML
    private void modifyBatch() {
        if (startDatePicker.getValue().isBefore(LocalDate.parse(needDateField.getText(), formatter))) {
            if (startDatePicker.getValue().isBefore(LocalDate.now())) {
                openError(new FXMLLoader(getClass().getResource("/fxml/errorDate.fxml")));
            } else {
                try{
                    databaseManager.updateBatchDB(batch);
                    } catch (PPGSchedulerException e) {
                        e.printStackTrace();
                    }
            this.stage.hide();
            WeeklyBatchesListController.refreshTable();
            }
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
            popupStage.show();
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(3), // Duración antes de ejecutar la acción
                    event -> popupStage.close() // Acción para cerrar la ventana
            ));
            timeline.setCycleCount(1); // Ejecutar solo una vez
            timeline.play(); // Iniciar el Timeline
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}