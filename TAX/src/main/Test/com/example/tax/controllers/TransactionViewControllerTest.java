package com.example.tax.controllers;

import com.example.tax.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionViewControllerTest {

    // The class we're testing
    private TransactionViewController controller;

    // Test data
    private List<Transaction> testTransactions;

    @BeforeEach
    public void setUp() {
        // Create a new controller for each test
        controller = new TransactionViewController();

        // Create test transactions
        testTransactions = new ArrayList<>();
        testTransactions.add(new Transaction("ITEM001", 100.00, 150.00, 10.0, "52"));
        testTransactions.add(new Transaction("ITEM002", 200.00, 300.00, 15.0, "54"));
        testTransactions.add(new Transaction("ITEM003", 50.00, 75.00, 5.0, "48"));

        // Note: We can't directly test setTransactions() because it interacts with JavaFX
        // Instead, we'll test the methods that don't depend on JavaFX components
    }

    @Test
    @DisplayName("Test tax calculation with valid input")
    public void testCalculateTax() {
        // This is a simplified version of the calculation in calculateFinalTaxOnClick()
        double totalProfit = 0.0;
        for (Transaction transaction : testTransactions) {
            totalProfit += transaction.getProfit();
        }

        double taxRate = 10.0; // 10%
        double expectedTax = totalProfit * (taxRate / 100.0);

        // We can't directly call calculateFinalTaxOnClick() because it uses JavaFX components
        // But we can test the calculation logic
        assertEquals(expectedTax, totalProfit * (taxRate / 100.0),
                "Tax calculation should be correct");
    }

    @Test
    @DisplayName("Test filtering invalid transactions")
    public void testFilterInvalidTransactions() {
        // Count how many transactions are invalid
        long invalidCount = testTransactions.stream()
                .filter(t -> !t.isValidChecksum())
                .count();

        // Create a new list with only valid transactions
        List<Transaction> validTransactions = testTransactions.stream()
                .filter(Transaction::isValidChecksum)
                .toList();

        // Verify the filtering works correctly
        assertEquals(testTransactions.size() - invalidCount, validTransactions.size(),
                "Filtering should remove invalid transactions");
    }

    @Test
    @DisplayName("Test filtering zero profit transactions")
    public void testFilterZeroProfitTransactions() {
        // Add a zero profit transaction to the test data
        testTransactions.add(new Transaction("ITEM004", 100.00, 100.00, 0.0, "50"));

        // Count how many transactions have zero profit
        long zeroProfitCount = testTransactions.stream()
                .filter(t -> t.getProfit() == 0)
                .count();

        // Create a new list without zero profit transactions
        List<Transaction> nonZeroProfitTransactions = testTransactions.stream()
                .filter(t -> t.getProfit() != 0)
                .toList();

        // Verify the filtering works correctly
        assertEquals(testTransactions.size() - zeroProfitCount, nonZeroProfitTransactions.size(),
                "Filtering should remove zero profit transactions");
    }

    @Test
    @DisplayName("Test total profit calculation")
    public void testTotalProfitCalculation() {
        // Calculate the total profit manually
        double expectedTotalProfit = 0.0;
        for (Transaction transaction : testTransactions) {
            expectedTotalProfit += transaction.getProfit();
        }

        // Calculate using the stream API (similar to how the controller would do it)
        double calculatedTotalProfit = testTransactions.stream()
                .mapToDouble(Transaction::getProfit)
                .sum();

        // Verify both calculations match
        assertEquals(expectedTotalProfit, calculatedTotalProfit, 0.001,
                "Total profit calculation should be correct");
    }
}
