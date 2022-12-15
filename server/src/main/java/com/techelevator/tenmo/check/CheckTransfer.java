package com.techelevator.tenmo.check;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

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
                "WHERE account_id = ?;";
        boolean canWithdraw = false;
        try {
            double balance = this.jdbcTemplate.queryForObject(sql, Double.class, transaction.getSenderAccountId());
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
        if (transaction.getReceiverAccountId() != transaction.getSenderAccountId()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canAccessTransactionInfo(int accountId, Transaction transaction) {
        if(accountId == transaction.getSenderAccountId() || accountId == transaction.getReceiverAccountId()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean canEditTransactionInfo(int accountId, Transaction transaction) {
        if(accountId == transaction.getSenderAccountId()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkIsReceiver(int accountId, Transaction transaction) {
        if(accountId == transaction.getReceiverAccountId()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkValidAccountId(int accountId) {
        String sql = "SELECT user_id " +
                "FROM account " +
                "WHERE account_id = ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, accountId);
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
         return checkValidAccountId(transaction.getSenderAccountId()) && checkValidAccountId(transaction.getReceiverAccountId());
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
}
