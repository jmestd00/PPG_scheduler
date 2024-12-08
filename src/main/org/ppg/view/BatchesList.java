package org.ppg.view;

import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * Main class to launch the JavaFX application for managing and displaying a list of batches.
 * It initializes the primary window and loads the FXML layout for the weekly batches list.
 */
public class BatchesList extends Application {
    Image logoPPG = new Image(String.valueOf(getClass().getResource("/images/PPG_Logo512_512.png")));
    
    /**
     * Entry point for the JavaFX application.
     * This method launches the JavaFX application and calls the start method
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    /**
     * Initializes the JavaFX window and loads the FXML layout for the weekly batches list.
     * This method is called when the JavaFX application is launched
     *
     * @param primaryStage the primary stage for the application, representing the main window
     * @throws Exception exception thrown if any error occurs while loading the FXML file or initializing the window
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/weeklyBatchesList.fxml")));
        Scene scene = new Scene(root);

        primaryStage.setTitle("Lista de Lotes Semanal");
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(logoPPG);
        primaryStage.resizableProperty().setValue(Boolean.FALSE);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
