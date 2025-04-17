from POS.Package.ItemManager import ItemManager

itemManager = ItemManager()


def printMenu():
    menuText = """\nWelcome to the Cupcake POS System\n  
1. Add Item to Basket  
2. View Basket  
3. Delete Item from Basket  
4. Update Item in Basket  
5. Generate Bill  
6. Search Bill  
7. Generate Tax Transaction File  
8. Exit \n"""
    print(menuText)

def processMenuOption(option):
    match option:
        case 1:
            addItem()
        case 2:
            viewBasket()
        case 3:
            deleteItem()
        case 4:
            updateItem()
        case 5:
            generateBill()
        case 6:
            searchBill()
        case 7:
            generateTaxFile()
        case 8:
            print("\nGoodbye!")
            return False
        case _:
            print("\nInvalid Option")
    return True

def getInput():
    try:
        return int(input("\nEnter an option : "))
    except ValueError:
        print("\nPlease enter a valid number\n")
        return -1


def validateItemCode(itemcode):
    if not itemcode:
        print("\nItem code cannot be empty")
        return False

    # Check if itemcode matches the accepted formats:
    # Lemon_01, LE_cup01, Cake124, etc.
    import re
    pattern = r'^[A-Za-z]+(_[A-Za-z0-9]+)?[0-9]*$'

    if not re.match(pattern, itemcode):
        print("\nInvalid item code format. Examples of valid formats: Lemon_01, LE_cup01, Cake124")
        return False

    return True


def validatePrice(price, label):
    if price < 0:
        print(f"\n{label} cannot be negative")
        return False
    return True


def validateDiscount(discount, internalprice):
    if discount < 0:
        print("\nDiscount cannot be negative")
        return False
    if discount > internalprice:
        print("\nDiscount cannot be greater than internal price")
        return False
    return True


def validateQuantity(quantity):
    if quantity <= 0:
        print("\nQuantity must be positive")
        return False
    return True


def getItemDetails():
    while True:
        itemcode = input("\n\tEnter Item Code \t\t: ")
        if not validateItemCode(itemcode):
            continue

        try:
            internalprice = int(input("\tEnter Internal Price \t: "))
            if not validatePrice(internalprice, "Internal price"):
                continue

            discount = int(input("\tEnter Discount \t\t: "))
            if not validateDiscount(discount, internalprice):
                continue

            # Automatically calculate sale price
            saleprice = internalprice - discount
            print(f"\tSale Price calculated \t: ${saleprice}")

            quantity = int(input("\tEnter Quantity \t\t: "))
            if not validateQuantity(quantity):
                continue

            return itemcode, internalprice, discount, saleprice, quantity

        except ValueError:
            print("\nPlease enter valid numeric values for prices and quantity")


def addItem():
    while True:
        details = getItemDetails()
        if details:
            itemcode, internalprice, discount, saleprice, quantity = details
            itemManager.addItem(itemcode, internalprice, discount, saleprice, quantity)
            print("\nItem Added Successfully")
            viewBasket()

        while True:
            op = input("\nGo again? (Y/N) : ").upper()
            if op == "Y":
                break
            elif op == "N":
                return
            else:
                print("\nInvalid input. Please enter Y or N")


def viewBasket():
    itemManager.viewCart()


def deleteItem():
    while True:
        viewBasket()  # Show the basket first so user can see line numbers

        try:
            line_number = input("Enter Line Number to delete: ")
            if itemManager.deleteItem(line_number):  # Using the updated method that accepts line number
                print("\nItem Deleted Successfully")
                viewBasket()  # Show updated basket
            # The error message is handled inside the ItemManager.deleteItem method

        except Exception as e:
            print(f"\nError: {e}")
            continue

        while True:
            op = input("\nGo again? (Y/N) : ").upper()
            if op == "Y":
                break
            elif op == "N":
                return
            else:
                print("\nInvalid input. Please enter Y or N")


def updateItem():
    while True:
        # Show the basket first so user can see line numbers
        viewBasket()

        if not itemManager.cart:
            print("\nBasket is empty. Nothing to update.")
            return

        try:
            line_number = input("\nEnter Line Number to update: ")
            if itemManager.updateItem(line_number):
                print("\nItem Updated Successfully")
                viewBasket()  # Show updated basket
            # Error messages are handled inside the ItemManager.updateItem method

        except Exception as e:
            print(f"\nError: {e}")
            continue

        while True:
            op = input("\nGo again? (Y/N) : ").upper()
            if op == "Y":
                break
            elif op == "N":
                return
            else:
                print("\nInvalid input. Please enter Y or N")



def generateBill():
    print("Generate Bill functionality will be implemented here")
    # Implementation for generating bill


def searchBill():
    print("Search Bill functionality will be implemented here")
    # Implementation for searching bill


def generateTaxFile():
    print("Generate Tax Transaction File functionality will be implemented here")
    # Implementation for generating tax transaction file

def main():
    running = True
    while running:
        printMenu()
        option = getInput()
        if option != -1:  # Only process valid inputs
            running = processMenuOption(option)
        else:
            main()


if __name__ == "__main__":
    main()