package com.techelevator.tenmo.controller;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.service.AccountAdjustmentService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

//You can keep transfers and accounts here
@CrossOrigin
@RestController
public class AccountManagementController {
    private AccountDao accountDao;
    private AccountAdjustmentService accountAdjustmentService;
    private Transfer transfer;
    private TransferDao transferDao;
    //needs response statuses, user.getId for validation

    public AccountManagementController(AccountDao accountDao,
                                       AccountAdjustmentService accountAdjustmentService,
                                       TransferDao transferDao) {
        this.accountDao = accountDao;
        this.accountAdjustmentService = accountAdjustmentService;
        this.transferDao = transferDao;
    }

    //Start of account methods
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "account/{account_id}", method = RequestMethod.GET)
    public Account getAccount(@PathVariable(name = "account_id") int id) {
        return accountDao.findByAccountId(id);
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(path = "account/user/{user_id}")
    public Account getAccountByUserId(@PathVariable(name = "user_id") int userId) {
        return accountDao.findByUserId(userId);
    }
    //End of account methods

    //Start of transfer methods
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "transfers/{transfer_id}", method = RequestMethod.GET)
    public Transfer getTransferById(@PathVariable(name = "transfer_id") long id) {
        return transferDao.findByTransferId(id);
    }

    //LINE 95 OF CLIENT APP. GOOD TO GO
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @RequestMapping(path = "transfers/user/{account_id}", method = RequestMethod.GET)
    public List<Transfer> findAllTransfersForUser(@PathVariable(name = "account_id") int accountId) {
        return transferDao.findAllTransfers(accountId);
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(path = "transfers")
    public List<Transfer> getAllTransfers() {
        return transferDao.findAll();
    }

    //don't think I need the path variable part, the transfer already has its id

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "transfers/save")
    public Transfer saveTransfer(@RequestBody Transfer ts) {
        if (ts.getTransfer_status_id() == 2) {
            accountAdjustmentService.processTransaction(ts.getAccount_to(), ts.getAccount_from(), ts.getAmount());
        }
        transferDao.saveTransfer(ts.getTransfer_id(), ts.getTransfer_type_id(), ts.getTransfer_status_id(),
                ts.getAccount_from(), ts.getAccount_to(), ts.getAmount());
        return ts;
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @PutMapping(path = "transfers/{transfer_id}")
    public Transfer updateTransfer(@Valid @RequestBody Transfer transfer) {
        if (transfer.getTransfer_status_id() == 2) {
            accountAdjustmentService.processTransaction(
                    transfer.getAccount_to(), transfer.getAccount_from(), transfer.getAmount());
        }
        transferDao.approveOrDeny(transfer.getTransfer_status_id(), transfer.getTransfer_id());
        return transfer;
    }

    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(path = "transfers/user/{user_id}/pending")
    public List<Transfer> getAllPendingTransfers(@PathVariable(name = "user_id") long id) {
        return transferDao.getAllPending(id);
    }
    //End of transfer methods

    @ResponseStatus(HttpStatus.ACCEPTED)
    @GetMapping(path = "transfers/testing/{account_id}")
    public List<Transfer> findAllTransfersForUserTestable(@PathVariable(name = "account_id") int accountId) {
        return transferDao.findAllTransfers(accountId);
    }
}

