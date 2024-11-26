package org.ppg.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
        try {
            databaseManager = DatabaseManager.getInstance();
        } catch (PPGSchedulerException e) {
            e.printStackTrace();
        }
        int paginationIndex = batchesListController.getPagination().getCurrentPageIndex();
        String type = combo_box.getValue().toString();
        Types typeBatch = Types.valueOf(type);
        Batch batchToAdd = new Batch(Integer.parseInt(nBatchText.getText()),planningClassText.getText(),plantText.getText(),"TO BE DETERMINED", Integer.parseInt(quantityText.getText()),
                LocalDate.of(0,1,1), datePicker.getValue(),Statuses.EN_ESPERA,  descriptionText.getText(), typeBatch, new Dilutor(0,"X",0));
        batchData.add(batchToAdd);

        if (batchesListController != null) {
            batchesListController.refreshTable();
            batchesListController.getPagination().setCurrentPageIndex(paginationIndex);
        }

    }

    public void setBatchesListController(BatchesListController batchesListController) {
        this.batchesListController = batchesListController;
    }
}
