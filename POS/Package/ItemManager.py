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

    def deleteItem(self,itemcode): #The parameter should  be the line number
        for item in self.cart:
            if item.itemcode == itemcode:
                self.cart.remove(item)

    def updateItem(self, itemcodeupd, itemcode, internalprice, discount, saleprice, quantity):
        for index, item in self.cart:
            if item.itemcode == itemcodeupd:
                self.cart[index] = Item(itemcode, internalprice, discount, saleprice, quantity)

    def searchItem(self, itemcode):
        for item in self.cart:
            if item.itemcode == itemcode:
                return True
            return False

    def viewCart(self):
        for item in self.cart:
            print(item.itemCode)
            print(item.salePrice)
            print(item.quantity)

