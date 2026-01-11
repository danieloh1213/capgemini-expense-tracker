import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.File;

public class ExpenseTracker {
    private ExpenseManager expenseManager;
    private Scanner scanner;

    public ExpenseTracker() {
        this.expenseManager = new ExpenseManager();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        System.out.println("*".repeat(45));
        System.out.println("EXPENSE TRACKER");
        System.out.println("*".repeat(45));

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getMenuChoice();

            switch (choice) {
                case 1:
                    addExpense();
                    break;
                case 2:
                    viewTotalExpenses();
                    break;
                case 3:
                    viewExpensesByCategory();
                    break;
                case 4:
                    viewMonthlyTrend();
                    break;
                case 5:
                    viewWeeklyTrend();
                    break;
                case 6:
                    viewHighestLowestCategories();
                    break;
                case 7:
                    viewAllExpenses();
                    break;
                case 8:
                    loadSampleData();
                    break;
                case 9:
                    saveExpensesToFile();
                    break;
                case 10:
                    loadExpensesFromFile();
                    break;
                case 11:
                    running = false;
                    System.out.println("\nExiting expense tracker");
                    break;
                default:
                    System.out.println("Invalid choice");
            }
        }
        scanner.close();
    }

    private void displayMenu() {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("MAIN MENU");
        System.out.println("=".repeat(45));
        System.out.println("1. Add Expense");
        System.out.println("2. View Total Expenses");
        System.out.println("3. View Expenses By Category");
        System.out.println("4. View Monthly Trend");
        System.out.println("5. View Weekly Trend");
        System.out.println("6. View Highest/Lowest Spending Categories");
        System.out.println("7. View All Expenses");
        System.out.println("8. Load Sample Data");
        System.out.println("9. Save Expenses to File");
        System.out.println("10. Load Expenses from File");
        System.out.println("11. Exit");
        System.out.println("=".repeat(45));
    }

    private int getMenuChoice() {
        System.out.print("\nEnter an option (1-11): ");
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void addExpense() {
        System.out.println("\n--- Add New Expense ---");

        try {
            System.out.print("Category: ");
            String category = scanner.nextLine().trim();

            System.out.print("Amount: $");
            double amount = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Date (MM/DD/YYYY): ");
            String date = scanner.nextLine().trim();

            System.out.print("Description: ");
            String description = scanner.nextLine().trim();

            expenseManager.addExpense(category, amount, date, description);
            System.out.println("Expense added successfully.");
        }   catch (NumberFormatException e) {
            System.out.println("\nInvalid amount format");
        }   catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private void viewTotalExpenses() {
        System.out.println("\n--- View Total Expenses ---");
        double total = expenseManager.getTotalExpenses();
        System.out.printf("Total expenses: $%.2f\n", total);
        System.out.printf("Number of expenses: %d\n", expenseManager.getExpensesCount());
    }

    private void viewExpensesByCategory() {
        System.out.println("\n--- View Expenses By Category ---");
        Map<String, Double> categoryTotals = expenseManager.getTotalByCategory();

        if (categoryTotals.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        System.out.println(String.format("%-15s %s", "Category", "Total"));
        System.out.println("-".repeat(40));

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            System.out.printf("%-15s $%.2f\n", entry.getKey(), entry.getValue());
        }
    }

    private void viewMonthlyTrend() {
        System.out.println("\n--- View Monthly Trend ---");
        Map<String, Double> monthlyTrend = expenseManager.getMonthlyTrend();
        if (monthlyTrend.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        System.out.println(String.format("%-15s %s", "Month", "Total"));
        System.out.println("-".repeat(40));

        for (Map.Entry<String, Double> entry : monthlyTrend.entrySet()) {
            System.out.printf("%-15s $%.2f\n", entry.getKey(), entry.getValue());
        }
    }

    private void viewWeeklyTrend() {
        System.out.println("\n--- View Weekly Trend ---");
        Map<String, Double> weeklyTrend = expenseManager.getWeeklyTrend();

        if (weeklyTrend.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        System.out.println(String.format("%-30s %s", "Week", "Total"));
        System.out.println("-".repeat(45));

        for (Map.Entry<String, Double> entry : weeklyTrend.entrySet()) {
            // currently in YYYY-MM-Wx, will make it into Month year - Week x
            String[] parts = entry.getKey().split("-W");
            String[] dateParts = parts[0].split("-");
            int year = Integer.parseInt(dateParts[0]);
            int month = Integer.parseInt(dateParts[1]);
            int week = Integer.parseInt(parts[1]);

            String monthName = java.time.Month.of(month).toString().substring(0,3);
            String display = monthName.charAt(0) + monthName.substring(1).toLowerCase() + " " + year + " - Week " + week;
            System.out.printf("%-30s $%.2f\n", display, entry.getValue());
        }
    }

    private void viewHighestLowestCategories() {
        System.out.println("\n--- View Highest/Lowest Spending Categories ---");
        String highestCategory = expenseManager.getHighestSpendCategory();
        String lowestCategory = expenseManager.getLowestSpendCategory();

        if (highestCategory.isEmpty() || lowestCategory.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        System.out.printf("Highest spending category: %s ($%.2f)\n", highestCategory, expenseManager.getCategoryTotal(highestCategory));
        System.out.printf("Lowest spending category: %s ($%.2f)\n", lowestCategory, expenseManager.getCategoryTotal(lowestCategory));
    }

    private void viewAllExpenses() {
        System.out.println("\n--- All Expenses ---");
        List<Expense> expenses = expenseManager.getAllExpenses();
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded yet.");
            return;
        }

        System.out.println(String.format("%-15s %-11s %-12s %s", "Category", "Amount", "Date", "Description"));
        System.out.println("-".repeat(60));
        for (Expense expense : expenses) {
            System.out.println(expense);
        }
        System.out.printf("\nTotal expenses: %d\n", expenseManager.getExpensesCount());
    }

    private void loadSampleData() {
        System.out.println("\n--- Load Sample Data ---");
        expenseManager.loadSeedData();
    }

    private void saveExpensesToFile() {
        System.out.print("\nEnter filename (must be csv): ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("Error: No filename provided.");
            return;
        }
        if (!filename.endsWith(".csv")) {
            System.out.println("Error: Filename must end with .csv");
            return;
        }

        expenseManager.saveToFile(filename);
    }

    private void loadExpensesFromFile() {
        System.out.print("\nEnter filename (must be csv): ");
        String filename = scanner.nextLine().trim();

        if (filename.isEmpty()) {
            System.out.println("Error: No filename provided.");
            return;
        }
        if (!filename.endsWith(".csv")) {
            System.out.println("Error: Filename must end with .csv");
            return;
        }
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("Error: File '"+ filename +"' does not exist.");
            return;
        }

        expenseManager.loadFromFile(filename);
    }

    public static void main(String[] args) {
        ExpenseTracker expenseTracker = new ExpenseTracker();
        expenseTracker.run();
    }
}
