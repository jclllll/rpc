package com.lrpc.demo.impl;

import com.lrpc.demo.DemoApiHello;
import com.lrpc.demo.DemoProvider;

public class DemoApiHelloImpl implements DemoApiHello {

	@Override
	public String sayHello(String arg) {
		System.out.println("rpc get " + arg);

		return String.valueOf(DemoProvider.port);
	}
}
