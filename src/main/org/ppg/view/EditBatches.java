package org.ppg.view;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.ppg.model.*;

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

    public void setBatch(Batch batch) {
        nBatchField.setText(String.valueOf(batch.nBatch()));
        pClassField.setText(batch.planningClass());
        plantField.setText(batch.plant());
        itemField.setText(batch.item());
        quantityField.setText(String.valueOf(batch.quantity()));
        startDatePicker.setValue(batch.startDate());  // Asegúrate de convertir a cadena si es necesario
        needDateField.setText(batch.needDate().toString());
        descriptionField.setText(batch.description());
    }
}
