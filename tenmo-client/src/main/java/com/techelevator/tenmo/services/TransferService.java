package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferService {

    void createTransfer(AuthenticatedUser authenticatedUser, Transfer transfer);
    Transfer[] getTransfersFromAccountId(AuthenticatedUser authenticatedUser, int userId);
    Transfer getTransferFromTransferId(AuthenticatedUser authenticatedUser, int id);
    Transfer[] getAllTransfers(AuthenticatedUser authenticatedUser);
    Transfer[] getPendingTransfersByUserId(AuthenticatedUser authenticatedUser, int id);
    void updateTransfer(AuthenticatedUser authenticatedUser, Transfer transfer);


}
