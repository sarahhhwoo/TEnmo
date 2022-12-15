package com.techelevator.tenmo.model;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Positive;

public class Transaction {

    private int transactionId;
    private int receiverAccountId;
    private int senderAccountId;
    @DecimalMin(value = "0.01", message = "Must send more than 0.00")
    private double moneySent;
    private String status;

    public Transaction() {

    }

    public Transaction(int transactionId, int receiverAccountId, int senderAccountId, double moneySent, String status) {
        this.transactionId = transactionId;
        this.receiverAccountId = receiverAccountId;
        this.senderAccountId = senderAccountId;
        this.moneySent = moneySent;
        this.status = status;
    }

    public int getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getReceiverAccountId() {
        return this.receiverAccountId;
    }

    public void setReceiverAccountId(int receiverAccountId) {
        this.receiverAccountId = receiverAccountId;
    }

    public int getSenderAccountId() {
        return this.senderAccountId;
    }

    public void setSenderAccountId(int senderAccountId) {
        this.senderAccountId = senderAccountId;
    }

    public double getMoneySent() {
        return this.moneySent;
    }

    public void setMoneySent(double moneySent) {
        this.moneySent = moneySent;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
