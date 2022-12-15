package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.check.CheckTransfer;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.model.User;
import org.springframework.data.relational.core.sql.In;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.TraversableResolver;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TenmoController {

    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private CheckTransfer checkTransfer;
    private UserDao userDao;

    public TenmoController(AccountDao accountDao, TransactionDao transactionDao, CheckTransfer checkTransfer, UserDao userDao){
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.checkTransfer = checkTransfer;
        this.userDao = userDao;
    }


    @RequestMapping(path = "/balance", method = RequestMethod.GET)
    public double currentBalance(Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        int accountId = accountDao.getAccountIdByUserId(userId);
        if (checkTransfer.checkValidAccountId(principal.getName())) {
            return accountDao.getAccountBalance(accountId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Account ID: " + accountId + " was not found.");
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
