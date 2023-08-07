package com.lrpc;

import com.lrpc.transport.message.request.LRPCRequest;
public class Test {
    @org.junit.Test
    public void LRPCCMSSettingTest() {
        byte csmSetting = LRPCRequest.getCSMSetting(1, 1, 1);
        System.out.println(binaryValue(csmSetting));
    }

    private String binaryValue(byte value) {
        StringBuilder builder = new StringBuilder(10);
        for (int i = 7; i >= 0; i--) {
            builder.append((value >> i) & 1);
        }
        return builder.toString();
    }
}
