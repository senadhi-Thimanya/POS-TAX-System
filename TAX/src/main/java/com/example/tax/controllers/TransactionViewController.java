package com.example.tax.controllers;

import com.example.tax.models.Transaction;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

public class TransactionViewController {
    @FXML
    private TableView<Transaction> transactionTable;

    @FXML
    private TableColumn<Transaction, String> itemCode;

    @FXML
    private TableColumn<Transaction, Number> cost;

    @FXML
    private TableColumn<Transaction, Number> salePrice;

    @FXML
    private TableColumn<Transaction, Number> discount;

    @FXML
    private TableColumn<Transaction, Number> discountedPrice;

    @FXML
    private TableColumn<Transaction, String> checksum;

    @FXML
    private TableColumn<Transaction, String> validity;

    @FXML
    private TableColumn<Transaction, Number> profit;

    @FXML
    private Button backButton;

    @FXML
    private Label fillAllRecords;

    @FXML
    private Label fillValidRecords;

    @FXML
    private Label fillInvalidRecords;

    @FXML
    private Button editBtn;

    @FXML
    private TextField taxRateField;

    @FXML
    private Label finalTaxLabel;

    @FXML
    private Label ProfitLabel;

    public void setTransactions(List<Transaction> transactions) {
        if (transactionTable != null) {
            transactionTable.setItems(FXCollections.observableArrayList(transactions));

            // Update the labels with the counts
            int totalRecords = transactions.size();
            long validRecords = transactions.stream().filter(Transaction::isValidChecksum).count();
            long invalidRecords = totalRecords - validRecords;

            fillAllRecords.setText(String.valueOf(totalRecords));
            fillValidRecords.setText(String.valueOf(validRecords));
            fillInvalidRecords.setText(String.valueOf(invalidRecords));
        }
    }

    public void initializeColumns() {
        if (transactionTable == null) {
            return;
        }

        try {
            // Use PropertyValueFactory for simple properties
            itemCode.setCellValueFactory(cellData -> cellData.getValue().itemCodeProperty());
            cost.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCost()));
            salePrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSalePrice()));
            discount.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getDiscount()));
            discountedPrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getDiscountedPrice()));
            checksum.setCellValueFactory(cellData -> cellData.getValue().checksumProperty());

            // Set cell factory for validity column to show "Valid" or "Invalid"
            validity.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().isValidChecksum() ? "Valid" : "Invalid"));

            profit.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProfit()));


            // Force the table to refresh
            transactionTable.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEditButtonClick() {
        // Get the selected transaction
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();

        if (selectedTransaction == null) {
            // Show an alert or message that no transaction is selected
            return;
        }

        try {
            // Load the update-view.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/tax/fxml/update-view.fxml"));
            Scene updateScene = new Scene(loader.load());

            // Get the controller and pass the selected transaction
            UpdateViewController controller = loader.getController();
            controller.setTransaction(selectedTransaction);

            // Create a new stage for the update view
            Stage updateStage = new Stage();
            updateStage.setTitle("Update Transaction");
            updateStage.setScene(updateScene);

            // Set the owner to the current stage to make it modal
            updateStage.initOwner(editBtn.getScene().getWindow());

            // Show the update view
            updateStage.showAndWait();

            // After the update window is closed, refresh the table
            transactionTable.refresh();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void deleteRecordOnClick() {
        // Get the selected transaction
        Transaction selectedTransaction = (Transaction) transactionTable.getSelectionModel().getSelectedItem();

        // Check if a transaction is selected
        if (selectedTransaction == null) {
            // Show an alert or message that no transaction is selected
            return;
        }

        // Check if the selected transaction is valid
        if (selectedTransaction.isValidChecksum()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Cannot Delete Valid Record");
            alert.setContentText("Only invalid records can be deleted.");
            alert.showAndWait();
            return;
        }

        // Remove the selected transaction from the table
        transactionTable.getItems().remove(selectedTransaction);

        // Update the record counts
        int totalRecords = transactionTable.getItems().size();
        long validRecords = transactionTable.getItems().stream()
                .filter(t -> ((Transaction) t).isValidChecksum()).count();
        long invalidRecords = totalRecords - validRecords;

        // Update the labels with the new counts
        fillAllRecords.setText(String.valueOf(totalRecords));
        fillValidRecords.setText(String.valueOf(validRecords));
        fillInvalidRecords.setText(String.valueOf(invalidRecords));
    }

    @FXML
    private void deleteAllInvalidRecords() {
        // Create a list to store the invalid transactions
        List<Transaction> invalidTransactions = transactionTable.getItems().stream()
                .filter(transaction -> !transaction.isValidChecksum())
                .toList();

        // Remove all invalid transactions from the table
        transactionTable.getItems().removeAll(invalidTransactions);

        // Update the record counts
        int totalRecords = transactionTable.getItems().size();
        long validRecords = transactionTable.getItems().stream()
                .filter(Transaction::isValidChecksum).count();
        long invalidRecords = totalRecords - validRecords;

        // Update the labels with the new counts
        fillAllRecords.setText(String.valueOf(totalRecords));
        fillValidRecords.setText(String.valueOf(validRecords));
        fillInvalidRecords.setText(String.valueOf(invalidRecords));
    }

    @FXML
    private void deleteAllZeroProfitRecords() {
        // Create a list to store the zero profit transactions
        List<Transaction> zeroProfitTransactions = transactionTable.getItems().stream()
                .filter(transaction -> transaction.getProfit() == 0)
                .toList();

        // Remove all zero profit transactions from the table
        transactionTable.getItems().removeAll(zeroProfitTransactions);

        // Update the record counts
        int totalRecords = transactionTable.getItems().size();
        long validRecords = transactionTable.getItems().stream()
                .filter(Transaction::isValidChecksum).count();
        long invalidRecords = totalRecords - validRecords;

        // Update the labels with the new counts
        fillAllRecords.setText(String.valueOf(totalRecords));
        fillValidRecords.setText(String.valueOf(validRecords));
        fillInvalidRecords.setText(String.valueOf(invalidRecords));
    }

    @FXML
    private void calculateFinalTaxOnClick() {
        try {
            // Get the tax rate from the text field
            double taxRate = Double.parseDouble(taxRateField.getText()) / 100.0;

            // Calculate the sum of (Profit - Loss) for all items
            double totalProfit = 0.0;

            for (Transaction transaction : transactionTable.getItems()) {
                // Add the profit (which already represents profit - loss)
                totalProfit += transaction.getProfit();
            }

            // Calculate the final tax
            double finalTax = totalProfit * taxRate;

            // Display the result in the label
            finalTaxLabel.setText(String.format("Final Tax: Rs.%.2f", finalTax));
            ProfitLabel.setText(String.format("Total Profit: Rs.%.2f", totalProfit));

        } catch (NumberFormatException e) {
            // Handle invalid input in the tax rate field
            finalTaxLabel.setText("Invalid tax rate. Please enter a valid number.");
        } catch (Exception e) {
            // Handle other exceptions
            finalTaxLabel.setText("Error calculating tax: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void goBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/tax/fxml/tax-home-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}