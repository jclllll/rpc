package com.lrpc.conf;

import com.lrpc.discovery.Registry;
import com.lrpc.proxy.ReferenceProxyHandler;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Proxy;
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
        ReferenceProxyHandler proxy = new ReferenceProxyHandler(registry, interfaceConsumer);
        //动态代理生成代理对象
        Object o = Proxy.newProxyInstance(classLoader, new Class[]{interfaceConsumer}, proxy);
        return (T) o;
    }

    public ReferenceConfig() {
    }
}
