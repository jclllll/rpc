package com.lrpc.demo.impl;

import com.lrpc.demo.DemoApiHello;

public class DemoApiHelloImpl implements DemoApiHello {

  @Override
  public String sayHello(String arg) {
    System.out.println("rpc get "+arg);
    return "rpc return "+arg;
  }
}
