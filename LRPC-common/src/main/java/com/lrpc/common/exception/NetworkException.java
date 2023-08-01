package com.lrpc.common.exception;

public class NetworkException extends RuntimeException{
    public NetworkException(String message) {
        super(message);
    }

    public NetworkException(){}
}
