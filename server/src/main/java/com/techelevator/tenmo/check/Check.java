package com.techelevator.tenmo.check;

import com.techelevator.tenmo.model.Transaction;

public interface Check {

    boolean checkGreaterThanZero();

    boolean checkNotMoreThanBalance();

    boolean checkNotSelf();

    boolean checkAccessOwnAccount();

    boolean checkValidAccountId(int accountId);

    boolean checkValidTransactionId(int transactionId);

    boolean checkValidTransaction(Transaction transaction);



}
