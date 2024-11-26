package org.ppg.view;

import javafx.scene.image.Image;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class BatchesList extends Application {
    Image logoPPG = new Image(String.valueOf(getClass().getResource("/images/PPG_Logo512_512.png")));

    //Method to launch the application
    public static void main(String[] args){
        launch(args);
    }
    
    //JavaFX First Window Init
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
}
