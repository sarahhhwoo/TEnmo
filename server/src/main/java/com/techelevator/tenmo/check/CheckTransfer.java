package com.techelevator.tenmo.check;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

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
    public boolean checkNotMoreThanBalance() {
        return false;
    }

    @Override
    public boolean checkNotSelf() {
        return false;
    }

    @Override
    public boolean checkAccessOwnAccount() {
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
}
