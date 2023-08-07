package com.lrpc.transport.message.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
@Data
@Builder
public class ResponsePayload implements Serializable {
    private byte code;
    private Object payload;
    private String msg;
}
