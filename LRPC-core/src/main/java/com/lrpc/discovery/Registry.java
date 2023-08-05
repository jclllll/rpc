package com.lrpc.discovery;

import com.lrpc.conf.ServiceConfig;

import java.net.InetSocketAddress;

public interface Registry {
    /**
     * 注册服务
     *
     * @param server 注册的服务的配置
     */
    void registry(ServiceConfig<?> server);

    /**
     * @param name 服务的名称
     * @return 服务的ip+port
     */
    InetSocketAddress lookup(String name);
}
