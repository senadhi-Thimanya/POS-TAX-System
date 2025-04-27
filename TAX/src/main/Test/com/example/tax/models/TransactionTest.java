package com.example.tax.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        // Create a new transaction before each test
        transaction = new Transaction("ITEM123", 100.00, 150.00, 10.0, "52");
    }

    @Test
    public void testConstructor() {
        assertEquals("ITEM123", transaction.getItemCode(), "Item code not set correctly");
        assertEquals(100.00, transaction.getCost(), 0.001, "Cost not set correctly");
        assertEquals(150.00, transaction.getSalePrice(), 0.001, "Sale price not set correctly");
        assertEquals(10.0, transaction.getDiscount(), 0.001, "Discount not set correctly");
        assertEquals("52", transaction.getChecksum(), "Checksum not set correctly");
    }

    @Test
    public void testCalculateDiscountedPrice() {
        // The discounted price should be calculated in the constructor
        double expectedDiscountedPrice = 150.00 - (150.00 * 10.0 / 100);
        assertEquals(expectedDiscountedPrice, transaction.getDiscountedPrice(), 0.001,
                "Discounted price calculation is incorrect");
    }

    @Test
    public void testCalculateProfit() {
        // The profit should be calculated in the constructor
        double expectedProfit = transaction.getDiscountedPrice() - 100.00;
        assertEquals(expectedProfit, transaction.getProfit(), 0.001,
                "Profit calculation is incorrect");
    }

    @Test
    public void testIsValidChecksumWithValidData() {
        // Mock a transaction with a valid checksum
        // This requires fixing the ChecksumUtil.calculateChecksum method first
        // For now, we'll use a simple approach

        // Create a transaction with a known valid checksum
        String itemCode = "ITEM123";
        double cost = 100.00;
        double salePrice = 150.00;
        double discount = 10.0;

        String transactionLine = String.format("%s,%.2f,%.2f,%.1f,%.2f",
                itemCode, cost, salePrice, discount, 135.00);
        int validChecksum = 52; // Assume this is the correct checksum

        Transaction validTransaction = new Transaction(
                itemCode, cost, salePrice, discount, String.valueOf(validChecksum));

        // Mock the checksum calculation to return the expected value
        // In a real test, you would use a mocking framework like Mockito
        // For now, this test might fail until ChecksumUtil is fixed

        assertTrue(validTransaction.isValidChecksum(),
                "Transaction with valid checksum should return true");
    }

    @Test
    public void testIsValidChecksumWithInvalidData() {
        // Test with invalid item code (contains special character)
        Transaction invalidTransaction = new Transaction(
                "ITEM@123", 100.00, 150.00, 10.0, "42");

        assertFalse(invalidTransaction.isValidChecksum(),
                "Transaction with invalid item code should return false");

        // Test with negative cost
        invalidTransaction = new Transaction(
                "ITEM123", -100.00, 150.00, 10.0, "42");

        assertFalse(invalidTransaction.isValidChecksum(),
                "Transaction with negative cost should return false");
    }
}
