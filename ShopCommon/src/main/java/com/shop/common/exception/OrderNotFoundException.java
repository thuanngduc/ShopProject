package com.shop.common.exception;

public class OrderNotFoundException extends Throwable {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
