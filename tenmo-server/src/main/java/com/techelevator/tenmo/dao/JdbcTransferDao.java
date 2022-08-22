package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;

    private List<Transfer> list = new ArrayList<>();

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate =jdbcTemplate;
    }

    @Override
    public Transfer findByTransferId(long id) {
        String sql = "Select * from transfer where transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        if (results.next()) {
            return mapToTransfer(results);
        }
        return null;
    }

    //The issue is I'm using the user id and not the account id
    @Override
    public List<Transfer> findAllTransfers(int accountId){
        list.clear();
        String sql = "SELECT * from transfer where account_from = ? or account_to = ?";
        int id = accountId;
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId, id);
        while (results.next()) {
            list.add(mapToTransfer(results));
        }
        return list;
    }

    @Override
    public boolean saveTransfer(int id, int type, int status, int fromAccount, int toAccount, BigDecimal amount) {
        String sql = "INSERT INTO transfer (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (?,?,?,?,?,?)";
        jdbcTemplate.update(sql, id, type, status, fromAccount, toAccount, amount);
        return true;
    }

    @Override
    public boolean approveOrDeny(long statusId, long transferId) {
        String sql = "UPDATE transfer SET transfer_status_id = ? WHERE transfer_id = ?";
        return jdbcTemplate.update(sql, statusId, transferId) == 1;
    }

    @Override
    public List<Transfer> findAllRequests(long id, long type) {
        list.clear();
        String sql = "Select * from transfer where account_to = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Transfer transfer = mapToTransfer(results);
            list.add(transfer);
        }
        return list;
    }

    //Consolidate with findAllRequests
    @Override
    public List<Transfer> findAllSent(long id, long type) {
        list.clear();
        String sql = "Select * from transfer where account_from = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Transfer transfer = mapToTransfer(results);
            list.add(transfer);
        }
        return list;
    }

    @Override
    public List<Transfer> getAllPending(long id){
        list.clear();
        String sql = "SELECT * from transfer where account_from = ? and transfer_status_id = 1";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, id);
        while (results.next()){
            Transfer transfer = mapToTransfer(results);
            list.add(transfer);
        }
        return list;
    }

    @Override
    public List<Transfer> findAll() {
        list.clear();
        String sql = "Select * from transfer";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()){
            Transfer transfer = mapToTransfer(results);
            list.add(transfer);
        }
        return list;
    }

    private Transfer mapToTransfer(SqlRowSet results) {
        Transfer transfer = new Transfer();
        transfer.setTransfer_id(results.getInt("transfer_id"));
        transfer.setTransfer_type_id(results.getInt("transfer_type_id"));
        transfer.setTransfer_status_id(results.getInt("transfer_status_id"));
        transfer.setAccount_from(results.getInt("account_from"));
        transfer.setAccount_to(results.getInt("account_to"));
        transfer.setAmount(results.getBigDecimal("amount"));
        return transfer;
    }

}
