package com.example.fantasyapp;

import java.util.Date;

public class Transaction {
    private String transactionType;
    private double amount;
    private Date transactionDate;
    private String transactionStatus;
    private String transactionId;

    // Constructor
    public Transaction(String transactionType, double amount, Date transactionDate, String transactionStatus,String transactionId) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.transactionStatus = transactionStatus;
        this.transactionId = transactionId;
    }

    // Getters
    public String getTransactionType() {
        return transactionType;
    }

    public double getAmount() {
        return amount;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getTransactionStatus()
    {
        return transactionStatus;
    }

    public String getTransactionId()
    {
        return transactionId;
    }
}
