package org.ppg.view;

import javafx.beans.property.StringProperty;
import org.ppg.model.*;

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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Objects;

public class ListaLotes extends Application {
    @FXML
    private AnchorPane rootPane;
    
    @FXML
    private Label titleLabel;
    
    @FXML
    private Button addButton;
    
    @FXML
    private TableView<Lote> tableView;
    
    @FXML
    private TableColumn<Lote, String> pClassCol;
    
    @FXML
    private TableColumn<Lote, String> plantCol;
    
    @FXML
    private TableColumn<Lote, String> itemCol;
    
    @FXML
    private TableColumn<Lote, String> iDateCol;
    
    @FXML
    private TableColumn<Lote, String> nDateCol;
    
    @FXML
    private TableColumn<Lote, String> nLoteCol;
    
    @FXML
    private TableColumn<Lote, String> statusCol;
    
    @FXML
    private TableColumn<Lote, String> quantityCol;
    
    @FXML
    private TableColumn<Button, Void> editCol; // Columna para botones de edición
    
    @FXML
    private Pagination pagination;
    
    @FXML
    private Stage stageActual;
    
    private final ObservableList<Lote> loteData = FXCollections.observableArrayList();
    
    private final int ROWS_PER_PAGE = 11; // Número de elementos por página
    
    public void initialize() {
        setupData();
        // Configuración de las columnas
        nLoteCol.setCellValueFactory(data -> data.getValue().getProperties()[0]);
        pClassCol.setCellValueFactory(data -> data.getValue().getProperties()[1]);
        plantCol.setCellValueFactory(data -> data.getValue().getProperties()[2]);
        itemCol.setCellValueFactory(data -> data.getValue().getProperties()[3]);
        quantityCol.setCellValueFactory(data -> data.getValue().getProperties()[4]);
        iDateCol.setCellValueFactory(data -> data.getValue().getProperties()[5]);
        nDateCol.setCellValueFactory(data -> data.getValue().getProperties()[6]);
        statusCol.setCellValueFactory(data -> data.getValue().getProperties()[7]);
        addTooltipToCells(nLoteCol, 8);
        
        // Configuración del botón en buttonNameColumn
        editCol.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("EDITAR");
            {
                // Define la acción del botón
                btn.setOnAction(event -> {
                
                });
                btn.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 0; -fx-alignment: " + "center; -fx-max-width: 130px; -fx-pref-height: 61px; -fx-padding: 0;" + "-fx-border-color: transparent; ");
                
                btn.setOnMouseEntered(event -> btn.setStyle("-fx-background-color: #FFD700; -fx-background-radius: 0; -fx-alignment: " + "center; -fx-max-width: 130px; -fx-pref-height: 61px; -fx-padding: 0;" + "-fx-border-color: transparent; -fx-transition: all 0.3s ease;"));
                
                btn.setOnMouseExited(event -> btn.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 0; -fx-alignment: " + "center; -fx-max-width: 130px; -fx-pref-height: 61px; -fx-padding: 0;" + "-fx-border-color: transparent; "));
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
        
        // Configuración de la paginación
        int totalPage = (int) (Math.ceil(loteData.size() * 1.0 / ROWS_PER_PAGE));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> changeTableView(newValue.intValue(), ROWS_PER_PAGE));
    }
    
    public void setupData() {
        loteData.add(new Lote(20000000, "A", "A", "A", 5, new Date(1, 2, 2000), new Date(1, 2, 2000), Estados.EN_ESPERA, "A", Types.PIMM));
        loteData.add(new Lote(2, "VD-WBBC", "VDW", "A-RXX3359-DD", 10, new Date(15, 2, 2023), new Date(15, 2, 2023), Estados.FINALIZADO, "a", Types.PIMM));
        for (int i = 3; i < 500; i++) {
            loteData.add(new Lote(i + 27000000, "VD-WBBC" + i, "VDW", "A-RXX3359-DD", 10, new Date(15, 2, 2023), new Date(15, 2, 2023), Estados.FINALIZADO, "Esto es la descripción. ", Types.PISC));
        }
    }
    
    private void changeTableView(int index, int limit) {
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, loteData.size());
        
        int minIndex = Math.min(toIndex, loteData.size());
        SortedList<Lote> sortedData = new SortedList<>(FXCollections.observableArrayList(loteData.subList(Math.min(fromIndex, minIndex), minIndex)));
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
    }
    
    @FXML
    private void abrirNuevoLote() {
        try {
            // Cargar el archivo FXML del popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/nuevoLote.fxml"));
            Parent popupRoot = fxmlLoader.load();
            
            // Crear una nueva ventana para el popup
            Stage popupStage = new Stage();
            popupStage.resizableProperty().setValue(Boolean.FALSE);
            popupStage.setTitle("Insertar nuevo lote");
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
    
    private void addTooltipToCells(TableColumn<Lote, String> column, int propertyIndex) {
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Lote, String> call(TableColumn<Lote, String> param) {
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
                        Lote lote = getTableView().getItems().get(getIndex());
                        StringProperty tooltipText = lote.getProperties()[propertyIndex];
                        Tooltip tooltip = new Tooltip(tooltipText.getValue());
                        setTooltip(tooltip);
                    }
                    }
                };
            }
        });
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/listaLotes.fxml")));
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("PPG - Lista de Lotes");
        primaryStage.setScene(scene);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
