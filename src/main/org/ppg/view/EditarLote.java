package org.ppg.view;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.ppg.model.*;


public class EditarLote {
    @FXML
    private TextField nLoteField;
    @FXML
    private TextField pClassField;
    @FXML
    private TextField plantField;
    @FXML
    private TextField itemField;
    @FXML
    private TextField quantityField;
    @FXML
    private DatePicker iDatePicker;
    @FXML
    private TextField nDateField;
    @FXML
    private TextArea descriptionField;

    public void setLote(Batch lote) {
        nLoteField.setText(String.valueOf(lote.nBatch()));
        pClassField.setText(lote.planningClass());
        plantField.setText(lote.plant());
        itemField.setText(lote.item());
        quantityField.setText(String.valueOf(lote.quantity()));
        iDatePicker.setValue(lote.dStart());  // Asegúrate de convertir a cadena si es necesario
        nDateField.setText(lote.dNeed().toString());
        descriptionField.setText(lote.description());
    }
}
