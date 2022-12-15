package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.check.CheckTransfer;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransactionContoller {

    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private CheckTransfer checkTransfer;
    private UserDao userDao;


    public TransactionContoller(AccountDao accountDao, TransactionDao transactionDao, CheckTransfer checkTransfer, UserDao userDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.checkTransfer = checkTransfer;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/transactions",method = RequestMethod.GET)
    public List<Transaction> listTransactionsByAccountId(Principal principal){
        int accountId = getAccountIdFromUsername(principal.getName());
        if (checkTransfer.checkValidAccountId(principal.getName())){
            return transactionDao.listAllTransactionsByUser(accountId);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Account ID: " + accountId + " was not found.");
    }

    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction transactionById(@PathVariable int id, Principal principal){
        Transaction transaction = transactionDao.getTransactionByID(id);
        if (checkTransfer.checkValidTransactionId(id) && checkTransfer.canAccessTransactionInfo(principal.getName(), transaction)){
            return transaction;
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction ID: " + id + " was not found.");
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public int createTransaction(@Valid @RequestBody Transaction transaction, Principal principal){
        transaction.setStatus("Approved");
        if (checkTransfer.checkTransactionBalanceEditAndNotSelf(transaction, principal.getName())){
            return this.transactionDao.create(transaction);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to create transaction.");
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.PUT)
    public void updateTransaction(@PathVariable int id, @Valid @RequestBody Transaction transaction, Principal principal){
        if (checkTransfer.checkTransactionTransactionIdPendingAccessNotSelf(transaction, principal.getName(), id)){
            this.transactionDao.updateTransaction(id, transaction);
        } else if(!checkTransfer.checkWasPending(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction must be pending to update.");
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction ID: " + id + " was not found " +
                    "or invalid transaction object entered.");
        }
    }

    @RequestMapping(path = "/transactions/pending", method = RequestMethod.GET)
    public List<Transaction> listPendingTransactions(Principal principal){
        if (checkTransfer.checkValidAccountId(principal.getName())){
            return this.transactionDao.listAllPendingTransactions(principal.getName());
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No user ID");
    }

    private int getAccountIdFromUsername(String name)  {
        int senderId = userDao.findIdByUsername(name);
        return accountDao.getAccountIdByUserId(senderId);
    }
}
