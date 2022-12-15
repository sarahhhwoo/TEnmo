package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionDao {

    List<Transaction> listAllTransactionsByUser(int userId);

    Transaction getTransactionByID(int transactionId);

    String getTransactionStatus(int transactionId);

    Transaction updateTransaction(int transactionID, Transaction transaction);

    List<Transaction> listAllPendingTransactions(String name);

    int create(Transaction transaction);

    void transferFunds(Transaction transaction);

}
