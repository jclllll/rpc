package com.lrpc.transport.message.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponsePayload implements Serializable {
    private byte code;
    private Object payload;
    private String msg;
}
