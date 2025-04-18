package com.example.tax.models;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.DoubleProperty;

public class Transaction {
    private final SimpleStringProperty itemCode;
    private final SimpleDoubleProperty cost;
    private final SimpleDoubleProperty salePrice;
    private final SimpleDoubleProperty discount;
    private final SimpleStringProperty checksum;
    private final SimpleDoubleProperty discountedPrice;
    private final SimpleDoubleProperty profit;

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

        // Create transaction line string including the discounted price but excluding the checksum
        String transactionLine = String.format("%s,%.2f,%.2f,%.1f,%.2f",
                getItemCode(), getCost(), getSalePrice(), getDiscount(), getDiscountedPrice());

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

        // Compare with provided checksum
        return String.valueOf(calculatedChecksum).equals(getChecksum());
    }

    private void calculateDiscountedPrice() {
        double discounted = getSalePrice() - (getSalePrice() * (getDiscount() / 100));
        setDiscountedPrice(discounted);
    }

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