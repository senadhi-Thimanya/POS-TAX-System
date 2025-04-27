from POS.Package.Bill import Bill
import os
import datetime


class BillManager:
    """
    Manages bill creation, viewing, searching, and persistence operations.
    Acts as a controller for bill-related functionality in the POS system.
    """

    def __init__(self):
        """
        Initialize the BillManager with an empty bills list and starting bill ID.
        Also loads any previously saved bills from storage.
        """
        self.bills = []  # List to store all bills
        self.next_bill_id = 1001  # Starting bill ID (auto-incrementing)
        # Load bills from storage when initialized
        self.load_bills_from_file()

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

        # Save bills to file after creating a new one
        self.save_bills_to_file()

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
        print(f"{'THE CAKE SHOP':^80}")
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
                f"{i:<4} {item['itemcode']:<15} {item['quantity']:<10} Rs.{item['saleprice']:<11.2f} "
                f"{item['discount']}%{' ':<6} Rs.{item['discountedprice']:<11.2f} Rs.{line_total:<11.2f}")

        # Print grand total
        print(f"{divider}")
        print(f"{'Grand Total:':<68} Rs.{bill.grand_total:<11.2f}")
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

    def save_bills_to_file(self):
        """
        Save all bills to CSV files for persistence.

        Creates two types of files:
        1. bills.csv - Contains bill headers (BillID, Date, GrandTotal)
        2. bill_items_{bill_id}.csv - One file per bill containing item details

        Example bills.csv:
        BillID,Date,GrandTotal
        1001,2025-04-27,41.25
        1002,2025-04-27,78.50

        Example bill_items_1001.csv:
        ItemCode,Cost,SalePrice,Discount,DiscountedPrice,Quantity
        Cake124,10.00,15.00,10,13.50,2
        Lemon_01,3.00,5.00,5,4.75,3
        """
        try:
            # Create Bills directory if it doesn't exist
            os.makedirs("Bills", exist_ok=True)

            # Save bill headers to one file
            with open("Bills/bills.csv", "w") as file:
                file.write("BillID,Date,GrandTotal\n")
                for bill in self.bills:
                    file.write(
                        f"{bill['bill_id']},{datetime.datetime.now().strftime('%Y-%m-%d')},{bill['grand_total']}\n")

            # Save bill items to separate files (one per bill)
            for bill in self.bills:
                with open(f"Bills/bill_items_{bill['bill_id']}.csv", "w") as file:
                    file.write("ItemCode,Cost,SalePrice,Discount,DiscountedPrice,Quantity\n")
                    for item in bill['items']:
                        file.write(f"{item['itemcode']},{item['cost']:.2f},{item['saleprice']:.2f},"
                                   f"{item['discount']},{item['discountedprice']:.2f},{item['quantity']}\n")

            print("\nBills saved successfully")
        except Exception as e:
            print(f"\nError saving bills: {e}")

    def load_bills_from_file(self):
        """
        Load bills from file on program startup.

        This method:
        1. Reads the bills.csv file to get bill headers
        2. For each bill, reads the corresponding bill_items_{bill_id}.csv file
        3. Reconstructs the bills list with all bill data
        4. Updates the next_bill_id based on the highest bill ID found
        """
        try:
            # Check if bills file exists
            if not os.path.exists("Bills/bills.csv"):
                return

            # Load bill headers
            bills_data = []
            with open("Bills/bills.csv", "r") as file:
                next(file)  # Skip header
                for line in file:
                    bill_id, date, grand_total = line.strip().split(",")
                    bills_data.append({
                        'bill_id': int(bill_id),
                        'date': date,
                        'grand_total': float(grand_total),
                        'items': []
                    })

            # Load items for each bill
            for bill in bills_data:
                items_file = f"Bills/bill_items_{bill['bill_id']}.csv"
                if os.path.exists(items_file):
                    with open(items_file, "r") as file:
                        next(file)  # Skip header
                        for line in file:
                            parts = line.strip().split(",")
                            bill['items'].append({
                                'itemcode': parts[0],
                                'cost': float(parts[1]),
                                'saleprice': float(parts[2]),
                                'discount': float(parts[3]),
                                'discountedprice': float(parts[4]),
                                'quantity': int(parts[5])
                            })

            # Update billManager with loaded bills
            self.bills = bills_data

            # Update next_bill_id based on the highest bill_id found
            if bills_data:
                self.next_bill_id = max(bill['bill_id'] for bill in bills_data) + 1

            print(f"\nLoaded {len(bills_data)} bills from storage")
        except Exception as e:
            print(f"\nError loading bills: {e}")
