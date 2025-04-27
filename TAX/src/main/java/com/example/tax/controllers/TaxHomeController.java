package com.example.tax.controllers;

import com.example.tax.models.Transaction;
import com.example.tax.utils.AlertUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the home view of the Tax Department System.
 * Handles importing transaction files and navigating to the transaction view.
 */
public class TaxHomeController {
    @FXML
    private Button importButton;
    @FXML
    private ImageView lavenderImageView;

    private List<Transaction> transactions = new ArrayList<>();

    /**
     * Handles the import button click.
     * Opens a file chooser dialog and imports the selected file.
     */
    @FXML
    private void handleImportFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Transaction File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(importButton.getScene().getWindow());

        if (selectedFile != null) {
            importTransactionFile(selectedFile);
            if (!transactions.isEmpty()) {
                showTransactionView();
            }
        }
    }

    /**
     * Imports transactions from a CSV file.
     * Parses each line and creates Transaction objects.
     *
     * @param file The CSV file to import
     */
    private void importTransactionFile(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            transactions.clear();

            // Skip header
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;

                String[] data = line.split(",");

                // Make sure we have enough columns (ItemCode,Cost,SalePrice,Discount,DiscountedPrice,Checksum)
                if (data.length >= 6) {
                    try {
                        Transaction transaction = new Transaction(
                                data[0].trim(), // itemCode
                                Double.parseDouble(data[1].trim()), // cost
                                Double.parseDouble(data[2].trim()), // salePrice
                                Double.parseDouble(data[3].trim()), // discount
                                data[5].trim() // checksum at index 5
                        );
                        transactions.add(transaction);
                    } catch (NumberFormatException e) {
                        AlertUtils.showError("Import Error", "Failed to process file", "Invalid number format in line " + (i + 1) + ": " + e.getMessage());
                        e.printStackTrace();
                        return;
                    } catch (Exception e) {
                        AlertUtils.showError("Import Error", "Failed to process file", "Error processing line " + (i + 1) + ": " + e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                } else {
                    AlertUtils.showError("Import Error", "Failed to process file", "Invalid data format in line " + (i + 1) + ". Expected at least 6 columns, but found " + data.length);
                    return;
                }
            }

            if (transactions.isEmpty()) {
                AlertUtils.showError("Import Error", "Failed to process file", "No valid transactions found in the file");
                return;
            }

        } catch (IOException e) {
            AlertUtils.showError("Import Error", "Failed to process file", "Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Shows the transaction view with the imported transactions.
     * Navigates to the transaction-view.fxml scene.
     */
    private void showTransactionView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/tax/fxml/transaction-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            TransactionViewController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.setTransactions(transactions);
                controller.initializeColumns();

                Stage stage = (Stage) importButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                AlertUtils.showError("Import Error", "Failed to process file", "Failed to initialize transaction view controller");
            }

        } catch (IOException e) {
            AlertUtils.showError("Import Error", "Failed to process file", "Error loading transaction view: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            AlertUtils.showError("Import Error", "Failed to process file", "Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
