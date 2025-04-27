package com.example.tax.models;

import com.example.tax.utils.ChecksumUtil;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

/**
 * Model class representing a transaction record imported from a Tax Transaction File.
 * Contains properties for item code, cost, sale price, discount, checksum, discounted price, and profit.
 * Provides methods to validate checksums and calculate derived values.
 */
public class Transaction {
    private final SimpleStringProperty itemCode;
    private final SimpleDoubleProperty cost;
    private final SimpleDoubleProperty salePrice;
    private final SimpleDoubleProperty discount;
    private final SimpleStringProperty checksum;
    private final SimpleDoubleProperty discountedPrice;
    private final SimpleDoubleProperty profit;

    /**
     * Constructs a new Transaction with the specified attributes.
     *
     * @param itemCode The unique identifier for the item
     * @param cost The internal cost price
     * @param salePrice The original selling price
     * @param discount The discount percentage
     * @param checksum The checksum value from the transaction file
     */
    public Transaction(String itemCode, double cost, double salePrice, double discount, String checksum) {
        this.itemCode = new SimpleStringProperty(itemCode);
        this.cost = new SimpleDoubleProperty(cost);
        this.salePrice = new SimpleDoubleProperty(salePrice);
        this.discount = new SimpleDoubleProperty(discount);
        this.checksum = new SimpleStringProperty(checksum);
        this.discountedPrice = new SimpleDoubleProperty(0.0);
        this.profit = new SimpleDoubleProperty(0.0);
        calculateDiscountedPrice();
        calculateProfit();
    }

    /**
     * Validates the transaction by checking:
     * 1. Item code format (no special characters except underscore)
     * 2. No negative values for cost or sale price
     * 3. Checksum matches the calculated value
     *
     * @return true if the transaction is valid, false otherwise
     */
    public boolean isValidChecksum() {
        // Check for special characters in item code (except underscore)
        String itemCode = getItemCode();
        if (itemCode.matches(".*[!@#$%^&*()+=\\[\\]{}|;:'\",.<>/?`~-].*")) {
            return false;
        }

        // Check for negative values
        if (getCost() < 0 || getSalePrice() < 0) {
            return false;
        }

        // Calculate discounted price first to ensure it's up to date
        calculateDiscountedPrice();

        // Use the utility class to format transaction line and calculate checksum
        String transactionLine = ChecksumUtil.formatTransactionLine(
                getItemCode(), getCost(), getSalePrice(), getDiscount(), getDiscountedPrice());

        int calculatedChecksum = ChecksumUtil.calculateChecksum(transactionLine);

        // Compare with provided checksum
        return String.valueOf(calculatedChecksum).equals(getChecksum());
    }

    /**
     * Calculates the discounted price based on sale price and discount percentage.
     * Updates the discountedPrice property.
     */
    private void calculateDiscountedPrice() {
        double discounted = getSalePrice() - (getSalePrice() * (getDiscount() / 100));
        setDiscountedPrice(discounted);
    }

    /**
     * Calculates the profit as the difference between discounted price and cost.
     * Updates the profit property.
     */
    private void calculateProfit() {
        double calculatedProfit = getDiscountedPrice() - getCost();
        setProfit(calculatedProfit);
    }

    // Getters and setters for JavaFX properties
    public String getItemCode() { return itemCode.get(); }
    public void setItemCode(String value) { itemCode.set(value); }
    public StringProperty itemCodeProperty() { return itemCode; }

    public double getCost() { return cost.get(); }
    public void setCost(double value) { cost.set(value); }
    public DoubleProperty costProperty() { return cost; }

    public double getSalePrice() { return salePrice.get(); }
    public void setSalePrice(double value) { salePrice.set(value); }
    public DoubleProperty salePriceProperty() { return salePrice; }

    public double getDiscount() { return discount.get(); }
    public void setDiscount(double value) { discount.set(value); }
    public DoubleProperty discountProperty() { return discount; }

    public String getChecksum() { return checksum.get(); }
    public void setChecksum(String value) { checksum.set(value); }
    public StringProperty checksumProperty() { return checksum; }

    public double getDiscountedPrice() { return discountedPrice.get(); }
    public void setDiscountedPrice(double value) { discountedPrice.set(value); }
    public DoubleProperty discountedPriceProperty() { return discountedPrice; }

    public double getProfit() { return profit.get(); }
    public void setProfit(double value) { profit.set(value); }
    public DoubleProperty profitProperty() { return profit; }
}
