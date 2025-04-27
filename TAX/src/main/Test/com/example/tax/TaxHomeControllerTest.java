package com.example.tax.controllers;

import com.example.tax.models.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Note: Testing JavaFX controllers requires special setup
// This is a simplified example - in a real project, you'd use TestFX or mock the JavaFX environment
public class TaxHomeControllerTest {

    private TaxHomeController controller;

    @BeforeEach
    public void setUp() {
        controller = new TaxHomeController();
        // In a real test, you would need to initialize JavaFX and load the FXML
    }

    @Test
    public void testImportTransactionFile() throws Exception {
        // Create a temporary CSV file for testing
        Path tempFile = Files.createTempFile("test-transactions", ".csv");
        List<String> lines = Arrays.asList(
                "ItemCode,Cost,SalePrice,Discount,DiscountedPrice,Checksum",
                "ITEM123,100.00,150.00,10.0,135.00,42"
        );
        Files.write(tempFile, lines);

        // Call the method to test
        // Note: This won't work as is because the method is private
        // In a real test, you'd need to make the method accessible or test through a public method
        // controller.importTransactionFile(tempFile.toFile());

        // Assert the expected outcome
        // This would require accessing the private transactions list
        // In a real test, you'd need to make this accessible or test through a public method

        // Clean up
        Files.deleteIfExists(tempFile);
    }
}
