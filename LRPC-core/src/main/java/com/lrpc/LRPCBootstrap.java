package com.lrpc;

import com.lrpc.common.utils.zookeeper.ZookeeperUtil;
import com.lrpc.conf.ProtocolConfig;
import com.lrpc.conf.ReferenceConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.discovery.Registry;
import com.lrpc.discovery.impl.ZookeeperRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.ZooKeeper;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LRPCBootstrap {
    private LRPCBootstrap() {
    }

    private String applicationName = "defaule";

    private RegistryConfig registryConfig;

    private ProtocolConfig protocolConfig;
    //注册中心
    private Registry registry;
    private int port;
    /**
     * 此处使用饿汉式单例
     */
    private static final LRPCBootstrap instance = new LRPCBootstrap();

    /**
     * 获取引导类对象
     *
     * @return
     */
    public static LRPCBootstrap getInstance() {
        return instance;
    }

    public LRPCBootstrap port(int port) {
        this.port = port;
        return this;
    }

    /**
     * 注册服务
     *
     * @param registry
     * @return
     */
    public LRPCBootstrap registry(RegistryConfig registry) {
        //使用Registry获取一个注册中心
        this.registry = registry.getRegistry();
        return this;
    }

    /**
     * 指定协议
     *
     * @param protocol
     * @return
     */
    public LRPCBootstrap protocol(ProtocolConfig protocol) {
        if (log.isDebugEnabled()) {
            log.debug("protocol config {} for serializable", protocol.toString());
        }
        this.protocolConfig = protocol;
        return this;
    }

    /**
     * 封装需要发布的服务,将发布的服务接口注册到注册中心
     *
     * @param service
     * @return
     */
    public LRPCBootstrap publish(ServiceConfig<?> service) {
        registry.registry(service);
        return this;
    }

    /**
     * 批量发布
     *
     * @param services
     * @return
     */
    public LRPCBootstrap publish(List<ServiceConfig<?>> services) {
        return this;
    }

    /**
     * 启动netty服务
     */
    public void start() {
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public LRPCBootstrap application(String name) {
        applicationName = name;
        return this;
    }

    public LRPCBootstrap reference(ReferenceConfig reference) {

        return this;
    }
}
