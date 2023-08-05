package com.lrpc.transport.message;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LRPCRequest{
    // 请求id
    private long requestId;
    // 压缩类型（3bit）、序列化方式（3bit）、消息类型（2bit）
    private byte compressSerializeMsgType;
    //负载payLoad
    private RequestPayload payload;
    //如果用户要自定义压缩方法和序列化方式
}
