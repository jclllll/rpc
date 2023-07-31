package com.lrpc.conf;

import com.lrpc.discovery.Registry;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

@Slf4j
public class ReferenceConfig<T> {
    private Class<T> interfaceConsumer;

    public Class<T> getInterfaceConsumer() {
        return interfaceConsumer;
    }

    public Registry registry;

    public void setInterfaceConsumer(Class<T> interfaceConsumer) {
        this.interfaceConsumer = interfaceConsumer;
    }

    public Registry getRegistry() {
        return registry;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    /**
     * @return 生成api接口的代理对象
     */
    public T get() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class[] classes = new Class[]{interfaceConsumer};
        //动态代理生成代理对象
        Object o = Proxy.newProxyInstance(classLoader, classes, (proxy, method, args) -> {
            log.info("method is {}", method);
            log.info("args is {}", args);
            //1、发现服务，从注册中心寻找可用的服务
            InetSocketAddress address = registry.lookup(interfaceConsumer.getName());
            log.info("discovery service {}",address);
            //2、使用netty连接服务器发送调用的服务的名字+方法名字+参数列表
            return null;
        });
        return (T) o;
    }

    public ReferenceConfig() {
    }
}
