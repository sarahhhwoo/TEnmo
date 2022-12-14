package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;

import java.util.List;

public interface TransactionDao {

    List<Transaction> listAllTransactionsByUser();

    Transaction getTransactionByID();

    String getTransactionStatus();

    Transaction updateTransaction();

    List<Transaction> listAllPendingTransactions();

    //double getMoneySentByID();

    boolean create();
}
