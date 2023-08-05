package com.lrpc.common.exception;

public class RequestTypeException extends RuntimeException{
    public RequestTypeException(Throwable e){
        super(e);
    }
    public RequestTypeException(){
        super();
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
    public RequestTypeException(String msg){
        super(msg);
    }
}
