package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.check.CheckTransfer;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.AccountNotFoundException;
import com.techelevator.tenmo.exception.TransactionNotCreatedException;
import com.techelevator.tenmo.exception.TransactionNotFoundException;
import com.techelevator.tenmo.exception.TransactionNotPendingException;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class TransactionController {

    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private CheckTransfer checkTransfer;
    private UserDao userDao;


    public TransactionController(AccountDao accountDao, TransactionDao transactionDao, CheckTransfer checkTransfer, UserDao userDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.checkTransfer = checkTransfer;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/transactions",method = RequestMethod.GET)
    public List<Transaction> listTransactionsByAccountId(Principal principal) throws AccountNotFoundException {
        int accountId = getAccountIdFromUsername(principal.getName());
        if (checkTransfer.checkValidAccountId(principal.getName())){
            return transactionDao.listAllTransactionsByUser(accountId);
        }
        throw new AccountNotFoundException();
    }

    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.GET)
    public Transaction transactionById(@PathVariable int id, Principal principal) throws TransactionNotFoundException {
        Transaction transaction = transactionDao.getTransactionByID(id);
        if (checkTransfer.checkValidTransactionId(id) && checkTransfer.canAccessTransactionInfo(principal.getName(), transaction)){
            return transaction;
        }
        throw new TransactionNotFoundException();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public int createTransaction(@Valid @RequestBody Transaction transaction, Principal principal) throws TransactionNotCreatedException {
        transaction.setStatus("Approved");
        if (checkTransfer.checkTransactionBalanceEditAndNotSelf(transaction, principal.getName())){
            return this.transactionDao.create(transaction);
        } else {
            throw new TransactionNotCreatedException();
        }
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "/transactions/{id}", method = RequestMethod.PUT)
    public void updateTransaction(@PathVariable int id, @Valid @RequestBody Transaction transaction, Principal principal) throws TransactionNotFoundException, TransactionNotPendingException {
        if (checkTransfer.checkTransactionTransactionIdPendingAccessNotSelf(transaction, principal.getName(), id)){
            this.transactionDao.updateTransaction(id, transaction);
        } else if(!checkTransfer.checkWasPending(id)) {
            throw new TransactionNotPendingException();
        } else {
            throw new TransactionNotFoundException();
        }
    }

    @RequestMapping(path = "/transactions/pending", method = RequestMethod.GET)
    public List<Transaction> listPendingTransactions(Principal principal) throws TransactionNotFoundException {
        if (checkTransfer.checkValidAccountId(principal.getName())){
            return this.transactionDao.listAllPendingTransactions(principal.getName());
        }
        throw new TransactionNotFoundException();
    }

    private int getAccountIdFromUsername(String name)  {
        int senderId = userDao.findIdByUsername(name);
        return accountDao.getAccountIdByUserId(senderId);
    }
}
