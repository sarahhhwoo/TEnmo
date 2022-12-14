package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

public interface AccountDao {

    Account findAccountByAccountId(int accountId);

    Account findAccountByUserID(int userId);

    double getAccountBalance(int accountId);

    Account addToAccountBalance(int accountID, double amountToAdd);

    Account subtractFromAccountBalance(int accountId, double amountToSubtract);

    int getAccountIdByUserId(int userId);

}
