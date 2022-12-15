package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;

public class Account {

    private int account_id;
    private int user_id;
    @DecimalMin(value = "0.00", message = "Account balance cannot be negative")
    private double balance;

    public Account(int accountId, int userId, double balance){
        this.account_id = accountId;
        this.user_id = userId;
        this.balance = balance;
    }

    public Account() {
    }

    public int getAccount_id() {
        return this.account_id;
    }

    public void setAccount_id(int account_id) {
        this.account_id = account_id;
    }

    public int getUser_id() {
        return this.user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
