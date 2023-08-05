package com.lrpc.transport.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestPayload {
    //接口名
    private String interfaceName;
    //方法名
    private String functionName;
    //参数类型
    private Class<?>[] paramsType;
    //参数列表
    private Object[] params;
    //返回值类型
    private Class<?>returnType;
}
