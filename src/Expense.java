import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Expense {
    private String category;
    private double amount;
    private LocalDate date;
    private String description;

    public Expense(String category, double amount, LocalDate date, String description) {
        this.category = category.toLowerCase().trim();
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // formatter makes the date do month day year in XX/YY/ZZZZ
    // - in front of the string for left align
    // 15 char width covers most categories
    // 10 char width for prices covers most prices. the .2 is for decimal
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        return String.format("%-15s $%-10.2f %-12s %s", category, amount, formatter.format(date), description);
    }
}
