from POS.Package.ItemManager import ItemManager
from POS.Package.BillManager import BillManager

itemManager = ItemManager()
billManager = BillManager()


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


def validateDiscount(discount):
    if discount < 0:
        print("\nDiscount percentage cannot be negative")
        return False
    if discount > 100:
        print("\nDiscount percentage cannot be greater than 100%")
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
            cost = float(input("\tEnter Cost \t\t: "))
            if not validatePrice(cost, "Cost"):
                continue

            saleprice = float(input("\tEnter Sale Price \t: "))
            if not validatePrice(saleprice, "Sale price"):
                continue

            if saleprice < cost:
                print("\tWarning: Sale price is less than cost")
                confirm = input("\tContinue? (Y/N): ").upper()
                if confirm != "Y":
                    continue

            discount_percent = float(input("\tEnter Discount Percentage \t: "))
            if not validateDiscount(discount_percent):
                continue

            # Calculate discounted price
            discount_amount = (discount_percent / 100) * saleprice
            discountedprice = saleprice - discount_amount
            print(f"\tDiscounted Price calculated \t: ${discountedprice:.2f}")

            quantity = int(input("\tEnter Quantity \t\t: "))
            if not validateQuantity(quantity):
                continue

            return itemcode, cost, saleprice, discount_percent, discountedprice, quantity

        except ValueError:
            print("\nPlease enter valid numeric values for prices and quantity")


def addItem():
    while True:
        details = getItemDetails()
        if details:
            itemcode, cost, saleprice, discount, discountedprice, quantity = details
            itemManager.addItem(itemcode, cost, saleprice, discount, discountedprice, quantity)
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
    # Check if basket is empty
    cart_items = itemManager.getCart()
    if not cart_items:
        print("\nCannot generate bill: Basket is empty")
        return

    # Create the bill
    bill = billManager.create_bill(cart_items)

    if bill:
        print("\nBill Generated Successfully")
        billManager.view_bill(bill)

        # Clear the basket
        itemManager.clearCart()
        print("\nBasket has been cleared")


def searchBill():
    bill_id = input("\nEnter Bill ID to search: ")

    try:
        # Try to convert to integer
        bill_id = int(bill_id)

        # Search for the bill
        bill = billManager.search_bill_by_id(bill_id)

        # Display the bill if found
        if bill:
            billManager.view_bill(bill)
    except ValueError:
        print("\nPlease enter a valid bill number")


def generateTaxFile():
            """
            Generate separate tax transaction files in CSV format with checksums for each bill
            """
            # Check if there are any bills to process
            if not billManager.bills:
                print("\nNo bills available to generate tax transaction files")
                return

            try:
                files_created = []

                # Process each bill separately
                for bill in billManager.bills:
                    bill_id = bill['bill_id']
                    file_name = f"TaxFiles/ttf_{bill_id}.csv"

                    with open(file_name, "w") as file:
                        # Write header
                        file.write("ItemCode,Cost,SalePrice,Discount,DiscountedPrice,Checksum\n")

                        # Process items for this bill
                        for item in bill['items']:
                            # Create transaction line
                            transaction_line = (f"{item['itemcode']},{item['cost']:.2f},"
                                             f"{item['saleprice']:.2f},{item['discount']},"
                                             f"{item['discountedprice']:.2f}")

                            # Calculate checksum
                            checksum = calculateChecksum(transaction_line)

                            # Write line with checksum
                            file.write(f"{transaction_line},{checksum}\n")

                    files_created.append(file_name)

                # Report success
                print("\nTax Transaction Files generated successfully:")
                for file in files_created:
                    print(f"- '{file}'")

            except Exception as e:
                print(f"\nError generating tax transaction files: {e}")


def calculateChecksum(transaction_line):
    """
    Calculate checksum for a transaction line based on these rules:
    - Count all capital letters
    - Count all simple (lowercase) letters
    - Count all numbers and decimals (periods)
    - Sum the above three values

    Parameters:
    transaction_line (str): The transaction line string

    Returns:
    int: The calculated checksum
    """
    capital_count = 0
    simple_count = 0
    number_count = 0

    for char in transaction_line:
        if char.isupper():
            capital_count += 1
        elif char.islower():
            simple_count += 1
        elif char.isdigit() or char == '.':
            number_count += 1

    # Calculate checksum
    checksum = capital_count + simple_count + number_count

    return checksum


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