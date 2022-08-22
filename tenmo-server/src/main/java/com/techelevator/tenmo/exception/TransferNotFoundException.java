package com.techelevator.tenmo.exception;

public class TransferNotFoundException extends Exception{
    public TransferNotFoundException(){super("That transfer doesn't seem to exist.");}
}
