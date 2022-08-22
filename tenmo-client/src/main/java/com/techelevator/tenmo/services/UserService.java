package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;

public interface UserService {

    User[] getAllUsersExceptCurrent(AuthenticatedUser authenticatedUser, int id);
    User getUserByUserId(AuthenticatedUser authenticatedUser, int id);
}