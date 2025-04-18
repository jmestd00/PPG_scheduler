package org.ppg.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.ppg.model.*;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller class for the weekly batches list, managing its display and related actions
 */
public class WeeklyBatchesListController {
    private static DatabaseManager databaseManager;
    private final int ROWS_PER_PAGE = 11;
    Image logoPPG = new Image(String.valueOf(getClass().getResource("/images/PPG_Logo512_512.png")));
    private ObservableList<Batch> batchData = FXCollections.observableArrayList();
    private ObservableList<Batch> weeklyBatchData = FXCollections.observableArrayList();
    ArrayList<Batch> listToUpdate = new ArrayList<>();
    ArrayList<Batch> batchesToPlan = new ArrayList<>();
    private BooleanProperty operationCompleted;
    private int weekNumber;
    
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
    private TableColumn<Batch, String> quantityCol;
    @FXML
    private TableColumn<Batch, Void> colorCol;
    @FXML
    private TableColumn<Batch, String> statusCol;
    @FXML
    private TableColumn<Batch, Void> editCol;
    @FXML
    private Pagination pagination;
    @FXML
    private Label titleLabel;

    /**
     * Initializes the controller by setting up the columns, loading the data and setting actions related to the table and pagination
     *
     * @throws PPGSchedulerException exception thrown when there is an error while accessing the database
     */
    public void initialize() throws PPGSchedulerException {
        LocalDate date = LocalDate.now();
        operationCompleted = new SimpleBooleanProperty(false);
        databaseManager = DatabaseManager.getInstance();
        weeklyBatchData.addAll(databaseManager.getBatchesWeekly());
        weekNumber = date.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        String text = titleLabel.getText();
        titleLabel.setText("LISTADO DE LOTES SEMANAL #" + weekNumber);
        try {
            databaseManager = DatabaseManager.getInstance();
            getFullData();
        } catch (PPGSchedulerException e) {
            e.printStackTrace();
        }
        new Thread(() -> {
            startBatches(weeklyBatchData);
            startBatches(batchData);
            try {
                Thread.sleep(10800000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        addButton.setOnAction(event -> openNewBatch(batchData));
        tableView.setSelectionModel(null);
        // Configuración de las columnas
        nBatchCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[0];
            }
        });
        nBatchCol.setReorderable(false);
        pClassCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[1];
            }
        });
        pClassCol.setReorderable(false);
        plantCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[2];
            }
        });
        plantCol.setReorderable(false);
        itemCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[3];
            }
        });
        itemCol.setReorderable(false);
        quantityCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[4];
            }
        });
        quantityCol.setReorderable(false);
        startDateCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[5];
            }
        });
        startDateCol.setReorderable(false);
        needDateCol.setCellValueFactory(data -> {
            if (data.getValue() == null) {
                return new SimpleStringProperty("");
            } else {
                return data.getValue().getProperties()[6];
            }
        });
        needDateCol.setReorderable(false);
        addTooltipToCells(nBatchCol, 8);
        colorCol.setReorderable(false);
        colorCol.setCellFactory(param -> new TableCell<Batch, Void>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                // Si la fila está vacía o la celda está vacía (null), no se renderiza nada
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);  // No mostrar gráfico
                    setText(null);      // No mostrar texto
                } else {
                    // Si la fila tiene un objeto Batch válido, renderizamos el gráfico
                    Batch selectedBatch = getTableView().getItems().get(getIndex());
                    
                    // Solo mostrar el gráfico si el Batch tiene un color válido en su estado
                    if (selectedBatch != null && selectedBatch.getStatus() != null) {
                        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, new Stop(0, Color.web(selectedBatch.getStatus().getHexColorPrimary())), new Stop(1, Color.web(selectedBatch.getStatus().getHexColorSecondary())));
                        
                        Circle circle = new Circle(15);  // Radio del círculo
                        circle.setFill(gradient);        // Color de relleno
                        circle.setStroke(Color.BLACK);   // Color del borde
                        
                        // Establecer el gráfico en la celda
                        setGraphic(circle);
                        setText(null);  // Asegurarse de no mostrar texto en la celda
                    } else {
                        setGraphic(null);  // Si no tiene un estado válido, no se muestra gráfico
                        setText(null);      // No mostrar texto
                    }
                }
            }
        });
        
        // Configuración del botón en buttonNameColumn
        statusCol.setCellFactory(param -> new TableCell<>() {
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null) {
                    setText(null);
                } else {
                    Batch selectedBatch = getTableView().getItems().get(getIndex());
                    if (selectedBatch == null) {
                        setText(null);
                    } else {
                        setText(selectedBatch.getStatus().getValue());
                    }
                    setStyle("-fx-alignment: center_left;-fx-font-weight: normal; -fx-font-size: 24px; -fx-font-family: 'Futura medium';");
                }
            }
        });
        statusCol.setReorderable(false);
        
        editCol.setCellFactory(param -> new TableCell<Batch, Void>() {
            private final Button btn = new Button("");
            
            {
                // Define la acción del botón
                btn.setOnAction(event -> {
                    Batch selectedBatch = getTableView().getItems().get(getIndex());
                    if (selectedBatch != null) {
                        openEditBatch(selectedBatch);
                    }
                });
                btn.setStyle("/*-fx-background-color: #ffffff;*/ -fx-background-radius: 0; -fx-alignment: center;" + " -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" + " -fx-background-image: url('/images/Engine128_128.png');" + " -fx-background-size: 60px 60px;" + " -fx-background-color: transparent;" + " -fx-background-position: center;" + " -fx-border-color: black;");
                
                // Estilos para el botón al pasar el ratón
                btn.setOnMouseEntered(event -> btn.setStyle("-fx-background-radius: 0; -fx-alignment: center;" + " -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" + " -fx-background-image: url('/images/Engine128_128.png');" + " -fx-background-size: 60px 60px;" + " -fx-background-color: transparent;" + " -fx-background-position: center;" + " -fx-border-color: black; -fx-scale-x: 1.05;" + " -fx-scale-y: 1.05;"));
                
                btn.setOnMouseExited(event -> btn.setStyle("-fx-background-radius: 0; -fx-alignment: center;" + " -fx-max-width: 130px; -fx-pref-height: 64px; -fx-padding: 0;" + " -fx-background-image: url('/images/Engine128_128.png');" + " -fx-background-size: 60px 60px;" + " -fx-background-color: transparent;" + " -fx-background-position: center;" + " -fx-border-color: black; "));
                
                btn.setPrefHeight(Double.MAX_VALUE);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                
                // Verificar si la fila está vacía o si el Batch es nulo
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);  // No mostrar el botón si está vacío
                } else {
                    Batch selectedBatch = getTableView().getItems().get(getIndex());
                    if (selectedBatch != null) {
                        setGraphic(btn);  // Mostrar el botón si el Batch es válido
                        setAlignment(Pos.CENTER);  // Alinear el botón
                    } else {
                        setGraphic(null);  // No mostrar el botón si el Batch es nulo
                    }
                }
            }
        });
        
        editCol.setReorderable(false);
        
        // Configuración de la paginación
        int totalPage = (int) (Math.ceil(weeklyBatchData.size() * 1.0 / ROWS_PER_PAGE));
        pagination.setPageCount(totalPage);
        pagination.setCurrentPageIndex(0);
        changeTableView(0, ROWS_PER_PAGE);
        pagination.currentPageIndexProperty().addListener((observable, oldValue, newValue) -> {
            changeTableView(newValue.intValue(), ROWS_PER_PAGE);
        });
    }

    /**
     * Updates the TableView with batches for the given page index and row limit
     *
     * @param index page index
     * @param limit number of rows to display per page
     */
    private void changeTableView(int index, int limit) {
        int fromIndex = index * limit;
        int toIndex = Math.min(fromIndex + limit, weeklyBatchData.size());
        int minIndex = Math.min(toIndex, weeklyBatchData.size());
        
        // Crea una lista de lotes para la página actual
        ObservableList<Batch> pageData = FXCollections.observableArrayList(weeklyBatchData.subList(fromIndex, minIndex));
        
        // Si hay espacio restante en la página, agrega filas vacías
        int remainingRows = limit - pageData.size();
        for (int i = 0; i < remainingRows; i++) {
            pageData.add(null);  // Añade una fila vacía representada por "null"
        }
        
        // Crear una SortedList para asegurar que se ordenen correctamente
        SortedList<Batch> sortedData = new SortedList<>(pageData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        
        tableView.setItems(sortedData);
    }

    /**
     * Adds a tooltip to the cells of the given column to display additional information
     *
     * @param column the TableColumn to which the tooltip will be added
     * @param propertyIndex the index of the property in the Batch object that will be displayed in the tooltip
     */
    private void addTooltipToCells(TableColumn<Batch, String> column, int propertyIndex) {
        column.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Batch, String> call(TableColumn<Batch, String> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                            setText(null);
                            setTooltip(null);
                        } else {
                            setText(item);
                            // Crear y configurar el Tooltip con el valor de la celda
                            Batch batch = getTableView().getItems().get(getIndex());
                            StringProperty tooltipText;
                            if (batch != null) {
                                tooltipText = batch.getProperties()[propertyIndex];
                            } else {
                                tooltipText = new SimpleStringProperty("");
                            }
                            String text = divideText(tooltipText.getValue(), 50);
                            Tooltip tooltip = new Tooltip(text);
                            setTooltip(tooltip);
                            tooltip.setStyle("-fx-font-size: 20px; -fx-font-family: Futura medium;" + "-fx-background-color: #ffffff; -fx-text-fill: black; " + "-fx-border-color: black; -fx-border-width: 1px; -fx-border-radius: 0px;");
                        }
                    }
                };
            }
        });
    }

    /**
     * Divides a long string into multiple lines with a maximum length
     *
     * @param text the text to be divided into lines
     * @param maxLineLength the maximum length of each line
     * @return a string with the text divided into lines
     */
    private String divideText(String text, int maxLineLength) {
        StringBuilder wrappedText = new StringBuilder();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxLineLength, text.length());
            // Si el límite no cae en un espacio, ajusta para no cortar palabras
            if (end < text.length() && text.charAt(end) != ' ') {
                while (end > start && text.charAt(end) != ' ') {
                    end--;
                }
            }
            // Si no encuentra espacio, corta la línea directamente
            if (end == start) {
                end = Math.min(start + maxLineLength, text.length());
            }
            wrappedText.append(text, start, end).append("\n");
            start = end + 1; // Avanza al siguiente segmento, saltando el espacio
        }
        return wrappedText.toString().trim();
    }

    /**
     * Refreshes the TableView by reloading the data and updating the pagination
     */
    public void refreshTable() {
        int totalPage = (int) Math.ceil(weeklyBatchData.size() * 1.0 / ROWS_PER_PAGE);
        pagination.setPageCount(totalPage);
        
        // Recargar los datos de la tabla
        changeTableView(pagination.getCurrentPageIndex(), ROWS_PER_PAGE);
    }
    
    //Ventanas auxiliares

    /**
     * Opens a new batch creation window
     *
     * @param batchData the list of batches to be displayed in the new batch creation window
     */
    @FXML
    private void openNewBatch(ObservableList<Batch> batchData) {
        try {
            // Cargar el archivo FXML del popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/newBatch.fxml"));
            Parent popupRoot = fxmlLoader.load();
            NewBatchController newBatch = fxmlLoader.getController();
            newBatch.setBatchData(batchData, weeklyBatchData);
            newBatch.setBatchesListController(this);
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

    /**
     * Opens the batch edit window for the selected batch
     *
     * @param sampleBatch the batch to be edited
     */
    @FXML
    private void openEditBatch(Batch sampleBatch) {
        if (sampleBatch.getStatus() != Statuses.EN_ESPERA) {
            openError(new FXMLLoader(getClass().getResource("/fxml/errorBatchInProgress.fxml")));
        } else {
            try {
                // Cargar el archivo FXML del popup
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/WeeklyEditBatches.fxml"));
                Parent popupRoot = fxmlLoader.load();
                WeeklyEditBatchesController editBatches = fxmlLoader.getController();
                // Pasar el objeto batch al controlador de la ventana de edición
                editBatches.setBatch(sampleBatch);
                editBatches.setBatchData(batchData, weeklyBatchData);
                editBatches.setBatchesListController(this);
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
                editBatches.setStage(popupStage);
                // Mostrar el popup
                popupStage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the pagination control for the weekly batches list
     *
     * @return the pagination control
     */
    public Pagination getPagination() {
        return this.pagination;
    }

    /**
     * Opens an error window with the provided FXML loader
     *
     * @param fxmlLoader the FXML loader for the error window
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
                double centerX = addButton.getScene().getWindow().getX() + addButton.getScene().getWindow().getWidth() / 2;
                double centerY = addButton.getScene().getWindow().getY() + addButton.getScene().getWindow().getHeight() / 2;
                // Calcular posición para centrar el popup
                popupStage.setX(centerX - popupStage.getWidth() / 2);
                popupStage.setY(centerY - popupStage.getHeight() / 2);
            });
            popupStage.show();
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), // Duración antes de ejecutar la acción
                    event -> popupStage.close() // Acción para cerrar la ventana
            ));
            timeline.setCycleCount(1); // Ejecutar solo una vez
            timeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a windows displaying the full list of batches centered relative to the main window to prevent interaction with it while open
     */
    @FXML
    private void openFullBatchesList() {
        try {
            // Cargar el archivo FXML del popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/BatchesList.fxml"));
            Parent popupRoot = fxmlLoader.load();
            Stage popupStage = new Stage();
            BatchesListController batchList = fxmlLoader.getController();
            batchList.setBatchesList(batchData, weeklyBatchData);
            batchList.setBatchesListController(this);
            popupStage.resizableProperty().setValue(Boolean.FALSE);
            popupStage.setTitle("Lista Completa de Lotes");
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

    /**
     * Retrieves all batch data from the database and returns it as an ObservableList
     *
     * @return ObservableList containing all batches
     */
    private ObservableList<Batch> getFullData() {
        try {
            batchData.addAll(databaseManager.getAllBatches());
        } catch (PPGSchedulerException e) {
            e.printStackTrace();
        }
        return batchData;
    }
    
    //Planificar lotes

    /**
     * Initiates the batch planning process, displaying a popup window and performing the batch insertion that runs in the background to avoid blocking the UI
     *
     * @throws PPGSchedulerException exception thrown if an error occurs while planning the batches
     */
    @FXML
    public void planBatches() throws PPGSchedulerException {
        try {
            // Cargar el archivo FXML del popup
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/timeInfo.fxml"));
            Parent popupRoot = fxmlLoader.load();
            Stage popupStage = new Stage();
            popupStage.resizableProperty().setValue(Boolean.FALSE);
            popupStage.setTitle("INFO");
            popupStage.initModality(Modality.APPLICATION_MODAL); // Bloquear la ventana principal
            popupStage.setScene(new Scene(popupRoot));
            popupStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/info_icon.png")));
            popupStage.setOnShown(event -> {
                // Obtener dimensiones de la ventana principal o pantalla
                double centerX = addButton.getScene().getWindow().getX() + addButton.getScene().getWindow().getWidth() / 2;
                double centerY = addButton.getScene().getWindow().getY() + addButton.getScene().getWindow().getHeight() / 2;
                // Calcular posición para centrar el popup
                popupStage.setX(centerX - popupStage.getWidth() / 2);
                popupStage.setY(centerY - popupStage.getHeight() / 2);
            });
            popupStage.show();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Task<Void> periodicTask = new Task<>() {
                @Override
                protected Void call() {
                    try {
                            PPGScheduler scheduler = new PPGScheduler(operationCompleted);
                            fillList(batchesToPlan);
                            try {
                                listToUpdate = scheduler.insert(batchesToPlan);
                            } catch (CantAddException e) {
                                e.printStackTrace();
                            }
                        while (!operationCompleted.get()) {
                            // Ejecutar la lógica del ciclo
                                                        // Verificar si debe terminar
                            if (operationCompleted.get()) {
                                break;
                            }

                            // Esperar 2 segundos antes de la próxima iteración
                            Thread.sleep(2000);
                        }
                    } catch (InterruptedException | PPGSchedulerException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    // Tareas de limpieza al finalizar
                    updateList(weeklyBatchData);
                    updateList(batchData);
                    popupStage.close();
                }
            };

// Ejecutar la tarea en un solo hilo
            executor.submit(periodicTask);
        } catch (Exception e) {
            e.printStackTrace();
        }
        tableView.refresh();
    }

    /**
     * Checks if a given batch is contained within a specified list of batches
     *
     * @param batch the batch to be checked
     * @param list the list of batches to search through
     * @return true if the batch is found in the list, false otherwise
     */
    public boolean contains(Batch batch, ObservableList<Batch> list) {
        for (Batch value : list) {
            if (value.getnBatch() == batch.getnBatch()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves the index of a given batch within a specified list of batches
     *
     * @param batch the batch to search for
     * @param list the list of batches to search through
     * @return the index of the batch in the list, -1 if it is not found
     */
    public int getIndex(Batch batch, ObservableList<Batch> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getnBatch() == batch.getnBatch()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Starts processing batches based on their status and start date updating the status of each batch accordingly
     *
     * @param list the list of batches to process
     */
    private void startBatches(ObservableList<Batch> list) {
        for (Batch batchToStart : list) {
            if (batchToStart.getStartDate().plusDays(batchToStart.getDuration()).isBefore(LocalDate.now())) {
                batchToStart.setStatus(Statuses.FINALIZADO);
            }
            if (batchToStart.getStatus() == Statuses.EN_ESPERA && batchToStart.getStartDate().isBefore(LocalDate.now()) && (batchToStart.getStartDate().plusDays(batchToStart.getDuration()).isBefore(batchToStart.getNeedDate()))) {
                batchToStart.setStatus(Statuses.EN_ADELANTO);
            } else if (batchToStart.getStatus() == Statuses.EN_ESPERA && batchToStart.getStartDate().isBefore(LocalDate.now()) && batchToStart.getStartDate().plusDays(batchToStart.getDuration()).isAfter(batchToStart.getNeedDate())) {
                batchToStart.setStatus(Statuses.EN_DEMORA);
            } else if (batchToStart.getStatus() == Statuses.EN_ESPERA && batchToStart.getStartDate().isBefore(LocalDate.now())) {
                batchToStart.setStatus(Statuses.EN_PROCESO);
            }
            /*
                try {
            DESCOMENTAR ESTA LÍNEA PONE EN RIESGO LA INTEGRIDAD DE LA BBDD PUESTO QUE MODIFICA EL LOTE SELECCIONADO
                    databaseManager.updateBatchDB(batchToStart);
                } catch (PPGSchedulerException e) {
                    e.printStackTrace();
                }
                */
        }
    }

    /**
     * Updates the list of batches with new information if a batch exists in both lists
     *
     * @param list the list of batches to update
     */
    private void updateList(ObservableList<Batch> list) {
        for (Batch batch : listToUpdate) {
            if (contains(batch, list)) {
                int index = getIndex(batch, list);
                Batch batchToUpdate = list.get(index);
                batchToUpdate.setStartDate(batch.startDate());
                if (contains(batch, weeklyBatchData) && batchToUpdate.getStartDate().get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()) != weekNumber) {
                    weeklyBatchData.remove(index);
                }
            }
        }
        tableView.refresh();
    }

    /**
     * Fills a list with all batches that are in the waiting status
     *
     * @param list the list to be filled with waiting batches
     */
    private void fillList(ArrayList<Batch> list) {
        int i = 0;
        for(Batch b: batchData) {
            if (b.getStatus() == Statuses.EN_ESPERA) {
                list.add(b);
            }
        }
    }
}
