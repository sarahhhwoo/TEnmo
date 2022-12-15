package com.techelevator.tenmo.check;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.sql.DataTruncation;

@Component
public class CheckTransfer implements Check{

    private JdbcTemplate jdbcTemplate;

    public CheckTransfer(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate =jdbcTemplate;
    }
    @Override
    public boolean checkGreaterThanZero() {
        return false;
    }

    @Override
    public boolean checkNotMoreThanBalance(Transaction transaction) {
        String sql = "SELECT balance " +
                "FROM account " +
                "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                "WHERE username = ?;";
        boolean canWithdraw = false;
        try {
            double balance = this.jdbcTemplate.queryForObject(sql, Double.class, transaction.getSenderUsername());
            if (balance >= transaction.getMoneySent()) {
                canWithdraw = true;
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return canWithdraw;
    }

    @Override
    public boolean checkNotSelf(Transaction transaction) {
        if (!transaction.getReceiverUsername().equalsIgnoreCase(transaction.getSenderUsername())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canAccessTransactionInfo(String name, Transaction transaction) {
        if(name.equalsIgnoreCase(transaction.getSenderUsername()) || name.equalsIgnoreCase(transaction.getReceiverUsername())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canEditTransactionInfo(String name, Transaction transaction) {
        if(name.equalsIgnoreCase(transaction.getSenderUsername())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkIsReceiver(String name, Transaction transaction) {
        if(name.equalsIgnoreCase(transaction.getReceiverUsername())) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkValidAccountId(String name) {
        String sql = "SELECT account.user_id " +
                "FROM account " +
                "JOIN tenmo_user ON tenmo_user.user_id = account.user_id " +
                "WHERE username = ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, name);
        if (rowSet.next()){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkValidTransactionId(int transactionId) {
        String sql = "SELECT transaction_id " +
                "FROM transaction " +
                "WHERE transaction_id = ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, transactionId);
        if (rowSet.next()){
            return true;
        }
        return false;
    }

    @Override
    public boolean checkValidTransaction(Transaction transaction) {
         return checkValidAccountId(transaction.getSenderUsername()) && checkValidAccountId(transaction.getReceiverUsername());
    }

    @Override
    public boolean checkWasPending(int transactionId) {
        String sql = "SELECT status " +
                "FROM transaction " +
                "WHERE transaction_id = ?;";
        String status = this.jdbcTemplate.queryForObject(sql, String.class, transactionId);
        if (status.equalsIgnoreCase("pending")) {
            return true;
        }
        return false;
    }



    @Override
    public boolean checkTransactionBalanceEditAndNotSelf(Transaction transaction, String name) {
        return checkValidTransaction(transaction) && checkNotMoreThanBalance(transaction) && canEditTransactionInfo(name, transaction) && checkNotSelf(transaction);
    }

    @Override
    public boolean checkTransactionTransactionIdPendingEditNotSelf(Transaction transaction, String name, int id) {
        return checkValidTransaction(transaction) && checkValidTransactionId(id) && checkWasPending(id) && canEditTransactionInfo(name, transaction) && checkNotSelf(transaction);
    }

    @Override
    public boolean checkTransactionTransactionIdBalancePendingEditNotSelf(Transaction transaction, String name, int id) {
        return checkValidTransaction(transaction) && checkNotMoreThanBalance(transaction) && checkValidTransactionId(id) && checkWasPending(id) && canEditTransactionInfo(name, transaction) && checkNotSelf(transaction);
    }

    @Override
    public boolean checkTransactionReceiverNotSelf(Transaction transaction, String name) {
        return checkValidTransaction(transaction) && checkIsReceiver(name, transaction) && checkNotSelf(transaction);
    }

    @Override
    public boolean checkTransactionTransactionIdPendingAccessNotSelf(Transaction transaction, String name, int id) {
        return checkValidTransaction(transaction) && checkValidTransactionId(id) && checkWasPending(id) && canAccessTransactionInfo(name, transaction) && checkNotSelf(transaction);
    }
}
