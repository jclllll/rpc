package com.lrpc.exception;

public class IdException extends RuntimeException{
    public IdException(){
        super();
    }
    public IdException(String msg){
        super(msg);
    }
    public IdException(Throwable e){
        super(e);
    }
}
