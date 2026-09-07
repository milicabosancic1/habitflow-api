package com.habitflow.api.exception;

public class GatewayTimeoutException extends RuntimeException {
    public GatewayTimeoutException(String message) { super(message); }
}
