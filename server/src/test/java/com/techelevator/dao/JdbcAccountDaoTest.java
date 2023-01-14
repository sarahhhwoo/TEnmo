package com.techelevator.dao;

import com.techelevator.tenmo.dao.JdbcAccountDao;
import com.techelevator.tenmo.model.Account;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcAccountDaoTest extends BaseDaoTests {

    private JdbcAccountDao sut;

    private static final Account ACCOUNT_1 = new Account(2001, 1001, 1500.0);
    private static final Account ACCOUNT_2 = new Account(2002, 1002, 500.0);
//    private static final Account ACCOUNT_3 = new Account(2003, 1003, 100.0);
//    private static final Account ACCOUNT_4 = new Account(2004, 1004, 700.0);

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcAccountDao(jdbcTemplate);
    }

    @Test
    public void findAccountByAccountId_returns_correct_account() {
        // Act
        Account actual1 = sut.findAccountByAccountId(2001);
        Account actual2 = sut.findAccountByAccountId(2002);

        // Assert
        assertAccountMatch(ACCOUNT_1, actual1);
        assertAccountMatch(ACCOUNT_2, actual2);
    }

//    @Test
//    public void findAccountByAccountId_returns_nothing() {
//        // Act
//        Account actual = sut.findAccountByAccountId(2005);
//
//        // Assert
//        Assert.assertNull(actual);
//    }

    @Test
    public void findAccountByUserId_returns_correct_Account() {
        //Act
        Account actual1 = sut.findAccountByUserID(1001);
        Account actual2 = sut.findAccountByUserID(1002);

        // Assert
        assertAccountMatch(ACCOUNT_1, actual1);
        assertAccountMatch(ACCOUNT_2, actual2);
    }

    @Test
    public void getAccountBalance_returns_correct_balance() {
        // Arrange
        double expected = ACCOUNT_1.getBalance();

        // Act
        double actual = sut.getAccountBalance(2001);

        // Assert
        Assert.assertEquals(expected, actual, 0.00);
    }

    @Test
    public void addToAccountBalance_returns_correct_balance() {
        // Arrange
        double expectedBalance = ACCOUNT_1.getBalance() + 500;
        sut.addToAccountBalance(2001, 500);

        // Act
        double actualBalance = sut.getAccountBalance(2001);

        // Assert
        Assert.assertEquals(expectedBalance, actualBalance, 0.00);
    }

    @Test
    public void subtractFromAccountBalance_returns_correct_balance() {
        // Arrange
        double expectedBalance = ACCOUNT_1.getBalance() - 500;
        Account actualAccount = sut.subtractFromAccountBalance(2001, 500);

        // Act
        double actualBalance = sut.getAccountBalance(2001);

        // Assert
        Assert.assertEquals(expectedBalance, actualBalance, 0.00);
    }

    @Test
    public void getAccountIdByUserId_returns_correct_AccountId() {
        int expectedId = ACCOUNT_2.getAccountId();

        // Act
        int actualId = sut.getAccountIdByUserId(1002);

        // Arrange
        Assert.assertEquals(expectedId, actualId);
    }


    private void assertAccountMatch(Account expected, Account actual) {
        Assert.assertEquals(expected.getAccountId(), actual.getAccountId());
        Assert.assertEquals(expected.getUserId(), actual.getUserId());
        Assert.assertEquals(expected.getBalance(), actual.getBalance(), 0.00);
    }

}