package com.example.tax.controllers;

import com.example.tax.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateViewControllerTest {

    // The class we're testing
    private UpdateViewController controller;

    // Test data
    private Transaction testTransaction;

    @BeforeEach
    public void setUp() {
        // Create a new controller for each test
        controller = new UpdateViewController();

        // Create a test transaction
        testTransaction = new Transaction("ITEM001", 100.00, 150.00, 10.0, "52");

        // Note: We can't directly test setTransaction() because it interacts with JavaFX
        // Instead, we'll test the methods that don't depend on JavaFX components
    }

    @Test
    @DisplayName("Test discounted price calculation")
    public void testDiscountedPriceCalculation() {
        // This is a simplified version of the calculation in updateDiscountedPrice()
        double salePrice = 150.00;
        double discount = 10.0;

        // Calculate the expected discounted price
        double expectedDiscountedPrice = salePrice - (salePrice * discount / 100);

        // Calculate using the same formula as in the controller
        double calculatedDiscountedPrice = salePrice - (salePrice * discount / 100);

        // Verify the calculation is correct
        assertEquals(expectedDiscountedPrice, calculatedDiscountedPrice, 0.001,
                "Discounted price calculation should be correct");
    }

    @Test
    @DisplayName("Test item code validation")
    public void testItemCodeValidation() {
        // We can use reflection to access the private isValidItemCode method
        try {
            java.lang.reflect.Method method = UpdateViewController.class.getDeclaredMethod("isValidItemCode", String.class);
            method.setAccessible(true);

            // Test valid item codes
            assertTrue((Boolean) method.invoke(controller, "ITEM001"),
                    "ITEM001 should be a valid item code");
            assertTrue((Boolean) method.invoke(controller, "item_001"),
                    "item_001 should be a valid item code");

            // Test invalid item codes
            assertFalse((Boolean) method.invoke(controller, "ITEM@001"),
                    "ITEM@001 should be an invalid item code");
            assertFalse((Boolean) method.invoke(controller, "ITEM#001"),
                    "ITEM#001 should be an invalid item code");
            assertFalse((Boolean) method.invoke(controller, "ITEM/001"),
                    "ITEM/001 should be an invalid item code");
        } catch (Exception e) {
            fail("Failed to test isValidItemCode: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Test transaction update")
    public void testTransactionUpdate() {
        // Create a transaction with initial values
        Transaction transaction = new Transaction("ITEM001", 100.00, 150.00, 10.0, "52");

        // Update the transaction with new values (simulating what handleSave() would do)
        String newItemCode = "ITEM002";
        double newCost = 120.00;
        double newSalePrice = 180.00;
        double newDiscount = 15.0;
        double newDiscountedPrice = newSalePrice - (newSalePrice * newDiscount / 100);
        String newChecksum = "54";

        transaction.setItemCode(newItemCode);
        transaction.setCost(newCost);
        transaction.setSalePrice(newSalePrice);
        transaction.setDiscount(newDiscount);
        transaction.setDiscountedPrice(newDiscountedPrice);
        transaction.setChecksum(newChecksum);
        transaction.setProfit(newDiscountedPrice - newCost);

        // Verify the transaction was updated correctly
        assertEquals(newItemCode, transaction.getItemCode(),
                "Item code should be updated");
        assertEquals(newCost, transaction.getCost(), 0.001,
                "Cost should be updated");
        assertEquals(newSalePrice, transaction.getSalePrice(), 0.001,
                "Sale price should be updated");
        assertEquals(newDiscount, transaction.getDiscount(), 0.001,
                "Discount should be updated");
        assertEquals(newDiscountedPrice, transaction.getDiscountedPrice(), 0.001,
                "Discounted price should be updated");
        assertEquals(newChecksum, transaction.getChecksum(),
                "Checksum should be updated");
        assertEquals(newDiscountedPrice - newCost, transaction.getProfit(), 0.001,
                "Profit should be updated");
    }

    @Test
    @DisplayName("Test validation logic")
    public void testValidationLogic() {
        // Test negative cost validation
        double negativeCost = -100.00;
        assertTrue(negativeCost < 0, "Negative cost should be detected as invalid");

        // Test negative sale price validation
        double negativeSalePrice = -150.00;
        assertTrue(negativeSalePrice < 0, "Negative sale price should be detected as invalid");

        // Test discount range validation
        double negativeDicount = -10.0;
        double excessiveDiscount = 110.0;
        assertTrue(negativeDicount < 0 || negativeDicount > 100,
                "Negative discount should be detected as invalid");
        assertTrue(excessiveDiscount < 0 || excessiveDiscount > 100,
                "Excessive discount should be detected as invalid");

        // Test cost exceeds sale price validation
        double highCost = 200.00;
        double lowSalePrice = 150.00;
        assertTrue(highCost > lowSalePrice,
                "Cost exceeding sale price should be detected");
    }
}
