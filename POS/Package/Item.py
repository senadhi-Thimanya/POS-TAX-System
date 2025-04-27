# This module defines the Item class which represents a product in the POS system.
# Each item has properties like item code, cost, sale price, discount, etc.

class Item:
    def __init__(self, itemcode, cost, saleprice, discount, discountedprice, quantity):
        """
        Initialize a new Item object with the specified attributes.

        Args:
            itemcode (str): Unique identifier for the item (e.g., Lemon_01, LE_cup01)
            cost (float): Internal cost price of the item
            saleprice (float): Original selling price before discount
            discount (float): Discount percentage (0-100)
            discountedprice (float): Final price after applying discount
            quantity (int): Number of items
        """
        self.itemcode = itemcode
        self.cost = cost
        self.salePrice = saleprice
        self.discount = discount
        self.discountedPrice = discountedprice
        self.quantity = quantity
