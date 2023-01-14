package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.check.CheckTransfer;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TenmoController {

    private AccountDao accountDao;
    private CheckTransfer checkTransfer;
    private UserDao userDao;

    public TenmoController(AccountDao accountDao, CheckTransfer checkTransfer, UserDao userDao){
        this.accountDao = accountDao;
        this.checkTransfer = checkTransfer;
        this.userDao = userDao;
    }


    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public double currentBalance(Principal principal) throws AccountNotFoundException {
        int userId = userDao.findIdByUsername(principal.getName());
        int accountId = accountDao.getAccountIdByUserId(userId);
        if (checkTransfer.checkValidAccountId(principal.getName())) {
            return accountDao.getAccountBalance(accountId);
        }
        throw new AccountNotFoundException();
    }



   @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<String> listAllUsernames(Principal principal) {
        String username = principal.getName();
        return this.userDao.findAll(username);

   }

    private int getAccountIdFromUsername(String name)  {
        int senderId = userDao.findIdByUsername(name);
        return accountDao.getAccountIdByUserId(senderId);
    }


}
