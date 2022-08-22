package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class JdbcAccountDao implements AccountDao{

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account findByUserId(int id) {
        String sql = "Select * from account where user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()){
           return mapToAccount(results);
        }
        return null;
    }

    @Override
    public Account findByAccountId(int accountId){
        String sql = "Select * from account where account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if(results.next()){
            return mapToAccount(results);
        }
        return null;
    }

    @Override
    public boolean updateBalance(Account adjustedAccount, int id) {
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, adjustedAccount.getBalance(), id) == 1;
    }


    //Don't think I need this, will double check where the rollback occurs and
    //delete if necessary.
    @Override
    public boolean updateDown(Account adjustedAccount, int id) {
        String sql = "UPDATE account SET balance = ? WHERE user_id = ?";
        return jdbcTemplate.update(sql, adjustedAccount.getBalance(), id) == 1;
    }

    private Account mapToAccount(SqlRowSet results){
        Account account = new Account(results.getInt("account_id"), results.getInt("user_id"),
                results.getBigDecimal("balance"));
        return account;
    }

}
