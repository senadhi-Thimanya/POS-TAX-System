# This is supposed to handle the add items,
# delete items, update items, view basket,
from POS.Package.Item import Item


# Add items will add them to a list
class ItemManager:
    def __init__(self):
        self.cart = []

    def addItem(self, itemcode, internalprice, discount, saleprice, quantity):
        item = Item(itemcode, internalprice, discount, saleprice, quantity)
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
                print(
                    f"Current values: Internal Price: ${item.internalPrice} \nDiscount: {item.discount} \nQuantity: {item.quantity}")

                # Get new values
                try:
                    # Get new internal price
                    new_internal_price = int(
                        input("\n\tEnter new Internal Price (or press Enter to keep current): ") or item.internalPrice)
                    if new_internal_price < 0:
                        print("\nInternal price cannot be negative")
                        return False

                    # Get new discount
                    new_discount = int(
                        input("\tEnter new Discount (or press Enter to keep current): ") or item.discount)
                    if new_discount < 0:
                        print("\nDiscount cannot be negative")
                        return False
                    if new_discount > new_internal_price:
                        print("\nDiscount cannot be greater than internal price")
                        return False

                    # Calculate new sale price
                    new_sale_price = new_internal_price - new_discount
                    print(f"\tNew Sale Price calculated: ${new_sale_price}")

                    # Get new quantity
                    new_quantity = int(
                        input("\tEnter new Quantity (or press Enter to keep current): ") or item.quantity)
                    if new_quantity <= 0:
                        print("\nQuantity must be positive")
                        return False

                    # Update the item
                    self.cart[index] = Item(item.itemcode, new_internal_price, new_discount, new_sale_price,
                                            new_quantity)
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
            if item.itemcode == itemcode:  # Assuming itemcode is the attribute, not itemCode
                return True
        return False  # Fixed indentation - was returning False after first item check

    def viewCart(self):
        if not self.cart:
            print("Cart is empty")
            return

        print("\nCart Items:")
        print("─" * 80)
        print(f"{'No.':<4} {'Item Code':<15} {'Quantity':<10} {'Discount':<10} {'Sale Price':<12} {'Line Total'}")
        print("─" * 80)

        for index, item in enumerate(self.cart, 1):
            # Using consistent attribute names based on Item class
            # Assuming the attributes are itemcode, quantity, discount, salePrice
            line_total = item.salePrice * item.quantity
            print(
                f"{index:<4} {item.itemcode:<15} {item.quantity:<10} {item.discount}%{' ':<6} ${item.salePrice:<11} ${line_total}")
        print("─" * 80)
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
                'internalprice': item.internalPrice,
                'discount': item.discount,
                'saleprice': item.salePrice,
                'quantity': item.quantity
            })
        return cart_items

    def clearCart(self):
        """
        Clear all items from the cart
        (Helper method for after bill generation)
        """
        self.cart = []