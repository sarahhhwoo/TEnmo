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
    public List<Transaction> listAllTransactionsByUser(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
       String sql = "SELECT transaction_id, receiver_username, sender_username, money_sent, status " +
               "FROM transaction " +
               "JOIN tenmo_user ON transaction.receiver_username = tenmo_user.username OR " +
               "transaction.sender_username = tenmo_user.username " +
               "JOIN account on tenmo_user.user_id = account.user_id " +
               "WHERE account.account_id = ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, accountId);
        while (rowSet.next()){
            transactions.add(mapRowToTransaction(rowSet));
        }
        return transactions;
    }

    @Override
    public Transaction getTransactionByID(int transactionId) {
        String sql = "SELECT transaction_id, receiver_username, sender_username, money_sent, status " +
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
                "SET receiver_username = ?, sender_username = ?, money_sent = ?, status = ? " +
                "WHERE transaction_id = ?;";
        this.jdbcTemplate.update(sql, transaction.getReceiverUsername(), transaction.getSenderUsername(),
                transaction.getMoneySent(), transaction.getStatus(), transactionID);
        if (transaction.getStatus().equalsIgnoreCase("Approved")) {
            transferFunds(transaction);
        }
        return this.getTransactionByID(transaction.getTransactionId());
    }

    @Override
    public List<Transaction> listAllPendingTransactions(String name) {
        List<Transaction> transactions = new ArrayList<>();
        String pending = "pending";
        String sql = "SELECT transaction_id, receiver_username, sender_username, money_sent, status " +
                "FROM transaction " +
                "WHERE status ILIKE ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, pending);
        while (rowSet.next()){
            Transaction transaction =  mapRowToTransaction(rowSet);
            if (transaction.getReceiverUsername().equalsIgnoreCase(name) || transaction.getSenderUsername().equalsIgnoreCase(name)){
                transactions.add(transaction);
            }
        }
        return transactions;
    }

    @Override
    public int create(Transaction transaction) {
        String sql = "INSERT INTO transaction " +
                "(receiver_username, sender_username, money_sent, status) " +
                "VALUES " +
                "(?, ?, ?, ?)" +
                "RETURNING transaction_id";
        int newID;
        try {
            newID = this.jdbcTemplate.queryForObject(sql, Integer.class, transaction.getReceiverUsername(), transaction.getSenderUsername(),
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
        String selectSql1 = "Select user_id " +
                "FROM tenmo_user " +
                "JOIN transaction ON tenmo_user.username = transaction.sender_username " +
                "WHERE sender_username = ? " +
                "GROUP BY user_id;";
        int senderId;
        try {
            senderId = this.jdbcTemplate.queryForObject(selectSql1, Integer.class, transaction.getSenderUsername());
        } catch (NullPointerException e){
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to complete transfer of funds.");
        }
        String selectSql2 = "Select user_id " +
                "FROM tenmo_user " +
                "JOIN transaction ON tenmo_user.username = transaction.receiver_username " +
                "WHERE receiver_username = ? " +
                "GROUP BY user_id;";
        int receiverId;
        try {
            receiverId = this.jdbcTemplate.queryForObject(selectSql2, Integer.class, transaction.getReceiverUsername());
        } catch (NullPointerException e){
            throw  new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to complete transfer of funds.");
        }

        String sql1 = "UPDATE account " +
                "SET balance = account.balance - ? " +
                "WHERE user_id = ?;";
        this.jdbcTemplate.update(sql1, transaction.getMoneySent(), senderId);
        String sql2 = "UPDATE account " +
                "SET balance = balance + ? " +
                "WHERE user_id = ?;";
        this.jdbcTemplate.update(sql2, transaction.getMoneySent(), receiverId);
    }

    private Transaction mapRowToTransaction(SqlRowSet rowSet){
        Transaction transaction = new Transaction();
        transaction.setTransactionId(rowSet.getInt("transaction_id"));
        transaction.setReceiverUsername(rowSet.getString("receiver_username"));
        transaction.setSenderUsername(rowSet.getString("sender_username"));
        transaction.setMoneySent(rowSet.getDouble("money_sent"));
        transaction.setStatus(rowSet.getString("status"));
        return transaction;
    }

}
