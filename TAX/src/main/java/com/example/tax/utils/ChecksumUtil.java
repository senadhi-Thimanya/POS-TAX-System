package com.example.tax.utils;

/**
 * Utility class for calculating checksums and formatting transaction lines.
 * This class provides static methods to ensure consistent checksum calculation
 * and transaction line formatting across the application.
 */
public class ChecksumUtil {

    /**
     * Calculates the checksum for a transaction line using the enhanced algorithm.
     * The algorithm counts capital letters, lowercase letters, digits, underscores,
     * and calculates the sum of digit values for improved error detection.
     *
     * @param transactionLine The transaction line string to calculate checksum for
     * @return The calculated checksum value
     */
    public static int calculateChecksum(String transactionLine) {
        int capitalCount = 0;
        int simpleCount = 0;
        int numberCount = 0;
        int underscoreCount = 0;
        int digitSum = 0;

        for (char c : transactionLine.toCharArray()) {
            if (Character.isUpperCase(c)) {
                capitalCount++;
            } else if (Character.isLowerCase(c)) {
                simpleCount++;
            } else if (Character.isDigit(c)) {
                numberCount++;
                digitSum += Character.getNumericValue(c);
            } else if (c == '.') {
                numberCount++;
            } else if (c == '_') {
                underscoreCount++;
            }
        }

        return capitalCount + simpleCount + numberCount + underscoreCount + digitSum + numberCount;
    }

    /**
     * Formats a transaction line string with consistent formatting.
     * Ensures that all transaction lines follow the same format for reliable checksum calculation.
     *
     * @param itemCode The item code
     * @param cost The cost price
     * @param salePrice The sale price
     * @param discount The discount percentage
     * @param discountedPrice The discounted price
     * @return Formatted transaction line string
     */
    public static String formatTransactionLine(String itemCode, double cost, double salePrice,
                                               double discount, double discountedPrice) {
        return String.format("%s,%.2f,%.2f,%.1f,%.2f",
                itemCode, cost, salePrice, discount, discountedPrice);
    }
}
