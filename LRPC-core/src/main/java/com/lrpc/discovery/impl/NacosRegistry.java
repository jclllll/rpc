package com.lrpc.discovery.impl;

import com.lrpc.common.Constant;
import com.lrpc.common.utils.net.NetUtils;
import com.lrpc.common.utils.zookeeper.ZookeeperNode;
import com.lrpc.common.utils.zookeeper.ZookeeperUtil;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.discovery.AbstractRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooKeeper;

import java.net.InetSocketAddress;

@Slf4j
public class NacosRegistry extends AbstractRegistry {
    public NacosRegistry(String connectString,int timeout){

    }
    @Override
    public void registry(ServiceConfig<?> server) {

    }

    @Override
    public InetSocketAddress lookup(String name) {
        return null;
    }
}
