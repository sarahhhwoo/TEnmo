package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.CredentialsDto;
import com.techelevator.tenmo.model.Transaction;
import org.springframework.http.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TenmoService {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public double getBalance(){
        try {
            return restTemplate.exchange(API_BASE_URL + "balance", HttpMethod.GET, makeAuthEntity(), Double.class).getBody();
        } catch (ResourceAccessException | RestClientResponseException | NullPointerException e){
            System.out.println("Error: Cannot retrieve balance at the moment.");
        }
        return -1;
    }

    public void sendTransaction(Transaction transaction){
        try {
            restTemplate.exchange(API_BASE_URL + "transactions", HttpMethod.POST, makeTransactionEntity(transaction), Integer.class);
        } catch (ResourceAccessException | RestClientResponseException e){
            System.out.println("Error: Cannot complete inputted transaction due to bad client request.");
        }
    }

    public List<String> getUsernames(){
        try {
            return Arrays.asList(restTemplate.exchange(API_BASE_URL + "users", HttpMethod.GET, makeAuthEntity(), String[].class).getBody());
        }catch (ResourceAccessException | RestClientResponseException | NullPointerException e){
            System.out.println("Error: Unable to retrieve usernames at the moment.");
        }
        return null;
    }

    public List<Transaction> getTransactions(){
        try {
            return Arrays.asList(restTemplate.exchange(API_BASE_URL + "transactions", HttpMethod.GET, makeAuthEntity(), Transaction[].class).getBody());
        } catch (ResourceAccessException | RestClientResponseException | NullPointerException e){
            System.out.println("Error: Unable to retrieve transaction history.");
        }
        return null;
    }

    public void requestTransaction(Transaction transaction) {
        try {
            restTemplate.exchange(API_BASE_URL + "request", HttpMethod.POST, makeTransactionEntity(transaction), Integer.class);
        } catch (ResourceAccessException | RestClientResponseException e){
            System.out.println("Error: Cannot complete your request due to bad client request.");
        }
    }

    public List<Transaction> getPendingTransactions(){
        try {
            return Arrays.asList(restTemplate.exchange(API_BASE_URL + "transactions/pending", HttpMethod.GET, makeAuthEntity(), Transaction[].class).getBody());
        } catch (ResourceAccessException | RestClientResponseException | NullPointerException e){
            System.out.println("Error: Unable to retrieve pending transactions.");
        }
        return null;
    }

    public void approveTransaction(int id){
        try {
            restTemplate.exchange(API_BASE_URL + "transactions/" + id + "/approve", HttpMethod.PUT, makeAuthEntity(), Void.class);
            System.out.println("Transaction is now approved.");
        } catch (ResourceAccessException | RestClientResponseException | NullPointerException e){
            System.out.println("Error: Unable to approve transaction.");
        }
    }

    public void rejectTransaction(int id){
        try {
            restTemplate.exchange(API_BASE_URL + "transactions/" + id + "/reject", HttpMethod.PUT, makeAuthEntity(), Void.class);
        } catch (ResourceAccessException | RestClientResponseException | NullPointerException e){
            //System.out.println(e.getMessage());
            System.out.println("Error: Unable to reject transaction.");
        }
    }


    private HttpEntity<Void> makeAuthEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(headers);
    }

    private HttpEntity<Transaction> makeTransactionEntity(Transaction transaction){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        return new HttpEntity<>(transaction, headers);
    }

}
