from POS.Package.ItemManager import ItemManager

itemManager = ItemManager()

def printMenu():
    menuText = """Welcome to the Cupcake POS System  
1. Add Item to Basket  
2. View Basket  
3. Delete Item from Basket  
4. Update Item in Basket  
5. Generate Bill  
6. Search Bill  
7. Generate Tax Transaction File  
8. Exit """
    print(menuText)

def getInput():
    #Add error handling here
    return int(input("Enter an option : "))

def goToMenu(option):
    while True:
        if option == 1:
            while True:
                itemcode = input("Enter Item Code : ")
                internalprice = int(input("Enter Internal Price : "))
                discount = int(input("Enter Discount : "))
                saleprice = int(input("Enter Sale Price : "))
                quantity = int(input("Enter Quantity : "))
                itemManager.addItem(itemcode, internalprice, discount, saleprice, quantity)
                print("Item Added Successfully")
                op = int(input("Go again? (Y/N) : "))
                if op == "Y":
                    continue
                else:
                    break

        elif option == 2:
            itemManager.viewCart()
            break

        elif option == 3:
            while True:
                itemcodedel = input("Enter Item Code to delete: ")
                itemManager.deleteItem(itemcodedel)
                print("Item Deleted Successfully")
                op = int(input("Go again? (Y/N) : "))
                if op == "Y":
                    continue
                else:
                    break

        elif option == 4:
            while True:
                itemcodeupd = input("Enter Item Code to update: ")
                if itemManager.searchItem(itemcodeupd):
                    itemcode = input("Enter Item Code : ")
                    internalprice = int(input("Enter Internal Price : "))
                    discount = int(input("Enter Discount : "))
                    saleprice = int(input("Enter Sale Price : "))
                    quantity = int(input("Enter Quantity : "))
                    itemManager.updateItem(itemcodeupd, itemcode, internalprice, discount, saleprice, quantity)
                    print("Item Updated Successfully")
                else:
                    print("Item Not Found")
                op = int(input("Go again? (Y/N) : "))
                if op == "Y":
                    continue
                else:
                    break

        elif option == 5:
            pass

        elif option == 6:
            pass

        elif option == 7:
            pass

        elif option == 8:
            print("Goodbye!")
            return

        else:
            print("Invalid Option")
            continue


while True:
    printMenu()
    goToMenu(getInput())