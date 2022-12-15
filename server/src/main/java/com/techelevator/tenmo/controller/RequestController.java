package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.check.CheckTransfer;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransactionDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.RequestNotMadeException;
import com.techelevator.tenmo.exception.TransactionNotFoundException;
import com.techelevator.tenmo.exception.TransactionNotPendingException;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
public class RequestController {

    private AccountDao accountDao;
    private TransactionDao transactionDao;
    private CheckTransfer checkTransfer;
    private UserDao userDao;

    public RequestController(AccountDao accountDao, TransactionDao transactionDao, CheckTransfer checkTransfer, UserDao userDao) {
        this.accountDao = accountDao;
        this.transactionDao = transactionDao;
        this.checkTransfer = checkTransfer;
        this.userDao = userDao;
    }

    @RequestMapping(path = "/request", method = RequestMethod.POST)
    public int requestTransaction(@Valid @RequestBody Transaction transaction, Principal principal) throws RequestNotMadeException {
        transaction.setStatus("Pending");
        if (checkTransfer.checkTransactionReceiverNotSelf(transaction, principal.getName())){
            return this.transactionDao.create(transaction);
        } else {
            throw new RequestNotMadeException();
        }
    }

    @RequestMapping(path = "/transactions/{id}/approve", method = RequestMethod.PUT)
    public void approveTransaction(@PathVariable int id, Principal principal) throws TransactionNotPendingException, TransactionNotFoundException {
        Transaction transaction = this.transactionDao.getTransactionByID(id);
        transaction.setStatus("Approved");
        if (checkTransfer.checkTransactionTransactionIdBalancePendingEditNotSelf(transaction, principal.getName(), id)){
            this.transactionDao.updateTransaction(id, transaction);
        } else if(!checkTransfer.checkWasPending(id)) {
            throw new TransactionNotPendingException();
        } else {
            throw new TransactionNotFoundException();
        }


    }

    @RequestMapping(path = "/transactions/{id}/reject", method = RequestMethod.PUT)
    public void rejectTransaction(@PathVariable int id, Principal principal) throws TransactionNotPendingException, TransactionNotFoundException {
        Transaction transaction = this.transactionDao.getTransactionByID(id);
        transaction.setStatus("Rejected");
        if (checkTransfer.checkTransactionTransactionIdPendingEditNotSelf(transaction, principal.getName(), id)){
            this.transactionDao.updateTransaction(id, transaction);
        } else if(!checkTransfer.checkWasPending(id)) {
            throw new TransactionNotPendingException();
        } else {
            throw new TransactionNotFoundException();
        }
    }

    private int getAccountIdFromUsername(String name)  {
        int senderId = userDao.findIdByUsername(name);
        return accountDao.getAccountIdByUserId(senderId);
    }
}
