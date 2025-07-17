import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Class representing a product
class Product {
    private final String name;
    private final double price;
    private final int quantity;

    public Product(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}

// Class representing a shopping cart
class ShoppingCart {
    private List<Product> items;
    private double discount;
    private LocalDateTime transactionTime;

    public ShoppingCart() {
        items = new ArrayList<>();
        discount = 0.0;
        transactionTime = LocalDateTime.now();
    }

    public void addItem(Product product) {
        items.add(product);
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
        }
    }

    public double calculateTotal() {
        double total = 0;
        for (Product item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return Math.max(total - discount, 0); // Ensure total does not go negative
    }

    public void setDiscount(double discount) {
        double total = calculateTotal() + this.discount; // Calculate total before applying new discount
        if (discount > total) {
            System.out.println("Discount cannot exceed total cost.");
        } else {
            this.discount = discount;
        }
        
    }

    public void printInvoice() {
        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println("----------- INVOICE -----------");
        for (int i = 0; i < items.size(); i++) {
            Product item = items.get(i);
            System.out.println((i + 1) + ". " + item.getName() + "\t$" + df.format(item.getPrice()) + "\tQty: " + item.getQuantity());
        }
        System.out.println("-------------------------------");
        System.out.println("Total: $" + df.format(calculateTotal()));
        System.out.println("Discount: $" + df.format(discount));
        System.out.println("-------------------------------");
    }

    public int getCartSize() {
        return items.size();
    }

    public Product getItem(int i) {
        return items.get(i);
    }

    public void saveInvoiceToFile() {
        DecimalFormat df = new DecimalFormat("0.00");
        try (FileWriter writer = new FileWriter("invoice.txt")) {
            writer.write("----------- INVOICE -----------\n");
            for (int i = 0; i < items.size(); i++) {
                Product item = items.get(i);
                writer.write((i + 1) + ". " + item.getName() + "\t$" + df.format(item.getPrice()) + "\tQty: " + item.getQuantity() + "\n");
            }
            writer.write("-------------------------------\n");
            writer.write("Total: $" + df.format(calculateTotal()) + "\n");
            writer.write("Discount: $" + df.format(discount) + "\n");
            writer.write("-------------------------------\n");
            System.out.println("Invoice saved to file.");
        } catch (IOException e) {
            System.out.println("Failed to save invoice to file: " + e.getMessage());
        }
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void generateTransaction() {
        TransactionManager.addTransaction(this);
    }
}

// Class for managing profit and loss reports
class ReportGenerator {
    public static void generateProfitLossReport(String period) {
        double totalProfit = 0;
        double totalLoss = 0;
        LocalDateTime now = LocalDateTime.now();

        for (Transaction transaction : TransactionManager.getTransactions()) {
            LocalDateTime transactionTime = transaction.getCart().getTransactionTime();
            boolean includeTransaction = false;

            // Filtering based on period (weekly, monthly, yearly)
            switch (period.toLowerCase()) {
                case "weekly":
                    includeTransaction = transactionTime.isAfter(now.minusWeeks(1)) && transactionTime.isBefore(now);
                    break;
                case "monthly":
                    includeTransaction = transactionTime.isAfter(now.minusMonths(1)) && transactionTime.isBefore(now);
                    break;
                case "yearly":
                    includeTransaction = transactionTime.isAfter(now.minusYears(1)) && transactionTime.isBefore(now);
                    break;
                default:
                    System.out.println("Invalid report period. Use 'weekly', 'monthly', or 'yearly'.");
                    return;
            }

            // Adding to profit or loss
            if (includeTransaction) {
                double total = transaction.getCart().calculateTotal();
                if (total >= 0) {
                    totalProfit += total;
                } else {
                    totalLoss += Math.abs(total);
                }
            }
        }

        System.out.println("Profit/Loss Report for " + period);
        System.out.println("Profit: $" + totalProfit);
        System.out.println("Loss: $" + totalLoss);
    }
}

// Class for managing transactions
class Transaction {
    private ShoppingCart cart;

    public Transaction(ShoppingCart cart) {
        this.cart = cart;
    }

    public ShoppingCart getCart() {
        return cart;
    }
}

// Transaction manager class for storing transactions
class TransactionManager {
    private static List<Transaction> transactions = new ArrayList<>();

    public static void addTransaction(ShoppingCart cart) {
        transactions.add(new Transaction(cart));
    }

    public static List<Transaction> getTransactions() {
        return transactions;
    }
}

// Main class representing the Supermarket Billing System
public class SupermarketBillingSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static ShoppingCart cart = new ShoppingCart();

    public static void main(String[] args) {
        showMenu();
    }

    private static void showMenu() {
        int choice;
        do {
            System.out.println("------ Supermarket Billing System ------");
            System.out.println("1. Add item to cart");
            System.out.println("2. Remove item from cart");
            System.out.println("3. View cart");
            System.out.println("4. Apply discount");
            System.out.println("5. Generate invoice");
            System.out.println("6. Download invoice");
            System.out.println("7. Weekly profit/loss report");
            System.out.println("8. Monthly profit/loss report");
            System.out.println("9. Yearly profit/loss report");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            choice = getIntInput();

            switch (choice) {
                case 1:
                    addItemToCart();
                    break;
                case 2:
                    removeItemFromCart();
                    break;
                case 3:
                    viewCart();
                    break;
                case 4:
                    applyDiscount();
                    break;
                case 5:
                    generateInvoice();
                    break;
                case 6:
                    downloadInvoice();
                    break;
                case 7:
                    ReportGenerator.generateProfitLossReport("weekly");
                    break;
                case 8:
                    ReportGenerator.generateProfitLossReport("monthly");
                    break;
                case 9:
                    ReportGenerator.generateProfitLossReport("yearly");
                    break;
                case 10:
                    System.out.println("Thank you for using the Supermarket Billing System!");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 10);
    }

    private static void addItemToCart() {
        scanner.nextLine(); // Consume any leftover newline from previous input
        System.out.print("Enter product name: ");
        String name = scanner.nextLine(); // Read product name
        System.out.print("Enter price: ");
        double price = getDoubleInput(); // Read price
        System.out.print("Enter quantity: ");
        int quantity = getIntInput(); // Read quantity

        Product product = new Product(name, price, quantity);
        cart.addItem(product);

        System.out.println("Item added to cart!");
    }

    private static void removeItemFromCart() {
        System.out.print("Enter item number to remove: ");
        int index = getIntInput();

        if (index >= 1 && index <= cart.getCartSize()) {
            cart.removeItem(index - 1);
            System.out.println("Item removed from cart!");
        } else {
            System.out.println("Invalid item number.");
        }
    }

    private static void viewCart() {
        if (cart.getCartSize() > 0) {
            DecimalFormat df = new DecimalFormat("0.00");
            System.out.println("------ Cart Items ------");
            for (int i = 0; i < cart.getCartSize(); i++) {
                Product item = cart.getItem(i);
                System.out.println((i + 1) + ". " + item.getName() + "\t$" + df.format(item.getPrice()) + "\tQty: " + item.getQuantity());
            }
            System.out.println("------------------------");
            System.out.println("Total: $" + df.format(cart.calculateTotal()));
            System.out.println("------------------------");
        } else {
            System.out.println("No items in the cart.");
        }
    }

    private static void applyDiscount() {
        System.out.print("Enter discount amount: ");
        double discount = getDoubleInput();
        cart.setDiscount(discount);
    }

    private static void generateInvoice() {
        if (cart.getCartSize() > 0) {
            cart.printInvoice();
            cart.generateTransaction(); // Save transaction after generating invoice
        } else {
            System.out.println("Cart is empty. No invoice to generate.");
        }
    }

    private static void downloadInvoice() {
        cart.saveInvoiceToFile();
    }

    private static int getIntInput() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter an integer.");
            scanner.next(); // Consume invalid input
        }
        return scanner.nextInt();
    }

    private static double getDoubleInput() {
        while (!scanner.hasNextDouble()) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.next(); // Consume invalid input
        }
        return scanner.nextDouble();
    }
}
