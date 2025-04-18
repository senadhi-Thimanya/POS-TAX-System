package com.example.tax;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class TaxHomeController {
    @FXML
    private Button importButton;

    @FXML
    private void handleImportFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Transaction File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(importButton.getScene().getWindow());

        if (selectedFile != null) {
            importTransactionFile(selectedFile);
        }
    }

    private void importTransactionFile(File file) {
        try {
            // Read the CSV file
            List<String> lines = Files.readAllLines(file.toPath());

            // Process the file (this is just a basic example)
            for (String line : lines) {
                // Skip header if present
                if (lines.indexOf(line) == 0 && line.contains("header")) {
                    continue;
                }

                // Parse the CSV line (this depends on your CSV format)
                String[] data = line.split(",");

                // Process the data (you'll need to adapt this to your specific needs)
                System.out.println("Processed transaction: " + Arrays.toString(data));

                // Here you would typically:
                // 1. Create transaction objects from the data
                // 2. Store them in your application's data model
                // 3. Update your UI as needed
            }

            // After successful import, you could switch to another scene
            // But as requested, we'll leave that part for later
            System.out.println("File imported successfully: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            // You might want to show an error dialog to the user
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Import Error");
            alert.setHeaderText("Failed to import file");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
