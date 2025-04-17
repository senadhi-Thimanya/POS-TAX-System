#This is supposed to create each item object.

class Item:
    def __init__(self, itemcode, internalprice, discount, saleprice, quantity):
        self.itemcode = itemcode
        self.internalPrice = internalprice
        self.discount = discount
        self.salePrice = saleprice
        self.quantity = quantity
