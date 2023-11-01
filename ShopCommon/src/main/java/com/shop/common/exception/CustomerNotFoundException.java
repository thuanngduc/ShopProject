package com.shop.common.exception;

public class CustomerNotFoundException extends Throwable{
    public CustomerNotFoundException(String message)
    {
        super(message);
    }
}
