package com.lrpc.transport.message.compress;

public interface Compress {

    byte[] compress(byte[] arr);

    byte[] deCompress(byte[] arr);
}
