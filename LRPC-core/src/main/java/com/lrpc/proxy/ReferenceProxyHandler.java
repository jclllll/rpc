package com.lrpc.proxy;

import com.lrpc.LRPCBootstrap;
import com.lrpc.common.exception.NetworkException;
import com.lrpc.discovery.NettyBootstrapInit;
import com.lrpc.discovery.Registry;
import com.lrpc.transport.message.LRPCRequest;
import com.lrpc.transport.message.MessageFormatConstant;
import com.lrpc.transport.message.RequestPayload;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
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
        //封装一个请求
        RequestPayload payload = RequestPayload.builder()
            .interfaceName(interfaceConsumer.getName())
            .functionName(method.getName())
            .paramsType(method.getParameterTypes())
            .params(args)
            .returnType(method.getReturnType())
            .build();
        LRPCRequest request = LRPCRequest.builder()
            .requestId(1L)
            .version(MessageFormatConstant.VERSION)
            .magic(MessageFormatConstant.MAGIC)
            .compressSerializeMsgType(LRPCRequest.getCSMSetting(1, 1, 1))
            .payload(payload)
            .build();
        //从缓存中取得一个Channel
        Channel channel = getChannelFromCache(address);
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        LRPCBootstrap.getInstance().PENDING_REQUEST.put(1L, completableFuture);
        //将请求发出去
        channel.writeAndFlush(request).addListener((ChannelFutureListener) promise -> {
            if (!promise.isSuccess()) {
                completableFuture.completeExceptionally(promise.cause());
            }else {
                completableFuture.complete(promise.get());
            }
        });
        return completableFuture.get(3, TimeUnit.SECONDS);
    }

    private Channel getChannelFromCache(InetSocketAddress address) {
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
            log.error("can not get channel error:{}", address);
            throw new NetworkException();
        }
        return channel;
    }
}
