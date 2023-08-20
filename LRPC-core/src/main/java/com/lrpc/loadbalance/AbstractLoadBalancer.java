package com.lrpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

abstract public class AbstractLoadBalancer {
	abstract protected void loadBalance(List<InetSocketAddress> addresses);

	abstract public InetSocketAddress choiceNode();
}
