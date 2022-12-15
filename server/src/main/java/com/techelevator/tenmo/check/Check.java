package com.techelevator.tenmo.check;

import com.techelevator.tenmo.model.Transaction;

public interface Check {

    boolean checkGreaterThanZero();

    boolean checkNotMoreThanBalance(Transaction transaction);

    boolean checkNotSelf(Transaction transaction);

    boolean canAccessTransactionInfo(int accountId, Transaction transaction);

    boolean canEditTransactionInfo(int accountId, Transaction transaction);

    boolean checkValidAccountId(int accountId);

    boolean checkValidTransactionId(int transactionId);

    boolean checkValidTransaction(Transaction transaction);

    boolean checkWasPending(int transactionId);



}
