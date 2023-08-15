package com.lrpc.demo;

import com.alibaba.fastjson.JSON;
import com.lrpc.IdGenerator;
import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ReferenceConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.transport.message.response.ResponsePayload;
import com.lrpc.transport.message.serialize.Serialize;
import com.lrpc.transport.message.serialize.impl.SerializeFactory;

public class DemoClient {
    static {
        IdGenerator.init(1, 1);
    }

    public static void main(String[] args) {
        ReferenceConfig<DemoApiHello> reference = new ReferenceConfig<>();
        reference.setInterfaceConsumer(DemoApiHello.class);
        LRPCBootstrap.getInstance()
            .application("LRPC-consumer")
            .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
            .serialize("hessian")
            .compress("snappY")
            .reference(reference);

        DemoApiHello demoApiHello = reference.get();
        String result = demoApiHello.sayHello("hhhhhhhh");
        System.out.println("返回值：" + result);

    }
}
