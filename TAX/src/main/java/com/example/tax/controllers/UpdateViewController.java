package com.example.tax.controllers;

import com.example.tax.models.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

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

    @FXML
    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;

        itemCodeField.setText(transaction.getItemCode());
        costField.setText(String.valueOf(transaction.getCost()));
        salePriceField.setText(String.valueOf(transaction.getSalePrice()));
        discountField.setText(String.valueOf(transaction.getDiscount()));
        discPriceLabel.setText(String.valueOf(transaction.getDiscountedPrice()));
        checksumLabel.setText(transaction.getChecksum());
    }

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

    @FXML
    private void generateChecksum() {
        try {
            String itemCode = itemCodeField.getText();
            double cost = Double.parseDouble(costField.getText());
            double salePrice = Double.parseDouble(salePriceField.getText());
            double discount = Double.parseDouble(discountField.getText());

            // Calculate discounted price
            double discountedPrice = salePrice - (salePrice * discount / 100);

            // Create transaction line string including the discounted price but excluding the checksum
            String transactionLine = String.format("%s,%.2f,%.2f,%.1f,%.2f",
                    itemCode, cost, salePrice, discount, discountedPrice);

            int capitalCount = 0;
            int simpleCount = 0;
            int numberCount = 0;

            // Count characters according to the rules
            for (char c : transactionLine.toCharArray()) {
                if (Character.isUpperCase(c)) {
                    capitalCount++;
                } else if (Character.isLowerCase(c)) {
                    simpleCount++;
                } else if (Character.isDigit(c) || c == '.') {
                    numberCount++;
                }
                // Note: Commas and underscores are not counted
            }

            // Calculate checksum
            int calculatedChecksum = capitalCount + simpleCount + numberCount;

            // Update the checksum label
            checksumLabel.setText(String.valueOf(calculatedChecksum));

        } catch (NumberFormatException e) {
            // Handle invalid input
            checksumLabel.setText("E");
        }
    }


    @FXML
    private void handleSave() {
        // Update the transaction with the new values
        transaction.setItemCode(itemCodeField.getText());
        transaction.setCost(Double.parseDouble(costField.getText()));
        transaction.setSalePrice(Double.parseDouble(salePriceField.getText()));
        transaction.setDiscount(Double.parseDouble(discountField.getText()));
        transaction.setDiscountedPrice(Double.parseDouble(discPriceLabel.getText()));
        transaction.setChecksum(checksumLabel.getText());
        transaction.setProfit(transaction.getDiscountedPrice() - transaction.getCost());

        // Close the window
        closeWindow();
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelBtn.getScene().getWindow();
        stage.close();
    }
}
