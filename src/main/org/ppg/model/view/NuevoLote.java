package org.ppg.model.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Objects;

public class NuevoLote extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/nuevoLote.fxml")));
        Scene scene = new Scene(root);
        
        primaryStage.setTitle("Insertar nuevo lote");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
