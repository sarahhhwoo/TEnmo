package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.check.CheckTransfer;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transaction;
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
        if (checkTransfer.checkValidAccountId(accountId)) {
            return accountDao.getAccountBalance(accountId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Account ID: " + accountId + " was not found.");
    }

    @RequestMapping(path = "/transactions",method = RequestMethod.GET)
    public List<Transaction> listTransactionsByAccountId(Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
        int accountId = accountDao.getAccountIdByUserId(userId);
        if (checkTransfer.checkValidAccountId(accountId)){
            return transactionDao.listAllTransactionsByUser(accountId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account ID: " + accountId + " was not found.");
    }

   @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
   public Transaction transactionBYID(@PathVariable int id){
        if (checkTransfer.checkValidTransactionId(id)){
            return transactionDao.getTransactionByID(id);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction ID: " + id + " was not found.");
   }

   @ResponseStatus(HttpStatus.CREATED)
   @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public void createTransaction(@Valid @RequestBody Transaction transaction){
        if (checkTransfer.checkValidTransaction(transaction)){
             this.transactionDao.create(transaction);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create transaction.");
        }
   }

   @ResponseStatus(HttpStatus.ACCEPTED)
   @RequestMapping(path = "/transactions/{id}", method = RequestMethod.PUT)
    public void updateTransaction(@PathVariable int id, @Valid @RequestBody Transaction transaction){
        if (checkTransfer.checkValidTransaction(transaction) && checkTransfer.checkValidTransactionId(id)){
            this.transactionDao.updateTransaction(id, transaction);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction ID: " + id + " was not found " +
                    "or invalid transaction object entered.");
        }
   }

   @RequestMapping(path = "/transactions/pending", method = RequestMethod.GET)
    public List<Transaction> listPendingTransactions(Principal principal){
        int userId = userDao.findIdByUsername(principal.getName());
       int accountId = accountDao.getAccountIdByUserId(userId);
        if (checkTransfer.checkValidAccountId(userId)){
            return this.transactionDao.listAllPendingTransactions(accountId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user ID");
   }
   }
