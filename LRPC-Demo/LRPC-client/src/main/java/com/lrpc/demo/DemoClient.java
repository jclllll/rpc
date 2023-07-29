package com.lrpc.demo;

import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ReferenceConfig;
import com.lrpc.conf.RegistryConfig;

public class DemoClient {

  public static void main(String[] args) {
    ReferenceConfig<DemoApiHello> reference = new ReferenceConfig<>();
    reference.setInterfaceConsumer(DemoApiHello.class);

    LRPCBootstrap.getInstance()
        .application("LRPC-consumer")
        .registry(new RegistryConfig("zookeeper://127.0.0.1:2181"))
        .reference(new ReferenceConfig());

    reference.get();
  }
}