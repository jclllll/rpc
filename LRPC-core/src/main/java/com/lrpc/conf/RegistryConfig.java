package com.lrpc.conf;

import com.lrpc.common.Constant;
import com.lrpc.common.exception.DiscoveryException;
import com.lrpc.discovery.Registry;
import com.lrpc.discovery.impl.NacosRegistry;
import com.lrpc.discovery.impl.ZookeeperRegistry;

public class RegistryConfig {
    public String getUrl() {
        return url;
    }

    private String host;
    private String type;
    private final String url;

    public RegistryConfig(String connectUrl) {
        url = connectUrl;
        getRegistryType();
    }

    public Registry getRegistry() {

        //获取注册中心类型
        if (type.equals("zookeeper")) {
            return new ZookeeperRegistry(host, Constant.TIME_OUT);
        }else if (type.equals("nacos")){
            return new NacosRegistry(host,Constant.TIME_OUT);
        }
        throw new DiscoveryException("not found dicovery");
    }

    private void getRegistryType() {
        String[] typeAndHost = url.split("://");
        if (typeAndHost.length != 2) {
            throw new RuntimeException("registry center url is not legal");
        }
        type = typeAndHost[0];
        host = typeAndHost[1];
    }
}
