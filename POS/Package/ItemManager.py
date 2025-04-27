# This module handles basket operations like adding, deleting, and updating items.
# It maintains the shopping cart and provides methods to manipulate it.

from POS.Package.Item import Item


class ItemManager:
    """
    Manages the shopping cart operations in the POS system.
    Provides methods to add, delete, update, and view items in the basket.
    """

    def __init__(self):
        """Initialize an empty shopping cart."""
        self.cart = []  # List to store items in the basket

    def addItem(self, itemcode, cost, saleprice, discount, discountedprice, quantity):
        """
        Add a new item to the shopping cart.

        Args:
            itemcode (str): Unique identifier for the item
            cost (float): Internal cost price
            saleprice (float): Original selling price
            discount (float): Discount percentage
            discountedprice (float): Price after discount
            quantity (int): Number of items
        """
        # Create a new Item object and add it to the cart
        item = Item(itemcode, cost, saleprice, discount, discountedprice, quantity)
        self.cart.append(item)

    def deleteItem(self, line_number):
        """
        Delete an item from the cart by line number.

        Args:
            line_number (str): The line number of the item to delete (1-based)

        Returns:
            bool: True if deletion was successful, False otherwise
        """
        try:
            # Convert line_number to integer and adjust for 0-based indexing
            index = int(line_number) - 1
            if 0 <= index < len(self.cart):
                # Remove item at specified index
                self.cart.pop(index)
                return True
            else:
                print("\nInvalid line number. Please try again.")
                return False
        except ValueError:
            print("\nPlease enter a valid number")
            return False

    def updateItem(self, line_number):
        """
        Update an item in the cart by line number.

        Args:
            line_number (str): The line number of the item to update (1-based)

        Returns:
            bool: True if update was successful, False otherwise
        """
        try:
            # Convert line_number to integer and adjust for 0-based indexing
            index = int(line_number) - 1
            if 0 <= index < len(self.cart):
                # Get the item to update
                item = self.cart[index]

                # Display current values
                print(f"\nUpdating item: {item.itemcode}")
                print(f"Current values: Cost: ${item.cost:.2f} \nSale Price: ${item.salePrice:.2f} "
                      f"\nDiscount: {item.discount}% \nDiscounted Price: ${item.discountedPrice:.2f} \nQuantity: {item.quantity}")

                # Get new values with validation
                try:
                    # Get new cost
                    new_cost = float(
                        input("\n\tEnter new Cost (or press Enter to keep current): ") or item.cost)
                    if new_cost < 0:
                        print("\nCost cannot be negative")
                        return False

                    # Get new sale price
                    new_saleprice = float(
                        input("\tEnter new Sale Price (or press Enter to keep current): ") or item.salePrice)
                    if new_saleprice < 0:
                        print("\nSale price cannot be negative")
                        return False

                    # Warn if sale price is less than cost
                    if new_saleprice < new_cost:
                        print("\tWarning: Sale price is less than cost")
                        confirm = input("\tContinue? (Y/N): ").upper()
                        if confirm != "Y":
                            return False

                    # Get new discount
                    new_discount = float(
                        input("\tEnter new Discount percentage (or press Enter to keep current): ") or item.discount)
                    if new_discount < 0:
                        print("\nDiscount percentage cannot be negative")
                        return False
                    if new_discount > 100:
                        print("\nDiscount percentage cannot be greater than 100%")
                        return False

                    # Calculate new discounted price
                    discount_amount = (new_discount / 100) * new_saleprice
                    new_discounted_price = new_saleprice - discount_amount
                    print(f"\tNew Discounted Price calculated: ${new_discounted_price:.2f}")

                    # Get new quantity
                    new_quantity = int(
                        input("\tEnter new Quantity (or press Enter to keep current): ") or item.quantity)
                    if new_quantity <= 0:
                        print("\nQuantity must be positive")
                        return False

                    # Update the item with new values
                    self.cart[index] = Item(item.itemcode, new_cost, new_saleprice, new_discount,
                                            new_discounted_price, new_quantity)

                    return True
                except ValueError:
                    print("\nPlease enter valid numeric values")
                    return False
            else:
                print("\nInvalid line number. Please try again.")
                return False
        except ValueError:
            print("\nPlease enter a valid number")
            return False

    def searchItem(self, itemcode):
        """
        Search for an item in the cart by item code.

        Args:
            itemcode (str): The item code to search for

        Returns:
            bool: True if item is found, False otherwise
        """
        for item in self.cart:
            if item.itemcode == itemcode:
                return True
        return False

    def viewCart(self):
        """
        Display all items in the shopping cart with formatting.

        """
        if not self.cart:
            print("Cart is empty")
            return

        # Display cart items with formatting
        print("\nCart Items:")
        print("─" * 100)
        print(
            f"{'No.':<4} {'Item Code':<15} {'Cost':<10} {'Sale Price':<12} {'Discount':<10} {'Disc. Price':<12} {'Qty':<5} {'Line Total'}")
        print("─" * 100)

        for index, item in enumerate(self.cart, 1):
            line_total = item.discountedPrice * item.quantity
            print(
                f"{index:<4} {item.itemcode:<15} ${item.cost:<9.2f} ${item.salePrice:<11.2f} {item.discount}%{' ':<6} "
                f"${item.discountedPrice:<11.2f} {item.quantity:<5} ${line_total:.2f}")

        print("─" * 100)
        print("\n")

    def getCart(self):
        """
        Get all items in the cart in a dictionary format.
        Helper method for bill generation.

        Returns:
            list: Cart items as dictionaries
        """
        cart_items = []
        for item in self.cart:
            cart_items.append({
                'itemcode': item.itemcode,
                'cost': item.cost,
                'saleprice': item.salePrice,
                'discount': item.discount,
                'discountedprice': item.discountedPrice,
                'quantity': item.quantity
            })
        return cart_items

    def clearCart(self):
        """
        Clear all items from the cart.
        Helper method for after bill generation.
        """
        self.cart = []
