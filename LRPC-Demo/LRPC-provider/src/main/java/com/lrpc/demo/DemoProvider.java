package com.lrpc.demo;

import com.alibaba.fastjson.JSON;
import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ProtocolConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.demo.impl.DemoApiHelloImpl;
import com.lrpc.transport.message.request.RequestPayload;
import com.lrpc.transport.message.serialize.Serialize;
import com.lrpc.transport.message.serialize.impl.SerializeFactory;

public class DemoProvider {
    public static int port=8083;
    public static void main(String[] args) {
        ServiceConfig<DemoApiHello> server = new ServiceConfig<>();
        server.setInterfaceProvider(DemoApiHello.class);
        server.setRes(new DemoApiHelloImpl());

        LRPCBootstrap.getInstance()
            .application("LRPC-provider")
            .port(port)
            .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
            .protocol(new ProtocolConfig("jdk"))

            .publish(server)
            .start();
    }

}
