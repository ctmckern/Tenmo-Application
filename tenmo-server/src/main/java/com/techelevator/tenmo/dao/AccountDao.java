package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

public interface AccountDao {
    Account findByUserId(int id);

    boolean updateBalance(Account adjustedAccount, int id);

    boolean updateDown(Account adjustedAccount, int id);

    Account findByAccountId(int id);
}
