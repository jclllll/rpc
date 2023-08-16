package com.lrpc.proxy;

import com.lrpc.IdGenerator;
import com.lrpc.LRPCBootstrap;
import com.lrpc.common.exception.NetworkException;
import com.lrpc.discovery.NettyBootstrapInit;
import com.lrpc.discovery.Registry;
import com.lrpc.loadbalance.AbstractLoadBalancer;
import com.lrpc.loadbalance.RoundRobinLoadBalance;
import com.lrpc.transport.message.request.LRPCRequest;
import com.lrpc.transport.message.MessageFormatConstant;
import com.lrpc.transport.message.request.RequestPayload;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.List;
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
        if(!LRPCBootstrap.getInstance().CACHE_SERVICE_LIST.containsKey(interfaceConsumer.getName())) {
            synchronized(this) {
                if (!LRPCBootstrap.getInstance().CACHE_SERVICE_LIST.containsKey(interfaceConsumer.getName())) {
                    LRPCBootstrap.getInstance().CACHE_SERVICE_LIST.put(interfaceConsumer.getName(), registry.lookup(interfaceConsumer.getName()));
                }
            }
        }
        List<InetSocketAddress> addresses = LRPCBootstrap.getInstance().CACHE_SERVICE_LIST.get(interfaceConsumer.getName());
        if(!LRPCBootstrap.getInstance().CACHE_INTERFACE_LOADBALANCE.containsKey(interfaceConsumer.getName())){
            synchronized (this) {
                if(!LRPCBootstrap.getInstance().CACHE_INTERFACE_LOADBALANCE.containsKey(interfaceConsumer.getName())) {
                    LRPCBootstrap.getInstance().CACHE_INTERFACE_LOADBALANCE.put(interfaceConsumer.getName(), new RoundRobinLoadBalance());
                }
            }
        }
        AbstractLoadBalancer balance=LRPCBootstrap.getInstance().CACHE_INTERFACE_LOADBALANCE.get(interfaceConsumer.getName());
        InetSocketAddress address=balance.choiceNode(addresses);
        log.info("discovery service {}", addresses);
        //封装一个请求
        RequestPayload payload = RequestPayload.builder()
            .interfaceName(interfaceConsumer.getName())
            .functionName(method.getName())
            .paramsType(method.getParameterTypes())
            .params(args)
            .returnType(method.getReturnType())
            .build();
        LRPCRequest request = LRPCRequest.builder()
            .requestId(IdGenerator.getUid())
            .version(MessageFormatConstant.VERSION)
            .magic(MessageFormatConstant.MAGIC)
            .compressSerializeMsgType(LRPCRequest.getCSMSetting(LRPCBootstrap.COMPRESS, LRPCBootstrap.SERIALIZE, 1))
            .payload(payload)
            .build();
        //从缓存中取得一个Channel
        Channel channel = getChannelFromCache(address);
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();
        LRPCBootstrap.getInstance().PENDING_REQUEST.put(request.getRequestId(), completableFuture);
        //将请求发出去
        channel.writeAndFlush(request).addListener((ChannelFutureListener) promise -> {
            if (!promise.isSuccess()) {
                completableFuture.completeExceptionally(promise.cause());
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
