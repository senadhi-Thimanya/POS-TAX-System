from POS.Package.Bill import Bill
#nijfeguytgut

class BillManager:
    def __init__(self):
        self.bills = []
        self.next_bill_id = 1001  # Starting bill ID

    def create_bill(self, items):
        """
        Create a new bill from items
        """
        if not items:
            print("Cannot create bill: No items in basket")
            return None

        # Create a new bill with the next available ID
        bill = Bill(self.next_bill_id, items)

        # Add to memory
        bill_dict = {
            'bill_id': bill.bill_id,
            'items': bill.items,
            'grand_total': bill.grand_total
        }
        self.bills.append(bill_dict)

        # Increment the bill ID for next time
        self.next_bill_id += 1

        return bill

    def view_bill(self, bill):
        """
        Display a bill with proper formatting
        """
        # Convert dict to Bill object if needed
        if isinstance(bill, dict):
            bill = Bill(bill['bill_id'], bill['items'])

        # Display the bill
        border = "=" * 80
        divider = "-" * 80

        print(f"\n{border}")
        print(f"{'THE CAKE SHOP':^75}")
        print(f"{border}")
        print(f"BILL #{bill.bill_id}")
        print(f"{divider}")

        # Print header
        print(f"{'No.':<4} {'Item Code':<15} {'Quantity':<10} {'Sale Price':<12} {'Discount':<10} {'Disc. Price':<12} {'Line Total':<12}")
        print(f"{divider}")

        # Print items
        for i, item in enumerate(bill.items, 1):
            line_total = item['discountedprice'] * item['quantity']
            print(
                f"{i:<4} {item['itemcode']:<15} {item['quantity']:<10} ${item['saleprice']:<11.2f} "
                f"{item['discount']}%{' ':<6} ${item['discountedprice']:<11.2f} ${line_total:<11.2f}")

        # Print grand total
        print(f"{divider}")
        print(f"{'Grand Total:':<68} ${bill.grand_total:<11.2f}")
        print(f"{border}")

    def search_bill_by_id(self, bill_id):
        """
        Search for a bill by ID
        """
        try:
            bill_id = int(bill_id)
            for bill in self.bills:
                if bill['bill_id'] == bill_id:
                    return bill
            print(f"Bill #{bill_id} not found")
            return None
        except ValueError:
            print("Please enter a valid bill number")
            return None