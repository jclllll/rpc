package com.lrpc;

import com.lrpc.exception.IdException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class IdGenerator {
    private static long lastTime = -1;
    private static long currentTime = 0;
    private static long seq = 0;
    private static int myRoom;
    private static int myMachine;

    public static void init(int room, int machine) {
        myRoom = room;
        myMachine = machine;
    }

    public static long getUid() {
        if (myRoom == 0 || myMachine == 0) {
            throw new IdException("没有绑定所在机房及机器号");
        }
        return getUid(myRoom,myMachine);
    }

    synchronized public static long getUid(int room, int machine) {
        if (room > GeneratorConstant.MAX_ROOM || machine > GeneratorConstant.MAX_MACHINE) {
            throw new IdException("机房或机器超过最大限制");
        }
        currentTime = getTime();
        if (currentTime < lastTime) {
            if (lastTime - currentTime >= 1000000) {
                throw new IdException("时钟回拨时间过长");
            } else {
                while (currentTime < lastTime) {
                    currentTime = getTime();
                }
                seq = 0;
            }
        }
        long id = 0;
        if (currentTime == lastTime) {
            if (seq >= GeneratorConstant.MAX_SEQ) {
                while (currentTime == lastTime) {
                    currentTime = getTime();
                }
                seq = 0;
            }
        } else {
            seq = 0;
        }
        id = (currentTime << GeneratorConstant.TIME_STAMP_LEFT_MOVE);
        id |= ((long) room << GeneratorConstant.ROOM_LEFT_MOVE);
        id |= ((long) machine << GeneratorConstant.MACHINE_LEFT_MOVE);
        id |= seq++;
        lastTime = currentTime;
        return id;
    }

    static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static long startTime;


    static {
        try {
            startTime = simpleDateFormat.parse("2023-08-09 00:00:00").getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static long getTime() {
        return System.currentTimeMillis() - startTime;
    }

    public static void print(int num) {
        for (int i = 31; i >= 0; i--) {
            if (((num >> i) & 1) == 1) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
        }
        System.out.println();
    }
    public static void print(long num) {
        for (int i = 63; i >= 0; i--) {
            if (((num >> i) & 1) == 1) {
                System.out.print("1");
            } else {
                System.out.print("0");
            }
        }
        System.out.println();
    }
}
