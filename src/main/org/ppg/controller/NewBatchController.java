package org.ppg.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DatePicker;

import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class NewBatchController {
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

}
