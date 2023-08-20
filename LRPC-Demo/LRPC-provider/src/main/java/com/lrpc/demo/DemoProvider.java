package com.lrpc.demo;

import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ProtocolConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.demo.impl.DemoApiHelloImpl;


public class DemoProvider {
    public static int port=8085;
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
