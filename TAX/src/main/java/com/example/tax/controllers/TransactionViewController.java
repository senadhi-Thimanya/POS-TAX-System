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
import javafx.util.Callback;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.List;

/**
 * Controller for the transaction view.
 * Displays transactions in a table and provides functionality for editing, deleting,
 * and calculating tax based on transaction profits.
 */
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

    /**
     * Initializes the controller.
     * Sets up initial values for labels and UI components.
     */
    @FXML
    private void initialize() {
        // Initialize the profit label with zero
        ProfitLabel.setText("Total Profit: LKR 0.00");
    }

    /**
     * Sets the transactions to display in the table.
     *
     * @param transactions List of transactions to display
     */
    public void setTransactions(List<Transaction> transactions) {
        if (transactionTable != null) {
            transactionTable.setItems(FXCollections.observableArrayList(transactions));
            updateRecordCounts();
        }
    }

    /**
     * Updates the record count labels and total profit display.
     * Called whenever the transaction table changes.
     */
    private void updateRecordCounts() {
        int totalRecords = transactionTable.getItems().size();
        long validRecords = transactionTable.getItems().stream()
                .filter(Transaction::isValidChecksum).count();
        long invalidRecords = totalRecords - validRecords;

        fillAllRecords.setText(String.valueOf(totalRecords));
        fillValidRecords.setText(String.valueOf(validRecords));
        fillInvalidRecords.setText(String.valueOf(invalidRecords));
        updateTotalProfit();
    }

    /**
     * Calculates and displays the total profit from all transactions.
     * Called whenever the transaction table changes.
     */
    private void updateTotalProfit() {
        double totalProfit = 0.0;
        for (Transaction transaction : transactionTable.getItems()) {
            totalProfit += transaction.getProfit();
        }
        ProfitLabel.setText(String.format("Total Profit: LKR %.2f", totalProfit));
    }

    /**
     * Initializes the table columns with cell factories and value factories.
     * Sets up custom formatting for discount and validity columns.
     */
    public void initializeColumns() {
        if (transactionTable == null) {
            return;
        }

        try {
            // Use PropertyValueFactory for simple properties
            itemCode.setCellValueFactory(cellData -> cellData.getValue().itemCodeProperty());
            cost.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getCost()));
            salePrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSalePrice()));

            // Set discount column to display as percentage
            discount.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getDiscount()));
            discount.setCellFactory(column -> new TableCell<Transaction, Number>() {
                @Override
                protected void updateItem(Number value, boolean empty) {
                    super.updateItem(value, empty);
                    if (empty || value == null) {
                        setText(null);
                    } else {
                        setText(value.doubleValue() + "%");
                    }
                }
            });

            discountedPrice.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getDiscountedPrice()));
            checksum.setCellValueFactory(cellData -> cellData.getValue().checksumProperty());

            // Set cell factory for validity column to show "Valid" or "Invalid" with colors
            validity.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().isValidChecksum() ? "Valid" : "Invalid"));
            validity.setCellFactory(column -> new TableCell<Transaction, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty || item == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);

                        // Set text color based on validity
                        if (item.equals("Valid")) {
                            setTextFill(Color.GREEN);
                        } else {
                            setTextFill(Color.RED);
                        }
                    }
                }
            });

            profit.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getProfit()));

            // Force the table to refresh
            transactionTable.refresh();
            updateTotalProfit();
        } catch (Exception e) {
            showError("Column Initialization Error", "Failed to initialize columns", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Handles the edit button click.
     * Opens a new window for editing the selected transaction.
     */
    @FXML
    private void handleEditButtonClick() {
        // Get the selected transaction
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();
        if (selectedTransaction == null) {
            showError("Selection Error", "No Transaction Selected", "Please select a transaction to edit.");
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
            updateTotalProfit();
        } catch (IOException e) {
            showError("Update Error", "Failed to open update window", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Deletes the selected transaction if it is invalid.
     * Valid transactions cannot be deleted.
     */
    @FXML
    private void deleteRecordOnClick() {
        // Get the selected transaction
        Transaction selectedTransaction = transactionTable.getSelectionModel().getSelectedItem();

        // Check if a transaction is selected
        if (selectedTransaction == null) {
            showError("Selection Error", "No Transaction Selected", "Please select a transaction to delete.");
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
        updateRecordCounts();
    }

    /**
     * Deletes all invalid transactions from the table.
     */
    @FXML
    private void deleteAllInvalidRecords() {
        // Create a list to store the invalid transactions
        List<Transaction> invalidTransactions = transactionTable.getItems().stream()
                .filter(transaction -> !transaction.isValidChecksum())
                .toList();

        // Remove all invalid transactions from the table
        transactionTable.getItems().removeAll(invalidTransactions);
        updateRecordCounts();
    }

    /**
     * Deletes all transactions with zero profit from the table.
     */
    @FXML
    private void deleteAllZeroProfitRecords() {
        // Create a list to store the zero profit transactions
        List<Transaction> zeroProfitTransactions = transactionTable.getItems().stream()
                .filter(transaction -> transaction.getProfit() == 0)
                .toList();

        // Remove all zero profit transactions from the table
        transactionTable.getItems().removeAll(zeroProfitTransactions);
        updateRecordCounts();
    }

    /**
     * Calculates the final tax based on the total profit and tax rate.
     * Displays the result in the finalTaxLabel.
     */
    @FXML
    private void calculateFinalTaxOnClick() {
        try {
            // Get the tax rate from the text field
            double taxRate = Double.parseDouble(taxRateField.getText()) / 100.0;

            // Get the current total profit value
            String profitText = ProfitLabel.getText().replace("Total Profit: LKR ", "");
            double totalProfit = Double.parseDouble(profitText);

            // Calculate the final tax
            double finalTax = totalProfit * taxRate;

            // Display the final tax
            finalTaxLabel.setText(String.format("Final Tax: LKR %.2f", finalTax));
        } catch (NumberFormatException e) {
            // Handle invalid input in the tax rate field
            finalTaxLabel.setText("Invalid tax rate. Please enter a valid number.");
        } catch (Exception e) {
            // Handle other exceptions
            finalTaxLabel.setText("Error calculating tax: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Navigates back to the home view.
     */
    @FXML
    private void goBack() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/tax/fxml/tax-home-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Navigation Error", "Failed to go back", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Utility method to show error alerts.
     *
     * @param title The alert title
     * @param header The alert header text
     * @param content The alert content text
     */
    private void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
