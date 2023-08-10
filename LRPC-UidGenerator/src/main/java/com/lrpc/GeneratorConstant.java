package com.lrpc;

public class GeneratorConstant {
    //    唯一id 结构
    //    0                00000000000000000000000000000000000000000  00000     00000       000000000000
    //  保留，使生成的id>0          41位  时间戳(41)                      机房(5)    机器(5)        序列号(12)
    public static int SEQ_LENGTH = 12;
    public static int MACHINE_LENGTH = 5;
    public static int ROOM_LENGTH = 5;
    public static int TIME_LENGTH = 41;

    public static int MACHINE_LEFT_MOVE = SEQ_LENGTH;
    public static int ROOM_LEFT_MOVE = MACHINE_LEFT_MOVE + MACHINE_LENGTH;
    public static int TIME_STAMP_LEFT_MOVE = ROOM_LEFT_MOVE + ROOM_LENGTH;

    public static int MAX_SEQ = ~(-1 << SEQ_LENGTH);
    public static int MAX_MACHINE = ~(-1 << MACHINE_LENGTH);
    public static int MAX_ROOM = ~(-1 << ROOM_LENGTH);
    public static long MAX_TIME = ~(-1L << TIME_LENGTH);
}
