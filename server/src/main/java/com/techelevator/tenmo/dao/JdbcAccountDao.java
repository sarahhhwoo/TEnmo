package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;


@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findAccountByAccountId(int accountId) {
        String sql = "SELECT account_id, user_id, balance " +
                "from account " +
                "WHERE account_id = ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, accountId);
        if (rowSet.next()){
            return mapRowToAccount(rowSet);
        } else{
            throw new UsernameNotFoundException("Account Id: " + accountId + " was not found.");
        }
    }

    @Override
    public Account findAccountByUserID(int userId) {
        String sql = "SELECT account_id, user_id, balance " +
                "from account " +
                "WHERE user_id = ?;";
        SqlRowSet rowSet = this.jdbcTemplate.queryForRowSet(sql, userId);
        if (rowSet.next()){
            return mapRowToAccount(rowSet);
        } else{
            throw new UsernameNotFoundException("UserId: " + userId + " was not found.");
        }
    }

    @Override
    public double getAccountBalance(int accountId) {
        String sql = "SELECT balance " +
                "FROM account " +
                "WHERE account_id =?;";
        double balance = -1;

        try {
            balance = this.jdbcTemplate.queryForObject(sql, double.class, accountId);
        }  catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return balance;
        }

    @Override
    public Account addToAccountBalance(int accountId, double amountToAdd) {
        String sql = "UPDATE account " +
                "SET balance = balance + ? " +
                "WHERE account_id =  ?;";
        this.jdbcTemplate.update(sql, amountToAdd, accountId);
        return this.findAccountByAccountId(accountId);
    }

    @Override
    public Account subtractFromAccountBalance(int accountId, double amountToSubtract) {
        String sql = "UPDATE account " +
                "SET balance = balance - ? " +
                "WHERE account_id =  ?;";
        this.jdbcTemplate.update(sql, amountToSubtract, accountId);
        return this.findAccountByAccountId(accountId);
    }

    @Override
    public int getAccountIdByUserId(int userId) {
        String sql = "SELECT account_id " +
                "FROM account " +
                "WHERE user_id = ?;";
        int accountId = -1;

        try {
            accountId = this.jdbcTemplate.queryForObject(sql, Integer.class, userId);
        } catch (NullPointerException e){
            System.out.println(e.getMessage());
        }
        return accountId;
    }


    private Account mapRowToAccount(SqlRowSet rowSet){
        Account account = new Account();
        account.setAccount_id(rowSet.getInt("account_id"));
        account.setUser_id(rowSet.getInt("user_id"));
        account.setBalance(rowSet.getDouble("balance"));
        return account;
    }
}
