# This module defines the Bill class which represents a customer bill.
# It contains bill ID, items purchased, and calculates the grand total.

class Bill:
    def __init__(self, bill_id, items):
        """
        Initialize a new Bill object with the specified attributes.

        Args:
            bill_id (int): Unique identifier for the bill (starting from 1001)
            items (list): List of dictionaries containing item details
        """
        self.bill_id = bill_id
        self.items = items
        self.grand_total = self.calculateGrandTotal()

    def calculateGrandTotal(self):
        """
        Calculate the grand total of all items in the bill.

        Returns:
            float: Sum of all line totals (discounted price × quantity)
        """
        total = 0
        for item in self.items:
            # For each item, calculate line total and add to grand total
            line_total = item['discountedprice'] * item['quantity']
            total += line_total
        return total
