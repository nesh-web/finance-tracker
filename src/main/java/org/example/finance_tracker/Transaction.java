package org.example.finance_tracker;

public class Transaction {
    private String date;
    private String type;
    private String category;
    private double amount;
    private String description;

    public Transaction(String date, String type, String category, double amount, String description) {
        this.date = date;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.description = description;
    }

    public String getDate() { return date; }
    public String getType() { return type; }
    public String getCategory() { return category; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
}