package com.lrpc.discovery;

import com.lrpc.conf.ServiceConfig;
import org.apache.zookeeper.server.ServerConfig;

public interface Registry {
    void registry(ServiceConfig<?> server);
}
