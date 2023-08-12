package com.lrpc.transport.message.serialize.exception;

public class SerializeException extends RuntimeException{
    public SerializeException(){
        super();
    }
    public SerializeException(String msg){
        super(msg);
    }
    public SerializeException(Throwable e){
        super(e);
    }
}
