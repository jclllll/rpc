package com.lrpc;

import com.lrpc.exception.IdException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class IdGenerator {


    private static long lastTime = -1;
    private static long currentTime = 0;
    private static int seq = 0;

    public static long getUid(int room, int machine) {
        if(room>GeneratorConstant.MAX_ROOM || machine>GeneratorConstant.MAX_MACHINE){
            throw new IdException("机房或机器超过最大限制");
        }
        currentTime = getTime();
        long id = (currentTime << GeneratorConstant.TIME_STAMP_LEFT_MOVE);
        id |= id | (room << GeneratorConstant.ROOM_LEFT_MOVE);
        id |= id | (machine << GeneratorConstant.MACHINE_LEFT_MOVE);
        id |= id | seq;
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

    public static void main(String[] args) throws ParseException {
    }
    public static void print(int num){
        for(int i=31;i>=0;i--){
            if(((num>>i)&1)==1){
                System.out.print("1");
            }
            else{
                System.out.print("0");
            }
        }
        System.out.println();
    }
    public static void print(long num){
        for(int i=63;i>=0;i--){
            if(((num>>i)&1)==1){
                System.out.print("1");
            }
            else{
                System.out.print("0");
            }
        }
        System.out.println();
    }
}
