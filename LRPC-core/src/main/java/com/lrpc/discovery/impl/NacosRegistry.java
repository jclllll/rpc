package com.lrpc.discovery.impl;

import com.lrpc.conf.ServiceConfig;
import com.lrpc.discovery.AbstractRegistry;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class NacosRegistry extends AbstractRegistry {
    public NacosRegistry(String connectString, int timeout) {

    }

    @Override
    public void registry(ServiceConfig<?> server) {

    }

    @Override
    public InetSocketAddress lookup(String name) {
        return null;
    }
}
