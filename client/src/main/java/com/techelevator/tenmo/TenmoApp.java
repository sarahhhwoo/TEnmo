package com.techelevator.tenmo;

import com.techelevator.tenmo.model.Transaction;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

public class TenmoApp {

    private final ConsoleService consoleService = new ConsoleService();
    private final TenmoService tenmoService = new TenmoService();
    private final AuthenticationService authenticationService = new AuthenticationService();

    public static void main(String[] args) {
        TenmoApp app = new TenmoApp();
        app.run();
    }

    private void run() {
        int menuSelection = -1;
        String username = null;
        double balance = 0.0;
        while (menuSelection != 0) {
            //Login
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection();
            if (menuSelection == 1) {
                username = handleLogin();
                balance = handleGetBalance();
            } else if (menuSelection == 2) {
                handleRegister();
            } else if (menuSelection == 0) {
                break;
            } else {
                System.out.println("Invalid Selection");
            }
            if(balance == -1){
                menuSelection = 0;
            }

            if (username != null && balance != -1) {

                menuSelection = -1;
                consoleService.printMainMenu(username, balance);
                menuSelection = consoleService.promptForMenuSelection();
                if (menuSelection == 1){
                    handleSendMoney(username);
                } else if (menuSelection == 2){
                    handleViewTransactions(username);
                }

            }

        }


    }

    private String handleLogin() {
        String username = consoleService.getUsername();
        String password = consoleService.getPassword();
        String token = authenticationService.login(username, password);
        if (token != null) {
            tenmoService.setAuthToken(token);
            return username;
        } else {
            System.out.println("Invalid Login");
            return null;
        }
    }

    private void handleRegister() {
        String username = consoleService.getUsername();
        String password = consoleService.getPassword();
        authenticationService.Register(username, password);
    }

    private double handleGetBalance(){
         return tenmoService.getBalance();
    }

    private void handleSendMoney(String name){
        String nameReceiver = consoleService.sendMoney(tenmoService.getUsernames());
        double amount = consoleService.getAmountToSend();
        Transaction transaction = new Transaction();
        transaction.setMoneySent(amount);
        transaction.setReceiverUsername(nameReceiver);
        transaction.setStatus("Approved");
        transaction.setSenderUsername(name);
        tenmoService.sendTransaction(transaction);
    }

    private void handleViewTransactions(String name){
        consoleService.showTransactions(tenmoService.getTransactions(), name);
    }

    private void handleViewTransactionDetails(){

    }

    private void handleRequestTransaction(){

    }

    private void handleViewPendingRequests(){

    }

    private void handleApprove(){

    }

    private void handleReject(){

    }
}
