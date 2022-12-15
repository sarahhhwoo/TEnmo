package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.LOCKED, reason = "Status must be \"Pending\" in order to update.")
public class TransactionNotPendingException extends Exception{
}
