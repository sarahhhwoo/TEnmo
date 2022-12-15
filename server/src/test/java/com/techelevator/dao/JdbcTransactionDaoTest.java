package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.dao.JdbcTransactionDao;
import com.techelevator.tenmo.model.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransactionDaoTest extends BaseDaoTests {

    private JdbcTransactionDao sut;
    private JdbcAccountDao accDao;

    private static final Transaction TRANSACTION_1 = new Transaction(3001, "user", "bob", 500, "Approved");
    private static final Transaction TRANSACTION_2 = new Transaction(3002, "bob", "user", 300, "Pending");

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcTransactionDao(jdbcTemplate);
    }

    @Test
    public void listAllTransactionsByUser_returns_correct_list_of_users() {
        // Arrange
        List<Transaction> expectedListWithBothTransactions = new ArrayList<>();
        expectedListWithBothTransactions.add(TRANSACTION_1);
        expectedListWithBothTransactions.add(TRANSACTION_2);

        // Act
        List<Transaction> actualList = sut.listAllTransactionsByUser(2001);

        // Arrange
        Assert.assertEquals(expectedListWithBothTransactions.size(), actualList.size());
        assertTransactionMatch(expectedListWithBothTransactions.get(0), actualList.get(0));
        assertTransactionMatch(expectedListWithBothTransactions.get(1), actualList.get(1));

    }

    @Test
    public void getTransactionByID_returns_correct_Transaction() {
        // Act
        Transaction actual = sut.getTransactionByID(3001);

        // Assert
        assertTransactionMatch(TRANSACTION_1, actual);
    }

    @Test
    public void getTransactionStatus() {
        // Arrange
        String expected = TRANSACTION_1.getStatus();

        // Act
        String actual = sut.getTransactionStatus(3001);

        // Arrange
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void updateTransaction() {
        // Act
        Transaction actual = sut.updateTransaction(3001, TRANSACTION_2);

        // Arrange
        assertTransactionMatch(TRANSACTION_2, actual);
    }

    @Test
    public void listAllPendingTransactions_return_correct_size_and_list() {
        // Arrange
        List<Transaction> expectedList = new ArrayList<>();
        expectedList.add(TRANSACTION_2);

        // Act
        List<Transaction> actualList = sut. listAllPendingTransactions("user");

        // Assert
        Assert.assertEquals(expectedList.size(), actualList.size());
        assertTransactionMatch(expectedList.get(0), actualList.get(0));
    }

    @Test
    public void create_returns_new_id() {
        // Arrange
        Transaction transaction = new Transaction();
        transaction.setReceiverUsername("bob");
        transaction.setSenderUsername("user");
        transaction.setMoneySent(150);
        transaction.setStatus("Approved");
        int expected = 3003;

        // Act
        int actualId = sut.create(transaction);
        transaction.setTransactionId(expected);


        // Arrange
        Assert.assertEquals(expected, actualId);
        assertTransactionMatch(sut.getTransactionByID(3003), transaction);
    }

    @Test
    public void transferFunds_results_in_correct_balances() {
        // Arrange
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        accDao = new JdbcAccountDao(jdbcTemplate);
        double expectedBalance = 1000;

        // Act
        sut.transferFunds(TRANSACTION_1);

        // Assert
        Assert.assertEquals(expectedBalance, accDao.getAccountBalance(2001), 0.00);
        Assert.assertEquals(expectedBalance, accDao.getAccountBalance(2002), 0.00);

    }

    private void assertTransactionMatch(Transaction expected, Transaction actual){
        Assert.assertEquals(expected.getTransactionId(), actual.getTransactionId());
            Assert.assertEquals(expected.getReceiverUsername(), actual.getReceiverUsername());
        Assert.assertEquals(expected.getSenderUsername(), actual.getSenderUsername());
        Assert.assertEquals(expected.getMoneySent(), actual.getMoneySent(), 0.00);
        Assert.assertEquals(expected.getStatus(), actual.getStatus());
    }

}