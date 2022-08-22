package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;

public interface AccountService {

    Account getBalance(AuthenticatedUser authenticatedUser);

    Account getAccountByUserId(AuthenticatedUser authenticatedUser, int userId);

    Account getAccountById(AuthenticatedUser authenticatedUser, int accountId);

}
