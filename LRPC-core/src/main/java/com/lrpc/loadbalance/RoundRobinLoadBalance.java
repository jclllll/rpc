package com.lrpc.loadbalance;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundRobinLoadBalance extends AbstractLoadBalancer {
    private int num = 0;
    public static Queue<String>queue=new LinkedList<>();
    @Override
    protected synchronized InetSocketAddress loadBalance(List<InetSocketAddress> addresses) {
        System.out.println(this.hashCode());
        if (num == addresses.size()) {
            num = 0;
        }
        RoundRobinLoadBalance.queue.add("thread:"+Thread.currentThread().getName()+" "+num);
        return addresses.get(num++);
    }
}
