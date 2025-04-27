package com.example.tax.utils;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ChecksumUtilTest {

    @Test
    public void testCalculateChecksum() {
        // Test case with a simple transaction line
        String transactionLine = "ITEM123,100.00,150.00,10.0,135.00";
        int checksum = ChecksumUtil.calculateChecksum(transactionLine);

        // We need to manually calculate the expected result based on the algorithm
        int expectedCapitalCount = 4; // I, T, E, M
        int expectedSimpleCount = 0;
        int expectedNumberCount = 25; // Correct count of numeric characters including decimal points
        int expectedUnderscoreCount = 0;
        int expectedDigitSum = 23; // Correct sum of digit values

        int expectedChecksum = expectedCapitalCount + expectedSimpleCount +
                expectedNumberCount + expectedUnderscoreCount +
                expectedDigitSum; // Removed the duplicate expectedNumberCount

        assertEquals(expectedChecksum, checksum, "Checksum calculation is incorrect");
    }


    @Test
    public void testFormatTransactionLine() {
        String formattedLine = ChecksumUtil.formatTransactionLine(
                "ITEM123", 100.00, 150.00, 10.0, 135.00);

        assertEquals("ITEM123,100.00,150.00,10.0,135.00", formattedLine,
                "Transaction line formatting is incorrect");
    }
}
