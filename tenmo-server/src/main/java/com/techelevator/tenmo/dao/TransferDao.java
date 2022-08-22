package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {
    Transfer findByTransferId(long id);
    //Double check, I think with save I need to put the full Transfer in
    boolean saveTransfer(int id, int type, int status, int from, int to, BigDecimal amount);
    boolean approveOrDeny(long id, long transferId);
    List<Transfer> findAllRequests(long id, long type);
    List<Transfer> findAllSent(long id, long type);
    List<Transfer> findAllTransfers(int userId);
    public List<Transfer> getAllPending(long id);
    List<Transfer> findAll();


}
