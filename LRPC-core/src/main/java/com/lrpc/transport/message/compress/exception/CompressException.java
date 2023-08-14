package com.lrpc.transport.message.compress.exception;

public class CompressException extends RuntimeException{
    public CompressException(){
        super();
    }
    public CompressException(Throwable e){
        super(e);
    }
    public CompressException(String msg){
        super(msg);
    }
}
