package com.lrpc.demo;

import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ProtocolConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.demo.impl.DemoApiHelloImpl;

public class DemoProducer {
    public static void main(String[] args) {
        ServiceConfig<DemoApiHello> server = new ServiceConfig<>();
        server.setInterfaceProvider(DemoApiHello.class);
        server.setRes(new DemoApiHelloImpl());

        LRPCBootstrap.getInstance()
            .application("LRPC-provider")
            .registry(new RegistryConfig("127.0.0.1:2181"))
            .protocol(new ProtocolConfig("jdk"))
            .port(8080)
            .publish(server)
            .start();
    }

}
