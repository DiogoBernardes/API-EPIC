package com.epic.finance.exception.account;

public class ExistingAccountNameException extends RuntimeException{
    public ExistingAccountNameException(String message){
        super(message);
    }
}
