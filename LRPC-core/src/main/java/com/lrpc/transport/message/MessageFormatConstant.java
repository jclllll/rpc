package com.lrpc.transport.message;

import java.nio.charset.StandardCharsets;

public class MessageFormatConstant {
    public final static byte[] MAGIC = "ac765c9b".getBytes(StandardCharsets.UTF_8);
    public final static byte VERSION = 1;
    public final static short HEADER_LENGTH = (short) (MAGIC.length + 1 + 2 + 4 + 1 + 8);
}
