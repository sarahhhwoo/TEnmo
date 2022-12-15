package com.techelevator.dao;


import com.techelevator.tenmo.dao.JdbcUserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcUserDaoTests extends BaseDaoTests{

    private static final User USER_1 = new User(1001, "bob", "$2a$10$G/MIQ7pUYupiVi72DxqHquxl73zfd7ZLNBoB2G6zUb.W16imI2.W2", "random");
    private static final User USER_2 = new User(1002, "user", "$2a$10$Ud8gSvRS4G1MijNgxXWzcexeXlVs4kWDOkjE7JFIkNLKEuE57JAEy", "random");

    private JdbcUserDao sut;

    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        sut = new JdbcUserDao(jdbcTemplate);
    }

    @Test
    public void createNewUser() {
        boolean userCreated = sut.create("TEST_USER","test_password");
        Assert.assertTrue(userCreated);
        User user = sut.findByUsername("TEST_USER");
        Assert.assertEquals("TEST_USER", user.getUsername());
    }

    @Test
    public void findIdByUsername_returns_correct_Id() {
        // Arrange
        int expected = USER_1.getId();

        // Act
        int actual = sut.findIdByUsername("bob");

        // Assert
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void findAll_returns_all_users_except_self() {
        List<String> expectedWithUser = new ArrayList<>();
        expectedWithUser.add(USER_2.getUsername());

        List<String> expectedWithBoth = new ArrayList<>();
        expectedWithBoth.add(USER_1.getUsername());
        expectedWithBoth.add(USER_2.getUsername());

        // Act
        List<String> actualWithUser = sut.findAll("bob");
        List<String> actualWithBoth = sut.findAll("jeff");

        // Assert
        Assert.assertEquals(expectedWithUser, actualWithUser);
        Assert.assertEquals(expectedWithUser.size(), actualWithUser.size());
        Assert.assertEquals(expectedWithBoth, actualWithBoth);
        Assert.assertEquals(expectedWithBoth.size(), actualWithBoth.size());
    }

    @Test
    public void findByUsername_returns_correct_User() {
        // Arrange
        User expected = USER_1;

        // Act
        User actual = sut.findByUsername("bob");

        // Assert
        assertUserMatch(expected, actual);

    }

    private void assertUserMatch(User expected, User actual) {
        Assert.assertEquals(expected.getId(), actual.getId());
        Assert.assertEquals(expected.getUsername(), actual.getUsername());
        Assert.assertEquals(expected.getPassword(), actual.getPassword());
    }

}
