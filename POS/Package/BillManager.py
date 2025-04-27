from POS.Package.Bill import Bill
import os
import csv
import datetime


class BillManager:
    """
    Manages bill creation, viewing, searching, and persistence operations.
    Acts as a controller for bill-related functionality in the POS system.
    """

    def __init__(self):
        """
        Initialize the BillManager with an empty bills list and starting bill ID.
        """
        self.bills = []  # List to store all bills
        self.next_bill_id = 1001  # Starting bill ID (auto-incrementing)

    def create_bill(self, items):
        """
        Create a new bill from the items in the basket.

        Args:
            items (list): List of dictionaries containing item details

        Returns:
            Bill: The newly created bill object, or None if creation failed
        """
        if not items:
            print("Cannot create bill: No items in basket")
            return None

        # Create a new bill with the next available ID
        bill = Bill(self.next_bill_id, items)

        # Add to memory as a dictionary for easier serialization
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
        Display a bill with proper formatting.

        Args:
            bill (dict or Bill): The bill to display
        """
        # Convert dict to Bill object if needed
        if isinstance(bill, dict):
            bill = Bill(bill['bill_id'], bill['items'])

        # Display the bill with formatting
        border = "=" * 80
        divider = "-" * 80
        print(f"\n{border}")
        print(f"{'THE CAKE SHOP':^75}")
        print(f"{border}")
        print(f"BILL #{bill.bill_id}")
        print(f"{divider}")

        # Print header
        print(
            f"{'No.':<4} {'Item Code':<15} {'Quantity':<10} {'Sale Price':<12} {'Discount':<10} {'Disc. Price':<12} {'Line Total':<12}")
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
        Search for a bill by ID.

        Args:
            bill_id (int): The bill ID to search for

        Returns:
            dict: The bill dictionary if found, None otherwise
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
