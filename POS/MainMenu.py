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
    if option == 1:
        itemcode = input("Enter Item Code : ")
        internalprice = int(input("Enter Internal Price : "))
        discount = int(input("Enter Discount : "))
        saleprice = int(input("Enter Sale Price : "))
        quantity = int(input("Enter Quantity : "))
        itemManager.addItem(itemcode, internalprice, discount, saleprice, quantity)

printMenu()
goToMenu(getInput())