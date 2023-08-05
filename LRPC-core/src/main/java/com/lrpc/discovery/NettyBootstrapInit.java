package com.lrpc.discovery;
import com.lrpc.handler.ChannelInitializerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class NettyBootstrapInit {
    private static final Bootstrap bootstrap = new Bootstrap();

    static {
        NioEventLoopGroup group = new NioEventLoopGroup();
        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializerHandler());
    }

    private NettyBootstrapInit() {
    }

    public static Bootstrap getBootstrap() {
        return bootstrap;
    }
}
