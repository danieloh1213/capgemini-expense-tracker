import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExpenseManager {
    private List<Expense> expenses;

    public ExpenseManager() {
        this.expenses = new ArrayList<>();
    }

    // add expense method
    // make sure amount is positive before adding expense
    public void addExpense(String category, double amount, String date, String description) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be empty");
        }
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }
        LocalDate dateObj;
        try {
            dateObj = LocalDate.parse(date, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Use \"MM/dd/yyyy\"");
        }

        Expense expense = new Expense(category, amount, dateObj, description);
        expenses.add(expense);
    }

    public double getTotalExpenses() {
        double total = 0.0;
        for (Expense expense : expenses) {
            total += expense.getAmount();
        }
        return total;
    }

    // gets the totals of each category by going through all the expenses
    // returns a map of the category and the amount
    public Map<String, Double> getTotalByCategory() {
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenses) {
            String category = expense.getCategory();
            double currentTotal = categoryTotals.getOrDefault(category, 0.0);
            categoryTotals.put(category, currentTotal + expense.getAmount());
        }
        return categoryTotals;
    }

    // gets the totals by each month
    public Map<String, Double> getMonthlyTrend() {
        Map<String, Double> monthlyTotals = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        for (Expense expense : expenses) {
            String monthKey = expense.getDate().format(formatter);
            double currentTotal = monthlyTotals.getOrDefault(monthKey, 0.0);
            monthlyTotals.put(monthKey, currentTotal + expense.getAmount());
        }

        // return a treemap because treemap is sorted
        return new TreeMap<>(monthlyTotals);
    }

    // gets the totals by each week
    public Map<String, Double> getWeeklyTrend() {
        Map<String, Double> weeklyTotals = new HashMap<>();

        for (Expense expense : expenses) {
            LocalDate date = expense.getDate();

            // creating a sortable key in the form of year month week
            String sortKey = String.format("%d-%02d-W%d",
                    date.getYear(),
                    date.getMonthValue(),
                    (date.getDayOfMonth() - 1) / 7 + 1);

            weeklyTotals.put(sortKey, weeklyTotals.getOrDefault(sortKey, 0.0) + expense.getAmount());
        }

        return new TreeMap<>(weeklyTotals);
    }

    // iterates through the category totals and finds the category with the highest amount
    public String getHighestSpendCategory() {
        Map<String, Double> categoryTotals = getTotalByCategory();
        if (categoryTotals.isEmpty()) {
            return "None";
        }

        String highestCategory = null;
        double highestAmount = Double.MIN_VALUE;

        for (Map.Entry<String, Double> categoryEntry : categoryTotals.entrySet()) {
            if (categoryEntry.getValue() > highestAmount) {
                highestCategory = categoryEntry.getKey();
                highestAmount = categoryEntry.getValue();
            }
        }

        return highestCategory;
    }

    // iterates through the category totals and finds the category with the lowest amount
    public String getLowestSpendCategory() {
        Map<String, Double> categoryTotals = getTotalByCategory();
        if (categoryTotals.isEmpty()) {
            return "None";
        }

        String lowestCategory = null;
        double lowestAmount = Double.MAX_VALUE;

        for (Map.Entry<String, Double> categoryEntry : categoryTotals.entrySet()) {
            if (categoryEntry.getValue() < lowestAmount) {
                lowestCategory = categoryEntry.getKey();
                lowestAmount = categoryEntry.getValue();
            }
        }

        return lowestCategory;
    }

    // get specific category total
    public double getCategoryTotal(String category) {
        Map<String, Double> categoryTotals = getTotalByCategory();
        if (categoryTotals.containsKey(category.toLowerCase().trim())) {
            return categoryTotals.get(category);
        }
        return 0.0;
    }

    public List<Expense> getAllExpenses() {
        List<Expense> expenses = new ArrayList<>(this.expenses);
        expenses.sort(Comparator.comparing(Expense::getDate));
        return expenses;
    }

    public int getExpensesCount() {
        return expenses.size();
    }

    // save expenses to a file
    // input: filename
    public void saveToFile(String filename) {
        try (FileWriter fileWriter = new FileWriter(filename)) {
            List<Expense> sortedExpenses = new ArrayList<>(this.expenses);
            sortedExpenses.sort(Comparator.comparing(Expense::getDate));
            for (Expense expense : sortedExpenses) {
                fileWriter.write(String.format("%s,%f,%s,%s\n",
                        expense.getCategory(),
                        expense.getAmount(),
                        expense.getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")),
                        expense.getDescription()));
            }
            System.out.println("Expense saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving to file: " + e.getMessage());
        }
    }

    // load expenses from file
    public void loadFromFile(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                addExpense(parts[0], Double.parseDouble(parts[1]), parts[2], parts[3]);
            }
            System.out.println("Expense loaded from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    public void loadSeedData() {
        addExpense("food", 45.50, "11/05/2024", "Grocery shopping");
        addExpense("transport", 30.00, "11/07/2024", "Gas");
        addExpense("entertainment", 75.00, "11/10/2024", "Concert tickets");
        addExpense("food", 25.00, "11/12/2024", "Restaurant");
        addExpense("utilities", 120.00, "11/15/2024", "Electric bill");
        addExpense("transport", 15.50, "11/18/2024", "Uber");
        addExpense("food", 60.00, "11/20/2024", "Groceries");
        addExpense("entertainment", 40.00, "11/22/2024", "Movie night");
        addExpense("healthcare", 150.00, "11/25/2024", "Doctor visit");
        addExpense("food", 35.00, "11/28/2024", "Takeout");

        addExpense("food", 50.00, "12/02/2024", "Groceries");
        addExpense("transport", 40.00, "12/05/2024", "Gas");
        addExpense("utilities", 125.00, "12/10/2024", "Water bill");
        addExpense("entertainment", 90.00, "12/12/2024", "Theater show");
        addExpense("food", 30.00, "12/15/2024", "Lunch out");
        addExpense("transport", 20.00, "12/18/2024", "Parking");
        addExpense("food", 55.00, "12/20/2024", "Dinner");
        addExpense("shopping", 200.00, "12/22/2024", "Holiday gifts");
        addExpense("entertainment", 60.00, "12/28/2024", "New Year party");

        addExpense("food", 40.00, "01/03/2025", "Groceries");
        addExpense("transport", 35.00, "01/05/2025", "Gas");
        addExpense("utilities", 130.00, "01/08/2025", "Internet bill");
        System.out.println("Loaded sample expenses");
    }
}
