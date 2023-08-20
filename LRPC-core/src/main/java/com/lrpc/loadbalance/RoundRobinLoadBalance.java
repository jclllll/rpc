package com.lrpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.List;

public class RoundRobinLoadBalance extends AbstractLoadBalancer {
    public RoundRobinLoadBalance(List<InetSocketAddress>addresses){
        loadBalance(addresses);
    }
    private List<InetSocketAddress>addresses;
    private int num = 0;
    @Override
    protected void loadBalance(List<InetSocketAddress> addresses) {
        this.addresses = addresses;
    }

    @Override
    public synchronized InetSocketAddress choiceNode() {
        if (num == addresses.size()) {
            num = 0;
        }
        return addresses.get(num++);
    }
}
