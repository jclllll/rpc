package com.lrpc.transport.message.serialize;

public interface Serialize {
    byte [] serialize(Object obj);

     <T> T deSerialize(byte []bytes);
}
