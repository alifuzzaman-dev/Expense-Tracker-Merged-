package personalexpense;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Expense {
    private String description;
    private double amount;
    private LocalDate date;
    private String category;

    public Expense(String description, double amount, LocalDate date, String category) {
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.category = category;
    }

    public String getDescription() { return description; }
    public double getAmount()      { return amount; }
    public LocalDate getDate()     { return date; }
    public String getCategory()    { return category; }

    public void setDescription(String description) { this.description = description; }
    public void setAmount(double amount)           { this.amount = amount; }
    public void setDate(LocalDate date)            { this.date = date; }
    public void setCategory(String category)       { this.category = category; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return String.format("%s - %s: $%.2f (%s)",
                date.format(fmt), description, amount, category);
    }
}