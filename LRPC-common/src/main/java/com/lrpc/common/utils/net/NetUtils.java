package com.lrpc.common.utils.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {
    public static String getLocalIp(){
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
