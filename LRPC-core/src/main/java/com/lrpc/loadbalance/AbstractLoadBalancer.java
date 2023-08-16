package com.lrpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

abstract public class AbstractLoadBalancer {
    abstract protected InetSocketAddress loadBalance(List<InetSocketAddress>addresses);

    public InetSocketAddress choiceNode(List<InetSocketAddress> addresses){
        return loadBalance(addresses);
    }
}
