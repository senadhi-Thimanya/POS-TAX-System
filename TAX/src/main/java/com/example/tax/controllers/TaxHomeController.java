package com.example.tax.controllers;

import com.example.tax.models.Transaction;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TaxHomeController {
    @FXML
    private Button importButton;

    private List<Transaction> transactions = new ArrayList<>();

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
                                data[0].trim(),                    // itemCode
                                Double.parseDouble(data[1].trim()), // cost
                                Double.parseDouble(data[2].trim()), // salePrice
                                Double.parseDouble(data[3].trim()), // discount
                                data[5].trim()                     // checksum at index 5
                        );
                        transactions.add(transaction);
                    } catch (NumberFormatException e) {
                        showError("Invalid number format in line " + (i + 1) + ": " + e.getMessage());
                        e.printStackTrace();
                        return;
                    } catch (Exception e) {
                        showError("Error processing line " + (i + 1) + ": " + e.getMessage());
                        e.printStackTrace();
                        return;
                    }
                } else {
                    showError("Invalid data format in line " + (i + 1) + ". Expected at least 6 columns, but found " + data.length);
                    return;
                }
            }

            if (transactions.isEmpty()) {
                showError("No valid transactions found in the file");
                return;
            }

        } catch (IOException e) {
            showError("Error reading file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showTransactionView() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/tax/transaction-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            TransactionViewController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.setTransactions(transactions);
                controller.initializeColumns();

                Stage stage = (Stage) importButton.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                showError("Failed to initialize transaction view controller");
            }
        } catch (IOException e) {
            showError("Error loading transaction view: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showError("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Import Error");
        alert.setHeaderText("Failed to process file");
        alert.setContentText(message);
        alert.showAndWait();
    }
}