package com.lrpc;

import com.lrpc.conf.ProtocolConfig;
import com.lrpc.conf.ReferenceConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.discovery.Registry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LRPCBootstrap {
    private LRPCBootstrap() {
    }

    private String applicationName = "default";


    private ProtocolConfig protocolConfig;
    //注册中心
    private Registry registry;
    private final Map<String, ServiceConfig<?>> SERVICE_MAP = new ConcurrentHashMap<>(16);
    public final Map<InetSocketAddress, Channel> CHANNEL_MAP = new ConcurrentHashMap<>(16);
    public final Map<Long, CompletableFuture<Object>> PENDING_REQUEST = new ConcurrentHashMap<>(128);
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

    public int getPort() {
        return port;
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
        SERVICE_MAP.put(service.getInterfaceProvider().getName(), service);
        return this;
    }

    /**
     * 批量发布
     *
     * @param services
     * @return
     */
    public LRPCBootstrap publish(List<ServiceConfig<?>> services) {
        services.forEach(this::publish);
        return this;
    }

    /**
     * 启动netty服务
     */
    public void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        EventLoopGroup boss = new NioEventLoopGroup(2);
        EventLoopGroup worker = new NioEventLoopGroup(10);
        try {
            serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new SimpleChannelInboundHandler<ByteBuf>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                        log.info("服务端收到信息：{}", byteBuf);
                        channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer(byteBuf));
                    }
                })
                .bind(port).sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            TimeUnit.SECONDS.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public LRPCBootstrap application(String name) {
        applicationName = name;
        return this;
    }

    public LRPCBootstrap reference(ReferenceConfig reference) {
        reference.setRegistry(registry);
        return this;
    }
}
