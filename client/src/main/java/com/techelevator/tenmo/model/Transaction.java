package com.techelevator.tenmo.model;

public class Transaction {
    private int transactionId;
    private String receiverUsername;
    private String senderUsername;
    private double moneySent;
    private String status;

    public int getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getReceiverUsername() {
        return this.receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getSenderUsername() {
        return this.senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
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
