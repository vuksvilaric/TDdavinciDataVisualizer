package com.example.j.testapp;

import java.util.Calendar;
import java.util.Date;


public class Transaction implements Comparable<Transaction>{
    public Double amount;
    public Calendar dateCal;
    public Date date;
    public String category;

    public  Transaction(Double amount, Calendar dateCal, Date date, String category) {
        this.amount = amount;
        this.dateCal = dateCal;
        this.date = date;
        this.category = category;
    }

    @Override
    public int compareTo(Transaction o) {
        return date.compareTo(o.date);
    }
}
