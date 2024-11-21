package org.ppg.view;

import org.ppg.model.*;

import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.util.Objects;

public class BatchesList extends Application {
    private final ObservableList<Batch> batchData = FXCollections.observableArrayList();
    private static DatabaseManager databaseManager;
    private final int ROWS_PER_PAGE = 11;
    
    Image logoPPG = new Image(String.valueOf(getClass().getResource("/images/PPG_Logo512_512.png")));
    @FXML
    private Button addButton;
    @FXML
    private TableView<Batch> tableView;
    @FXML
    private TableColumn<Batch, String> pClassCol;
    @FXML
    private TableColumn<Batch, String> plantCol;
    @FXML
    private TableColumn<Batch, String> itemCol;
    @FXML
    private TableColumn<Batch, String> startDateCol;
    @FXML
    private TableColumn<Batch, String> needDateCol;
    @FXML
    private TableColumn<Batch, String> nBatchCol;
    @FXML
    private TableColumn<Batch, String> statusCol;
    @FXML
    private TableColumn<Batch, String> quantityCol;
    @FXML
    private TableColumn<Batch, Void> editCol;
    @FXML
    private Pagination pagination;
    
    //Main
    public static void main(String[] args) throws PPGSchedulerException {
        databaseManager = DatabaseManager.getInstance();
        launch(args);
    }
    
    //JavaFX init
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/batchesList.fxml")));
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("PPG - Lista de Lotes");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(logoPPG);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();
    }
    public void initialize() {
        setupData();
        tableView.setSelectionModel(null);
        // Configuración de las columnas
        nBatchCol.setCellValueFactory(data -> data.getValue().getProperties()[0]);
        nBatchCol.setReorderable(false);
        pClassCol.setCellValueFactory(data -> data.getValue().getProperties()[1]);
        pClassCol.setReorderable(false);
        plantCol.setCellValueFactory(data -> data.getValue().getProperties()[2]);
        plantCol.setReorderable(false);
        itemCol.setCellValueFactory(data -> data.getValue().getProperties()[3]);
        itemCol.setReorderable(false);
        quantityCol.setCellValueFactory(data -> data.getValue().getProperties()[4]);
        quantityCol.setReorderable(false);
        startDateCol.setCellValueFactory(data -> data.getValue().getProperties()[5]);
        startDateCol.setReorderable(false);
        needDateCol.setCellValueFactory(data -> data.getValue().getProperties()[6]);
        needDateCol.setReorderable(false);
        addTooltipToCells(nBatchCol, 8);
        // Configuración del botón en buttonNameColumn
        statusCol.setCellFactory(param -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    Batch selectedLote = getTableView().getItems().get(getIndex());
                    setText(selectedLote.status().getValue());
                    setStyle("-fx-background-color: " + selectedLote.status().getHexColor() +
                            "; -fx-text-fill: white; -fx-alignment: center; -fx-font-weight: bold; -fx-font-size: 24px");
                }
            }
        });
        statusCol.setReorderable(false);
        
        editCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("");
            
            {
                // Define la acción del botón
                btn.setOnAction(event -> {
                    Batch selectedLote = getTableView().getItems().get(getIndex());
                    openEditBatch(selectedLote);
                });
                btn.setStyle("/*-fx-background-color: #ffffff;*/ -fx-background-radius: 0; -fx-alignment: " +
                        "center; -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" +
                        "    -fx-background-image: url('/images/Engine128_128.png');" +
                        "    -fx-background-size: 60px 60px;" +
                        " -fx-background-color: transparent;" +
                        " -fx-background-position: center;" +
                        " -fx-border-color: black; ");
                
                btn.setOnMouseEntered(event -> btn.setStyle("-fx-background-radius: 0; -fx-alignment: " +
                        "center; -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" +
                        "    -fx-background-image: url('/images/Engine128_128.png');" +
                        "    -fx-background-size: 60px 60px;" +
                        " -fx-background-color: transparent;" +
                        " -fx-background-position: center;" +
                        " -fx-border-color: black; -fx-scale-x: 1.05;" +
                        "    -fx-scale-y: 1.05;"));
                
                btn.setOnMouseExited(event -> btn.setStyle("-fx-background-radius: 0; -fx-alignment: " +
                        "center; -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" +
                        "    -fx-background-image: url('/images/Engine128_128.png');" +
                        "    -fx-background-size: 60px 60px;" +
                        " -fx-background-color: transparent;" +
                        " -fx-background-position: center;" +
                        " -fx-border-color: black; "));
                btn.setPrefHeight(Double.MAX_VALUE);
                btn.setPrefHeight(Double.MAX_VALUE);
            }
            
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    //btn.setGraphic(new FontAwesomeIconView(FontAwesomeIcon.BELL));
                    setGraphic(btn);
                    setAlignment(Pos.CENTER);
                }
            }
        });
        editCol.setReorderable(false);
        
        // Configuración de la paginación
        int totalPage = (int) (Math.ceil(batchData.size() * 1.0 / ROWS_PER_PAGE));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pagination.currentPageIndexProperty().addListener(
                (observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));
    }
    public void setupData() {
        batchData.addAll(databaseManager.getBatchesListDB());
    }
    
    //Ventana principal
    private void changeTableView(int index, int limit) {
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, batchData.size());
        
        int minIndex = Math.min(toIndex, batchData.size());
        SortedList<Batch> sortedData = new SortedList<>(FXCollections.observableArrayList(batchData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        
        tableView.setItems(sortedData);
    }
    private void addTooltipToCells(TableColumn<Batch, String> column, int propertyIndex) {
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Batch, String> call(TableColumn<Batch, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        
                        if (empty || item == null) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            setText(item);
                            
                            // Crear y configurar el Tooltip con el valor de la celda
                            Batch batch = getTableView().getItems().get(getIndex());
                            StringProperty tooltipText = batch.getProperties()[propertyIndex];
                            Tooltip tooltip = new Tooltip(tooltipText.getValue());
                            setTooltip(tooltip);
                        }
                    }
                };
            }
        });
    }
    
    //Ventanas auxiliares
    @FXML
    private void openNewBatch() {
        try {
            // Cargar el archivo FXML del popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/newBatch.fxml"));
            Parent popupRoot = fxmlLoader.load();
            // Crear una nueva ventana para el popup
            Stage popupStage = new Stage();
            popupStage.resizableProperty().setValue(Boolean.FALSE);
            popupStage.setTitle("Insertar nuevo lote");
            popupStage.getIcons().add(logoPPG);
            
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquear la ventana principal
            popupStage.setScene(new Scene(popupRoot));
            popupStage.setOnShown(event -> {
                // Obtener dimensiones de la ventana principal o pantalla
                double centerX = addButton.getScene().getWindow().getX() + addButton.getScene().getWindow().getWidth() / 2;
                double centerY = addButton.getScene().getWindow().getY() + addButton.getScene().getWindow().getHeight() / 2;
                
                // Calcular posición para centrar el popup
                popupStage.setX(centerX - popupStage.getWidth() / 2);
                popupStage.setY(centerY - popupStage.getHeight() / 2);
            });
            // Mostrar el popup
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openEditBatch(Batch sampleBatch) {
        try {
            // Cargar el archivo FXML del popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/editBatches.fxml"));
            Parent popupRoot = fxmlLoader.load();
            EditBatches editBatches = fxmlLoader.getController();
            // Pasar el objeto batch al controlador de la ventana de edición
            editBatches.setBatch(sampleBatch);
            // Crear una nueva ventana para el popup
            Stage popupStage = new Stage();
            popupStage.resizableProperty().setValue(Boolean.FALSE);
            popupStage.setTitle("Editar lote");
            popupStage.getIcons().add(logoPPG);
            
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquear la ventana principal
            popupStage.setScene(new Scene(popupRoot));
            popupStage.setOnShown(event -> {
                // Obtener dimensiones de la ventana principal o pantalla
                double centerX = addButton.getScene().getWindow().getX() + addButton.getScene().getWindow().getWidth() / 2;
                double centerY = addButton.getScene().getWindow().getY() + addButton.getScene().getWindow().getHeight() / 2;
                
                // Calcular posición para centrar el popup
                popupStage.setX(centerX - popupStage.getWidth() / 2);
                popupStage.setY(centerY - popupStage.getHeight() / 2);
            });
            // Mostrar el popup
            popupStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
