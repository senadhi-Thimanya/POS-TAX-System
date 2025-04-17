# This is supposed to generate the bill when the items are given.
class Bill:
    def __init__(self, bill_id, items):
        self.bill_id = bill_id
        self.items = items
        self.grand_total = self.calculateGrandTotal()

    def calculateGrandTotal(self):
        total = 0
        for item in self.items:
            line_total = item['discountedprice'] * item['quantity']
            total += line_total
        return total