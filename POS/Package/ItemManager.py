# This is supposed to handle the add items,
# delete items, update items, view basket,
from POS.Package.Item import Item


# Add items will add them to a list
class ItemManager:
    def __init__(self):
        self.cart = []

    def addItem(self, itemcode, cost, saleprice, discount, discountedprice, quantity):
        item = Item(itemcode, cost, saleprice, discount, discountedprice, quantity)
        self.cart.append(item)

    def deleteItem(self, line_number):  # Changed to accept line number
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
        try:
            # Convert line_number to integer and adjust for 0-based indexing
            index = int(line_number) - 1
            if 0 <= index < len(self.cart):
                # Get the item to update
                item = self.cart[index]

                print(f"\nUpdating item: {item.itemcode}")
                print(f"Current values: Cost: ${item.cost:.2f} \nSale Price: ${item.salePrice:.2f} "
                      f"\nDiscount: {item.discount}% \nDiscounted Price: ${item.discountedPrice:.2f} \nQuantity: {item.quantity}")

                # Get new values
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

                    # Update the item
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
        for item in self.cart:
            if item.itemcode == itemcode:
                return True
        return False

    def viewCart(self):
        if not self.cart:
            print("Cart is empty")
            return

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
        Get all items in the cart in a dictionary format
        (Helper method for bill generation)

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
        Clear all items from the cart
        (Helper method for after bill generation)
        """
        self.cart = []