#This is supposed to create each item object.

class Item:
    def __init__(self, itemcode, cost, saleprice, discount, discountedprice, quantity):
        self.itemcode = itemcode
        self.cost = cost
        self.salePrice = saleprice
        self.discount = discount
        self.discountedPrice = discountedprice
        self.quantity = quantity