package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transaction;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransactionDao implements TransactionDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcTransactionDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Transaction> listAllTransactionsByUser(int userId) {
        List<Transaction> transactions = new ArrayList<>();
       String sql = "SELECT transaction_id, receiver_account_id, sender_account_id, money_sent, status " +
               "FROM transaction " +
               "WHERE receiver_account_id = ? OR sender_account_id = ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (rowSet.next()){
            transactions.add(mapRowToTransaction(rowSet));
        }
        return transactions;
    }

    @Override
    public Transaction getTransactionByID(int transactionId) {
        String sql = "SELECT transaction_id, receiver_account_id, sender_account_id, money_sent, status " +
                "FROM transaction " +
                "WHERE transaction_id = ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, transactionId);
        if (rowSet.next()){
            return mapRowToTransaction(rowSet);
        }
        throw new UsernameNotFoundException("Transaction ID: " + transactionId + " was not found.");
    }

    @Override
    public String getTransactionStatus(int transactionId) {
        String sql = "SELECT status " +
                "FROM transaction " +
                "WHERE transaction_id = ?;";
        String status = this.jdbcTemplate.queryForObject(sql, String.class, transactionId);
        if (status != null){
            return status;
        } else {
            throw new UsernameNotFoundException("Transaction ID: " + transactionId + " was not found.");
        }
    }

    @Override
    public Transaction updateTransaction(int transactionID, Transaction transaction) {
        //check prior approval
        String sql = "UPDATE transaction " +
                "SET receiver_account_id = ?, sender_account_id = ?, money_sent = ?, status = ? " +
                "WHERE transaction_id = ?;";
        this.jdbcTemplate.update(sql, transaction.getReceiverAccountId(), transaction.getSenderAccountId(),
                transaction.getMoneySent(), transaction.getStatus(), transactionID);
        if (transaction.getStatus().equalsIgnoreCase("Approved")) {
            transferFunds(transaction);
        }
        return this.getTransactionByID(transaction.getTransactionId());
    }

    @Override
    public List<Transaction> listAllPendingTransactions(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        String pending = "pending";
        String sql = "SELECT transaction_id, receiver_account_id, sender_account_id, money_sent, status " +
                "FROM transaction " +
                "WHERE status ILIKE ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, pending);
        while (rowSet.next()){
            Transaction transaction =  mapRowToTransaction(rowSet);
            if (transaction.getReceiverAccountId()==accountId || transaction.getSenderAccountId()==accountId){
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public int create(Transaction transaction) {
        String sql = "INSERT INTO transaction " +
                "(receiver_account_id, sender_account_id, money_sent, status) " +
                "VALUES " +
                "(?, ?, ?, ?)" +
                "RETURNING transaction_id";
        int newID;
        try {
            newID = this.jdbcTemplate.queryForObject(sql, Integer.class, transaction.getReceiverAccountId(), transaction.getSenderAccountId(),
                    transaction.getMoneySent(), transaction.getStatus());
        }catch (DataAccessException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction not created.");
        }
        if (transaction.getStatus().equalsIgnoreCase("Approved")) {
            transferFunds(transaction);
        }
        return newID;
    }

    @Override
    public void transferFunds(Transaction transaction) {
        String sql1 = "UPDATE account " +
                "SET balance = balance - ? " +
                "WHERE account_id = ?;";
        this.jdbcTemplate.update(sql1, transaction.getMoneySent(), transaction.getSenderAccountId());
        String sql2 = "UPDATE account " +
                "SET balance = balance + ? " +
                "WHERE account_id = ?;";
        this.jdbcTemplate.update(sql2, transaction.getMoneySent(), transaction.getReceiverAccountId());
    }

    private Transaction mapRowToTransaction(SqlRowSet rowSet){
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rowSet.getInt("transaction_id"));
        transaction.setReceiverAccountId(rowSet.getInt("receiver_account_id"));
        transaction.setSenderAccountId(rowSet.getInt("sender_account_id"));
        transaction.setMoneySent(rowSet.getDouble("money_sent"));
        transaction.setStatus(rowSet.getString("status"));
        return transaction;
    }

}
