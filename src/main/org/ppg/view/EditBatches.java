package org.ppg.view;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.ppg.model.*;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

public class EditBatches {
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
}
