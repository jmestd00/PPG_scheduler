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
import java.time.temporal.WeekFields;
import java.util.Locale;

/**
 * Controller class for managing the editing of batches within the weekly view. This controller provides functionality for modifying and removing batches, as well as validating dates and handling errors in batch data
 */
public class WeeklyEditBatchesController {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private ObservableList<Batch> batchData;
    private ObservableList<Batch> weeklyBatchData;
    private WeeklyBatchesListController weeklyBatchesListController;
    private Stage stage;
    private Batch batch;
    private DatabaseManager databaseManager;
    private int weekNumber;
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

    /**
     * Sets the batch to be edited and updates the fields with its data
     *
     * @param batch the batch to be edited
     */
    public void setBatch(Batch batch) {
        nBatchField.setText(String.valueOf(batch.getnBatch()));
        pClassField.setText(batch.getPlanningClass());
        plantField.setText(batch.getPlant());
        itemField.setText(batch.getItem());
        quantityField.setText(String.valueOf(batch.getQuantity()));
        startDatePicker.setValue(batch.getStartDate());  // Asegúrate de convertir a cadena si es necesario
        needDateField.setText(batch.getNeedDate().format(formatter));
        descriptionField.setText(batch.getDescription());
        this.batch = batch;
    }
    
    /**
     * Initializes the view by selecting the current week number and removing context menus
     * from input fields to prevent unwanted interactions. It also configures the DatePicker
     * to not display week numbers
     */
    public void initialize() {
        // Inicializa los campos de texto
        LocalDate date = LocalDate.now();
        weekNumber = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
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
     * Sets the WeeklyBatchesListController to interact with the list of batches
     * @param WeeklyBatchesListController the controller to manage the batches list
     */
    public void setBatchesListController(WeeklyBatchesListController WeeklyBatchesListController) {
        this.weeklyBatchesListController = WeeklyBatchesListController;
    }

    /**
     * Sets the stage for the current window
     * @param stage the current window stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Sets the batch data and weekly batch data
     *
     * @param batchData the complete list of batches
     * @param weeklyBatchData the list of batches for the current week
     */
    public void setBatchData(ObservableList<Batch> batchData, ObservableList<Batch> weeklyBatchData) {
        this.batchData = batchData;
        this.weeklyBatchData = weeklyBatchData;
    }

    /**
     * Updates the batch with the current value in the DatePicker
     *
     * @param batch the batch to be updated
     */
    private void updateBatch(Batch batch) {
        batch.setStartDate(startDatePicker.getValue());
    }

    /**
     * Removes the current batch from the weekly batch data list and the batch data list, and closes the current editing window
     */
    @FXML
    private void removeBatch() {
        this.stage.close();
        weeklyBatchData.remove(weeklyBatchData.indexOf(batch));
        if (weeklyBatchesListController.contains(batch, batchData)) {
            int index = weeklyBatchesListController.getIndex(batch, batchData);
            batchData.remove(index);
        }
        weeklyBatchesListController.refreshTable();
            /*
        try {
            DESCOMENTAR ESTA LÍNEA PONE EN RIESGO LA INTEGRIDAD DE LA BBDD PUESTO QUE ELIMINA EL LOTE SELECCIONADO
            databaseManager.deleteBatch(batch.getnBatch());
        } catch (PPGSchedulerException e) {
            e.printStackTrace();
        }
             */
    }

    /**
     * Modifies the batch with the updated information and validates the dates. If the start date is
     * before the need date and is not in the past, the batch is updated. If invalid, an error popup is shown
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
            DESCOMENTAR ESTA LÍNEA PONE EN RIESGO LA INTEGRIDAD DE LA BBDD PUESTO QUE MODIFICA EL LOTE SELECCIONADO
                    databaseManager.updateBatchDB(batch);
                } catch (PPGSchedulerException e) {
                    e.printStackTrace();
                }
                */
                this.stage.hide();
                if (weeklyBatchesListController.contains(batch, batchData)) {
                    int index = weeklyBatchesListController.getIndex(batch, batchData);
                    batchData.set(index, batch);
                }
                if (batch.getStartDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) != weekNumber) {
                    weeklyBatchData.remove(weeklyBatchData.indexOf(batch));
                }
                weeklyBatchesListController.refreshTable();
            }
        } else {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorModifyPopup.fxml")));
        }
    }

    /**
     * Opens an error popup with a specified FXML layout.
     * The error popup will close automatically after 3 seconds
     *
     * @param fxmlLoader the FXMLLoader instance to load the error popup layout
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
                double centerX = descriptionField.getScene().getWindow().getX() + descriptionField.getScene().getWindow().getWidth() / 2;
                double centerY = descriptionField.getScene().getWindow().getY() + descriptionField.getScene().getWindow().getHeight() / 2;
                // Calcular posición para centrar el popup
                popupStage.setX(centerX - popupStage.getWidth() / 2);
                popupStage.setY(centerY - popupStage.getHeight() / 2);
            });
            popupStage.show();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), // Duración antes de ejecutar la acción
                    event -> popupStage.close() // Acción para cerrar la ventana
            ));
            timeline.setCycleCount(1); // Ejecutar solo una vez
            timeline.play(); // Iniciar el Timeline
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}