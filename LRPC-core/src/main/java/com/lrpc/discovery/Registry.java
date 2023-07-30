package com.lrpc.discovery;

import com.lrpc.conf.ServiceConfig;

public interface Registry {
    void registry(ServiceConfig<?> server);
}
