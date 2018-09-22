package com.example.j.testapp;

import java.util.Date;

public class Transaction {
    public Double amount;
    public String date;
    public String category;

    public  Transaction(Double amount,String date,String category) {
        this.amount = amount;
        this.date = date;
        this.category = category;
    }
}
