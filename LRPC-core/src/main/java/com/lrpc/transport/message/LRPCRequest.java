package com.lrpc.transport.message;


import com.lrpc.common.exception.RequestTypeException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Slf4j
public class LRPCRequest {
    public static byte getCSMSetting(int compress, int serialize, int msgType) {
        if (compress > 7 || serialize > 7 || msgType > 3) {
            log.error("compress:{} serialize:{} msgType{}", compress, serialize, msgType);
            throw new RequestTypeException("compress or serialize > 7  or  msgType >3");
        }
        return (byte) ((compress << 5) | (serialize << 2) | msgType);
    }

    // 请求id
    private long requestId;
    // 压缩类型（3bit）、序列化方式（3bit）、消息类型（2bit）
    private byte compressSerializeMsgType;
    //负载payLoad
    private RequestPayload payload;
    //如果用户要自定义压缩方法和序列化方式
}
