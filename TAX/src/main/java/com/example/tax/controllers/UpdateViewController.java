package com.example.tax.controllers;

import com.example.tax.models.Transaction;
import com.example.tax.utils.AlertUtils;
import com.example.tax.utils.ChecksumUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller for the transaction update view.
 * Handles editing transaction details and recalculating checksums.
 */
public class UpdateViewController {
    @FXML
    private TextField itemCodeField;
    @FXML
    private TextField costField;
    @FXML
    private TextField salePriceField;
    @FXML
    private TextField discountField;
    @FXML
    private Label discPriceLabel;
    @FXML
    private Label checksumLabel;
    @FXML
    private Button updateBtn;
    @FXML
    private Button cancelBtn;

    private Transaction transaction;
    private boolean hasChanges = false;

    /**
     * Sets the transaction to be edited and populates the form fields.
     *
     * @param transaction The transaction to edit
     */
    @FXML
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        itemCodeField.setText(transaction.getItemCode());
        costField.setText(String.valueOf(transaction.getCost()));
        salePriceField.setText(String.valueOf(transaction.getSalePrice()));
        discountField.setText(String.valueOf(transaction.getDiscount()));
        discPriceLabel.setText(String.valueOf(transaction.getDiscountedPrice()));
        checksumLabel.setText(transaction.getChecksum());

        // Track changes in fields
        itemCodeField.textProperty().addListener((observable, oldValue, newValue) -> {
            hasChanges = true;
        });

        costField.textProperty().addListener((observable, oldValue, newValue) -> {
            hasChanges = true;
        });

        salePriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            hasChanges = true;
        });

        discountField.textProperty().addListener((observable, oldValue, newValue) -> {
            hasChanges = true;
        });
    }

    /**
     * Initializes the controller.
     * Sets up listeners for fields that affect the discounted price.
     */
    @FXML
    private void initialize() {
        // Add listeners to the fields that affect discounted price
        salePriceField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateDiscountedPrice();
        });
        discountField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateDiscountedPrice();
        });
        // Initial calculation
        updateDiscountedPrice();
    }

    /**
     * Updates the discounted price label based on sale price and discount.
     * Called whenever sale price or discount changes.
     */
    private void updateDiscountedPrice() {
        try {
            double salePrice = Double.parseDouble(salePriceField.getText());
            double discount = Double.parseDouble(discountField.getText());
            // Calculate the discounted price
            double discountedPrice = salePrice - (salePrice * discount / 100);
            // Format and set the calculated value to the label
            discPriceLabel.setText(String.format("%.1f", discountedPrice));
        } catch (NumberFormatException e) {
            // Handle invalid input
            discPriceLabel.setText("");
        }
    }

    /**
     * Validates if the item code contains any special characters except underscore.
     *
     * @param itemCode The item code to validate
     * @return true if valid, false if it contains special characters
     */
    private boolean isValidItemCode(String itemCode) {
        return !itemCode.matches(".*[!@#$%^&*()+=\\[\\]{}|;:'\",.<>/?`~-].*");
    }

    /**
     * Generates a new checksum based on the current field values.
     * Called when the user clicks the "Generate Checksum" button.
     */
    @FXML
    private void generateChecksum() {
        try {
            String itemCode = itemCodeField.getText();

            // Validate item code
            if (!isValidItemCode(itemCode)) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Item code contains invalid special characters");
                return;
            }

            double cost = Double.parseDouble(costField.getText());
            double salePrice = Double.parseDouble(salePriceField.getText());
            double discount = Double.parseDouble(discountField.getText());

            // Validate numeric values
            if (cost < 0) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Cost cannot be negative");
                return;
            }

            if (salePrice < 0) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Sale price cannot be negative");
                return;
            }

            if (discount < 0 || discount > 100) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Discount must be between 0 and 100");
                return;
            }

            // Calculate discounted price
            double discountedPrice = salePrice - (salePrice * discount / 100);

            // Use the utility class to format transaction line and calculate checksum
            String transactionLine = ChecksumUtil.formatTransactionLine(
                    itemCode, cost, salePrice, discount, discountedPrice);

            int calculatedChecksum = ChecksumUtil.calculateChecksum(transactionLine);

            // Update the checksum label
            checksumLabel.setText(String.valueOf(calculatedChecksum));
        } catch (NumberFormatException e) {
            // Handle invalid input
            AlertUtils.showError("Validation Error", "Invalid Input", "Please enter valid numeric values for all fields");
        }
    }

    /**
     * Saves the updated transaction values and closes the window.
     * Called when the user clicks the "Save" button.
     */
    @FXML
    private void handleSave() {
        try {
            // Validate item code
            String itemCode = itemCodeField.getText();
            if (itemCode.isEmpty()) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Item code cannot be empty");
                return;
            }
            if (!isValidItemCode(itemCode)) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Item code contains invalid special characters");
                return;
            }

            // Validate numeric fields
            double cost = Double.parseDouble(costField.getText());
            double salePrice = Double.parseDouble(salePriceField.getText());
            double discount = Double.parseDouble(discountField.getText());

            // Check for negative values
            if (cost < 0) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Cost cannot be negative");
                return;
            }
            if (salePrice < 0) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Sale price cannot be negative");
                return;
            }

            // Check discount range
            if (discount < 0 || discount > 100) {
                AlertUtils.showError("Validation Error", "Invalid Input", "Discount must be between 0 and 100");
                return;
            }

            // Check if cost exceeds sale price
            if (cost > salePrice) {
                boolean proceed = AlertUtils.showWarningWithOptions(
                        "Warning",
                        "Cost exceeds Sale Price",
                        "The cost is higher than the sale price, which will result in negative profit."
                );
                if (!proceed) {
                    return;
                }
            }


            // Auto-generate checksum if needed
            if (checksumLabel.getText().isEmpty() || hasChanges) {
                generateChecksum();
            }

            // Update the transaction with the new values
            transaction.setItemCode(itemCode);
            transaction.setCost(cost);
            transaction.setSalePrice(salePrice);
            transaction.setDiscount(discount);
            transaction.setDiscountedPrice(Double.parseDouble(discPriceLabel.getText()));
            transaction.setChecksum(checksumLabel.getText());
            transaction.setProfit(transaction.getDiscountedPrice() - transaction.getCost());

            // Close the window
            closeWindow();
        } catch (NumberFormatException e) {
            AlertUtils.showError("Validation Error", "Invalid Input", "Please enter valid numeric values for all fields");
        }
    }

    /**
     * Cancels the edit operation and closes the window.
     * Called when the user clicks the "Cancel" button.
     */
    @FXML
    private void handleCancel() {
        if (hasChanges) {
            boolean proceed = AlertUtils.showConfirmation(
                    "Confirm Cancel",
                    "Unsaved Changes",
                    "You have unsaved changes. Are you sure you want to cancel?"
            );
            if (!proceed) {
                return;
            }
        }

        closeWindow();
    }

    /**
     * Closes the update window.
     */
    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }

}
