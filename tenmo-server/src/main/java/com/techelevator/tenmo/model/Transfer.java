package com.techelevator.tenmo.model;

import org.springframework.data.annotation.Id;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Transfer {
    @Id
    private int transfer_id;
    @NotNull
    private int transfer_type_id;
    @NotNull
    private int transfer_status_id;
    @NotNull
    private int account_from;
    @NotNull
    private int account_to;
    @Min(value = 0, message = "Amount transferred must be greater than nothing")
    private BigDecimal amount;

    public Transfer(){
    }

    public Transfer(int transfer_id, int transfer_type_id, int transferStatusId, int fromAccountId,
                    int accountTo, BigDecimal amount){
        this.transfer_id = transfer_id;
        this.transfer_type_id = transfer_type_id;
        this.transfer_status_id = transferStatusId;
        this.account_from = fromAccountId;
        this.account_to = accountTo;
        this.amount = amount;
    }

    public int getTransfer_id() {
        return transfer_id;
    }

    public void setTransfer_id(int transfer_id) {
        this.transfer_id = transfer_id;
    }

    public int getTransfer_type_id() {
        return transfer_type_id;
    }

    public void setTransfer_type_id(int transfer_type_id) {
        this.transfer_type_id = transfer_type_id;
    }

    public int getTransfer_status_id() {
        return transfer_status_id;
    }

    public void setTransfer_status_id(int transfer_status_id) {
        this.transfer_status_id = transfer_status_id;
    }

    public int getAccount_from() {
        return account_from;
    }

    public void setAccount_from(int account_from) {
        this.account_from = account_from;
    }

    public int getAccount_to() {
        return account_to;
    }

    public void setAccount_to(int account_to) {
        this.account_to = account_to;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
