package com.lrpc.transport.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestPayload implements Serializable {
    //接口名
    private String interfaceName;
    //方法名
    private String functionName;
    //参数类型
    private Class<?>[] paramsType;
    //参数列表
    private Object[] params;
    //返回值类型
    private Class<?> returnType;
}
