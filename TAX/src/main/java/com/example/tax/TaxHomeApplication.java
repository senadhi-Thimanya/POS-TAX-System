package com.example.tax;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class TaxHomeApplication extends Application{
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(TaxHomeApplication.class.getResource("tax-home-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Tax Dpt Application");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);  // Pass args to launch method
    }
}
