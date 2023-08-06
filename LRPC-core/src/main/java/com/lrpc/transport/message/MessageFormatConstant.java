package com.lrpc.transport.message;

import java.nio.charset.StandardCharsets;

public class MessageFormatConstant {
    public final static byte[] MAGIC = "ac765c9b".getBytes(StandardCharsets.UTF_8);
    public final static byte VERSION = 1;
    public final static short HEADER_LENGTH = (short) (MAGIC.length + 1 + 2 + 4 + 1 + 8);

    public final static int MAX_FRAME_LENGTH = 1024 * 1024;

    public final static int VERSION_LENGTH = 1;

    public final static int SHORT_LENGTH = 2;
    public final static int INT_LENGTH = 4;
    public final static int LONG_LENGTH = 8;
    public final static int BYTE_LENGTH = 1;
}
