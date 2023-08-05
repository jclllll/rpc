package com.lrpc.conf;

import com.lrpc.LRPCBootstrap;
import com.lrpc.common.exception.NetworkException;
import com.lrpc.discovery.NettyBootstrapInit;
import com.lrpc.discovery.Registry;
import com.lrpc.proxy.ReferenceProxyHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
        ReferenceProxyHandler proxy=new ReferenceProxyHandler(registry,interfaceConsumer);
        //动态代理生成代理对象
        Object o = Proxy.newProxyInstance(classLoader, new Class[]{interfaceConsumer},proxy );
        return (T) o;
    }

    public ReferenceConfig() {
    }
}
