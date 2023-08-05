package com.lrpc.proxy;

import com.lrpc.LRPCBootstrap;
import com.lrpc.common.exception.NetworkException;
import com.lrpc.discovery.NettyBootstrapInit;
import com.lrpc.discovery.Registry;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class ReferenceProxyHandler implements InvocationHandler {
    private final Registry registry;
    private final Class<?> interfaceConsumer;

    public ReferenceProxyHandler(Registry registry, Class<?> interfaceConsumer) {
        this.registry = registry;
        this.interfaceConsumer = interfaceConsumer;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        InetSocketAddress address = registry.lookup(interfaceConsumer.getName());
        log.info("discovery service {}", address);
        Channel channel = getChannelFromCache(address);
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        LRPCBootstrap.getInstance().PENDING_REQUEST.put(1L, completableFuture);
        channel.writeAndFlush(Unpooled.copiedBuffer(Arrays.toString(args).getBytes(StandardCharsets.UTF_8))).addListener((ChannelFutureListener) promise -> {
            if (!promise.isSuccess()) {
                completableFuture.completeExceptionally(promise.cause());
            }
        });
        return completableFuture.get(3, TimeUnit.SECONDS);
    }
    private Channel getChannelFromCache(InetSocketAddress address){
        Channel channel = LRPCBootstrap.getInstance().CHANNEL_MAP.get(address);
        if (channel == null) {
            CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
            NettyBootstrapInit.getBootstrap()
                .connect(address).addListener(
                    (ChannelFutureListener) promise -> {
                        if (promise.isSuccess()) {
                            log.info("connect {} is success", address);
                            completableFuture.complete(promise.channel());
                        } else if (!promise.isSuccess()) {
                            completableFuture.completeExceptionally(promise.cause());
                        }
                    });
            //阻塞获取
            try {
                channel = completableFuture.get(3, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                throw new RuntimeException(e);
            }
            LRPCBootstrap.getInstance().CHANNEL_MAP.put(address, channel);
        }
        if (channel == null) {
            log.error("can not get channel error:{}",address);
            throw new NetworkException();
        }
        return channel;
    }
}
