package com.lrpc;

import com.lrpc.conf.ProtocolConfig;
import com.lrpc.conf.ReferenceConfig;
import com.lrpc.conf.RegistryConfig;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.discovery.Registry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class LRPCBootstrap {
    private LRPCBootstrap() {
    }

    private String applicationName = "default";


    private ProtocolConfig protocolConfig;
    //注册中心
    private Registry registry;
    private final Map<String,ServiceConfig<?>> SERVICE_MAP=new ConcurrentHashMap<>(16);
    public final Map<InetSocketAddress, Channel>CHANNEL_MAP=new ConcurrentHashMap<>(16);
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
        SERVICE_MAP.put(service.getInterfaceProvider().getName(),service);
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
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        EventLoopGroup boss=new NioEventLoopGroup();
        EventLoopGroup worker=new NioEventLoopGroup();

        serverBootstrap.group(boss,worker)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(null);
                }
            });
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
