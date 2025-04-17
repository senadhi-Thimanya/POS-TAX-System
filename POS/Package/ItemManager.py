#This is supposed to handle the add items,
#delete items, update items, view basket,
from POS.Package.Item import Item


#Add items will add them to a list
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

    def updateItem(self, itemcodeupd, itemcode, internalprice, discount, saleprice, quantity):
        for index, item in enumerate(self.cart):  # Fixed enumerate usage
            if item.itemcode == itemcodeupd:  # Assuming itemcode is the attribute, not itemCode
                self.cart[index] = Item(itemcode, internalprice, discount, saleprice, quantity)
                return True
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
            print(f"{index:<4} {item.itemcode:<15} {item.quantity:<10} {item.discount}%{' ':<6} ${item.salePrice:<11} ${line_total}")
        print("─" * 80)
        print("\n")