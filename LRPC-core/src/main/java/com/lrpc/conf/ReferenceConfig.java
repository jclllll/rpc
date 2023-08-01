package com.lrpc.conf;

import com.lrpc.LRPCBootstrap;
import com.lrpc.common.exception.NetworkException;
import com.lrpc.discovery.NettyBootstrapInit;
import com.lrpc.discovery.Registry;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
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
        Class[] classes = new Class[]{interfaceConsumer};
        //动态代理生成代理对象
        Object o = Proxy.newProxyInstance(classLoader, classes, (proxy, method, args) -> {
            log.info("method is {}", method);
            log.info("args is {}", args);
            //1、发现服务，从注册中心寻找可用的服务
            InetSocketAddress address = registry.lookup(interfaceConsumer.getName());
            log.info("discovery service {}", address);
            //2、使用netty连接服务器发送调用的服务的名字+方法名字+参数列表
            //判断缓存有无
            Channel channel = LRPCBootstrap.getInstance().CHANNEL_MAP.get(address);
            if (channel == null) {
                //缓存没有
                //同步
                //channel = NettyBootstrapInit.getBootstrap
                //  .connect(address).await().channel();
                CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
                NettyBootstrapInit.getBootstrap()
                    .connect(address).addListener(
                        (ChannelFutureListener) promise -> {
                            if (promise.isDone()) {
                                log.info("connect {} is success", address);
                                completableFuture.complete(promise.channel());
                            } else if (!promise.isSuccess()) {
                                completableFuture.completeExceptionally(promise.cause());
                            }
                        });
                //阻塞获取
                channel = completableFuture.get(3, TimeUnit.SECONDS);
                LRPCBootstrap.getInstance().CHANNEL_MAP.put(address, channel);
            }
            if (channel == null) {
                log.error("can not get channel error");
                throw new NetworkException("can ");
            }
            //同步策略
//            ChannelFuture channelFuture = channel.writeAndFlush(new Object());
//            if (channelFuture.isDone()) {
//                Object object=channelFuture.getNow();
//            } else if (!channelFuture.isSuccess()) {
//                //捕获异步任务的异常
//                Throwable cause = channelFuture.cause();
//                throw new RuntimeException(cause);
//            }

            //异步策略
            CompletableFuture<Object> completableFuture=new CompletableFuture<>();
            channel.writeAndFlush(new Object()).addListener((ChannelFutureListener) promise -> {
                if(promise.isDone()){
                    completableFuture.complete(promise.getNow());
                }else if(!promise.isSuccess()){
                    completableFuture.completeExceptionally(promise.cause());
                }
            });

            return completableFuture.get(3,TimeUnit.SECONDS);
        });
        return (T) o;
    }

    public ReferenceConfig() {
    }
}
