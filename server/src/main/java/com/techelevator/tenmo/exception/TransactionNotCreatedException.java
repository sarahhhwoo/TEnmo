package com.techelevator.tenmo.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unable to create transaction.")
public class TransactionNotCreatedException extends Exception{
}
