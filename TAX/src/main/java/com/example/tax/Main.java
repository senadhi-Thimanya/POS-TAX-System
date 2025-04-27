package com.example.tax;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Main application class for the Tax Department System.
 * Initializes the JavaFX application and loads the home view.
 */
public class Main extends Application {
    /**
     * Starts the JavaFX application.
     * Loads the tax-home-view.fxml file and displays it in a new stage.
     *
     * @param stage The primary stage for this application
     * @throws IOException If the FXML file cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("fxml/tax-home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Tax Dpt Application");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Main method to launch the application.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args); // Pass args to launch method
    }
}
