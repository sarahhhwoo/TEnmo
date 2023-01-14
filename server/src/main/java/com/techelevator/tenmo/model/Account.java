package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.DecimalMin;

public class Account {

    @JsonProperty("account_id")
    private int accountId;
    @JsonProperty("user_id")
    private int userId;
    @DecimalMin(value = "0.00", message = "Account balance cannot be negative")
    private double balance;

    public Account(int accountId, int userId, double balance){
        this.accountId = accountId;
        this.userId = userId;
        this.balance = balance;
    }

    public Account() {
    }

    public int getAccountId() {
        return this.accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public int getUserId() {
        return this.userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getBalance() {
        return this.balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
