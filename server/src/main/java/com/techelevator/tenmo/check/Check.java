package com.techelevator.tenmo.check;

import com.techelevator.tenmo.model.Transaction;

public interface Check {

    boolean checkGreaterThanZero();

    boolean checkNotMoreThanBalance(Transaction transaction);

    boolean checkNotSelf(Transaction transaction);

    boolean canAccessTransactionInfo(String name, Transaction transaction);

    boolean canEditTransactionInfo(String name, Transaction transaction);

    boolean checkIsReceiver(String name, Transaction transaction);

    boolean checkValidAccountId(String name);

    boolean checkValidTransactionId(int transactionId);

    boolean checkValidTransaction(Transaction transaction);

    boolean checkWasPending(int transactionId);

    boolean checkTransactionBalanceEditAndNotSelf(Transaction transaction, String name);

    boolean checkTransactionTransactionIdPendingEditNotSelf(Transaction transaction, String name, int id);

    boolean checkTransactionTransactionIdBalancePendingEditNotSelf(Transaction transaction, String name, int id);

    boolean checkTransactionReceiverNotSelf(Transaction transaction, String name);

    boolean checkTransactionTransactionIdPendingAccessNotSelf(Transaction transaction, String name, int id);
}
