package com.techelevator.tenmo.service;


import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
@Transactional
public class AccountAdjustmentService {
    BigDecimal zero = BigDecimal.valueOf(0.00);
    private AccountDao accountDao;

    public AccountAdjustmentService(AccountDao accountDao){
        this.accountDao = accountDao;
    }

    //For some reason the integers being passed in here don't lead to an account
    public void processTransaction(int toAccountId, int fromAccountId, BigDecimal amount){
        Account toAccount = accountDao.findByAccountId(toAccountId);
        Account fromAccount = accountDao.findByAccountId(fromAccountId);
        if(!(verifyFunds(fromAccount, amount))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient funds.");
            //return a bad request message saying they didn't have the funds.
        }
        if(!(verifyAskingAmount(amount))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must ask for an amount greater than zero.");
            //return a bad request message saying they can't ask for a zero or negative amount.
        }
        BigDecimal newBalance = toAccount.getBalance().add(amount);
        toAccount.setBalance(newBalance);
        accountDao.updateBalance(toAccount, toAccount.getUserId());
        newBalance = fromAccount.getBalance().subtract(amount);
        fromAccount.setBalance(newBalance);

        accountDao.updateDown(fromAccount, fromAccount.getUserId());
    }

    private boolean verifyFunds(Account account, BigDecimal amount){
        return account.getBalance().compareTo(amount) >= 0;
    }

    private boolean verifyAskingAmount(BigDecimal amount){
        return amount.compareTo(zero) > 0;
    }

}
