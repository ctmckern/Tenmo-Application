package com.techelevator.tenmo;

import com.techelevator.tenmo.exceptions.InvalidTransferIdChoice;
import com.techelevator.tenmo.exceptions.InvalidUserChoiceException;
import com.techelevator.tenmo.exceptions.UserNotFoundException;
import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.*;
import com.techelevator.tenmo.view.ConsoleService;
import java.math.BigDecimal;
import java.util.List;

public class App {
    private static final String API_BASE_URL = "http://localhost:8080/";
    //menu options provided to user
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
    private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
    private static final String[] LOGIN_MENU_OPTIONS = {LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};
    private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View current balance";
    private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View past transfers";
    private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
    private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View pending requests";
    private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
    private static final String[] MAIN_MENU_OPTIONS = {MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT};

    private AuthenticatedUser currentUser;
    private final ConsoleService console;
    private final AuthenticationService authenticationService;
    private final AccountService accountService;
    private final UserService userService;

    private final TransferService transferService;

    private static int transferIdNumber;

    public static void main(String[] args) {
        App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL));
        app.run();
    }
    public App(ConsoleService console, AuthenticationService authenticationService) {
        this.console = console;
        this.authenticationService = authenticationService;
        this.accountService = new RestAccountService(API_BASE_URL);
        this.userService = new RestUserService();

        this.transferService = new RestTransferService(API_BASE_URL);
    }
    //increment transfer ID each time transfer is processed
    public static void incrementTransferIdNumber() {
        transferIdNumber++;
    }
    //application
    public void run() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");

        registerAndLogin();
        mainMenu();
    }
    private void mainMenu() {
        while (true) {
            String choice = (String) console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
            if (MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
                //Working
                viewCurrentBalance();
            } else if (MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
                //Working
                viewTransferHistory();
            } else if (MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
                //GOOD TO GO
                viewPendingRequests();
            } else if (MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
                //GOOD TO GO
                sendBucks(); // start at line 132
            } else if (MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
                //GOOD TO GO
                requestBucks();
            } else if (MAIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else {
                // the only other option on the main menu is to exit
                exitProgram();
            }
        }
    }
    //view balance method
    private void viewCurrentBalance() {
        Account balance = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId());
        System.out.println("Your current account balance is:  $" + balance.getBalance());
    }
    //view transfers that have occurred method
    private void viewTransferHistory() {
        //If fails convert to list of transfers instead of array
        //First makes a list of transfers, then needs to print the stubs and ask for input
        Account account = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId());
        Transfer[] transfers = transferService.getTransfersFromAccountId(currentUser, account.getAccountId());
        System.out.println("-------------------------------");
        System.out.println("Transfers");
        System.out.println("ID          To          Amount");
        System.out.println("-------------------------------");

        Account userAccount = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId());
        int currentUserAccountId = userAccount.getAccountId();
        for(Transfer transfer: transfers) {
            printTransferStubDetails(currentUser, transfer);
        }
        int transferIdChoice = console.getUserInputInteger("\nPlease enter transfer ID to view details (0 to cancel)");
        Transfer transferChoice = validateTransferIdChoice(transferIdChoice, transfers, currentUser);
        if(transferChoice != null) {
            printTransferDetails(currentUser, transferChoice);
        }
    }
    //review pending requests for user, line 91 of transferService
    private void viewPendingRequests() {
        Account toGather = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId());
        Transfer[] transfers = transferService.getPendingTransfersByUserId(currentUser, toGather.getAccountId());
        System.out.println("-------------------------------");
        System.out.println("Pending Transfers");
        System.out.println("ID          To          Amount");
        System.out.println("-------------------------------");
        for(Transfer transfer: transfers) {
            printTransferStubDetails(currentUser, transfer);
        }
        // TODO ask to view details
        int transferIdChoice = console.getUserInputInteger("\nPlease enter transfer ID to approve/reject (0 to cancel)");
        Transfer transferChoice = validateTransferIdChoice(transferIdChoice, transfers, currentUser);
        if(transferChoice != null) {
            approveOrReject(transferChoice, currentUser);
        }
    }
    //method to send TE bucks to another user, createTransfer is line 23 of restTransfer
    private void sendBucks() {
        User[] users = userService.getAllUsersExceptCurrent(currentUser, currentUser.getUser().getId());
        printUserOptions(currentUser, users);
        int userIdChoice = console.getUserInputInteger("Enter ID of user you are sending to (0 to cancel)");
        if (validateUserChoice(userIdChoice, users, currentUser)) {
            String amountChoice = console.getUserInput("Enter amount");
            createTransfer(userIdChoice, amountChoice, 2, 2);
        }
    }
    //method to ask for TE bucks from another user
    private void requestBucks () {
        User[] users = userService.getAllUsersExceptCurrent(currentUser, currentUser.getUser().getId());
        printUserOptions(currentUser, users);
        int userIdChoice = console.getUserInputInteger("Enter ID of user you are requesting from (0 to cancel)");
        if (validateUserChoice(userIdChoice, users, currentUser)) {
            String amountChoice = console.getUserInput("Enter amount");
            createTransfer(userIdChoice, amountChoice, 1, 1);
        }
    }
    //exit application
    private void exitProgram () {
        System.exit(0);
    }

    private void registerAndLogin () {
        while (!isAuthenticated()) {
            String choice = (String) console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
            if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
                login();
            } else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
                register();
            } else {
                // the only other option on the login menu is to exit
                exitProgram();
            }
        }
    }
    private boolean isAuthenticated () {
        return currentUser != null;
    }
    private void register () {
        System.out.println("Please register a new user account");
        boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            authenticationService.register(credentials);
            isRegistered = true;
            System.out.println("Registration successful. You can now login.");
        }
    }
    //method to login to application with setup credentials
    private void login () {
        System.out.println("Please log in");
        currentUser = null;
        while (currentUser == null) //will keep looping until user is logged in
        {
            //for some reason I'm getting a 404 here
            UserCredentials credentials = collectUserCredentials();
            currentUser = authenticationService.login(credentials);
        }
        transferIdNumber = getHighestTransferIdNumber() + 1;
    }
    private UserCredentials collectUserCredentials () {
        String username = console.getUserInput("Username");
        String password = console.getUserInput("Password");
        return new UserCredentials(username, password);
    }
    private Transfer createTransfer (int accountChoiceUserId, String amountString, int transferType, int status){
        int accountToId;
        int accountFromId;
        //issue here, account ids are defaulting to 0.
        if(transferType == 2) {
            accountToId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
            accountFromId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
        } else {
            accountToId = accountService.getAccountByUserId(currentUser, currentUser.getUser().getId()).getAccountId();
            accountFromId = accountService.getAccountByUserId(currentUser, accountChoiceUserId).getAccountId();
        }
        BigDecimal amount = new BigDecimal(amountString);
        Transfer transfer = new Transfer();
        transfer.setAccount_from(accountFromId);
        transfer.setAccount_to(accountToId);
        transfer.setAmount(amount);
        transfer.setTransfer_status_id(status);
        transfer.setTransfer_type_id(transferType);
        transfer.setTransfer_id(transferIdNumber);
        transferService.createTransfer(currentUser, transfer);
        //increment transferIdNumber so it is always unique
        App.incrementTransferIdNumber();
        return transfer;
    }
    private int getHighestTransferIdNumber() {
        Transfer[] transfers = transferService.getAllTransfers(currentUser);
        int highestTransferIdNumber = 0;
        if (!(transfers.length == 0)) {
            for (Transfer transfer : transfers) {
                if (transfer.getTransfer_id() > highestTransferIdNumber) {
                    highestTransferIdNumber = transfer.getTransfer_id();
                }
            }
        }
        return highestTransferIdNumber;
    }
    private void printTransferStubDetails(AuthenticatedUser authenticatedUser, Transfer transfer) {
        String fromOrTo = "";
        int accountFrom = transfer.getAccount_from();
        int accountTo = transfer.getAccount_to();
        if (accountService.getAccountById(currentUser, accountTo).getUserId() == authenticatedUser.getUser().getId()) {
            int accountFromUserId = accountService.getAccountById(currentUser, accountFrom).getUserId();
            String userFromName = userService.getUserByUserId(currentUser, accountFromUserId).getUsername();
            fromOrTo = "From: " + userFromName;
        } else {
            int accountToUserId = accountService.getAccountById(currentUser, accountTo).getUserId();
            String userToName = userService.getUserByUserId(currentUser, accountToUserId).getUsername();
            fromOrTo = "To: " + userToName;
        }
        console.printTransfers(transfer.getTransfer_id(), fromOrTo, transfer.getAmount());
    }
    private void printTransferDetails(AuthenticatedUser currentUser, Transfer transferChoice) {
        String transferType = assignType(transferChoice.getTransfer_type_id());
        String transferStatus = assignStatus(transferChoice.getTransfer_status_id());
        int id = transferChoice.getTransfer_id();
        BigDecimal amount = transferChoice.getAmount();
        int fromAccount = transferChoice.getAccount_from();
        int toAccount = transferChoice.getAccount_to();
        int transactionTypeId = transferChoice.getTransfer_type_id();
        int transactionStatusId = transferChoice.getTransfer_status_id();
        int fromUserId = accountService.getAccountById(currentUser, fromAccount).getUserId();
        String fromUserName = userService.getUserByUserId(currentUser, fromUserId).getUsername();
        int toUserId = accountService.getAccountById(currentUser, toAccount).getUserId();
        String toUserName = userService.getUserByUserId(currentUser, toUserId).getUsername();
        console.printTransferDetails(id, fromUserName, toUserName, transferType, transferStatus, amount);
    }

    private String assignType(int type){
        String returnType = "";
        if(type == 1){
            returnType = "Request";
        }
        if (type == 2){
            returnType = "Send";
        }
        return returnType;
    }

    private String assignStatus(int status){
        String returnStatus = "";
        if (status == 1){
            returnStatus = "Pending";
        }
        if (status == 2){
            returnStatus = "Approved";
        }
        if (status == 3){
            returnStatus = "Rejected";
        }
        return returnStatus;
    }

    private void printUserOptions(AuthenticatedUser authenticatedUser, User[] users) {
        System.out.println("-------------------------------");
        System.out.println("Users");
        System.out.println("ID          Name");
        System.out.println("-------------------------------");
        // TODO: update this to not display current user
        users = userService.getAllUsersExceptCurrent(currentUser, currentUser.getUser().getId());
        console.printUsers(users);
    }
    private boolean validateUserChoice(int userIdChoice, User[] users, AuthenticatedUser currentUser) {
        if(userIdChoice != 0) {
            try {
                boolean validUserIdChoice = false;
                for (User user : users) {
                    if(userIdChoice == currentUser.getUser().getId()) {
                        throw new InvalidUserChoiceException();
                    }
                    if (user.getId() == userIdChoice) {
                        validUserIdChoice = true;
                        break;
                    }
                }
                if (validUserIdChoice == false) {
                    throw new UserNotFoundException();
                }
                return true;
            } catch (UserNotFoundException | InvalidUserChoiceException e) {
                System.out.println(e.getMessage());
            }
        }
        return false;
    }
    private Transfer validateTransferIdChoice(int transferIdChoice, Transfer[] transfers, AuthenticatedUser currentUser) {
        Transfer transferChoice = null;
        if(transferIdChoice != 0) {
            try {
                boolean validTransferIdChoice = false;
                for (Transfer transfer : transfers) {
                    if (transfer.getTransfer_id() == transferIdChoice) {
                        validTransferIdChoice = true;
                        transferChoice = transfer;
                        break;
                    }
                }
                if (!validTransferIdChoice) {
                    throw new InvalidTransferIdChoice();
                }
            } catch (InvalidTransferIdChoice e) {
                System.out.println(e.getMessage());
            }
        }
        return transferChoice;
    }
    private void approveOrReject(Transfer pendingTransfer, AuthenticatedUser authenticatedUser) {
        // TODO: write method to approve or reject transfer
        console.printApproveOrRejectOptions();
        int choice = console.getUserInputInteger("Please choose an option");
        if(choice != 0) {
            if(choice == 1) {
                pendingTransfer.setTransfer_status_id(2);
            } else if (choice == 2) {
                pendingTransfer.setTransfer_status_id(3);
            } else {
                System.out.println("Invalid choice.");
            }
            transferService.updateTransfer(currentUser, pendingTransfer);
        }

    }
}