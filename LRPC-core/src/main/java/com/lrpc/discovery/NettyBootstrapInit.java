package com.lrpc.discovery;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyBootstrapInit {
    private static final Bootstrap bootstrap = new Bootstrap();
    static {
        NioEventLoopGroup group=new NioEventLoopGroup();
        bootstrap.group(group)
            .channel(NioServerSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(null);
                }
            });
    }
    private NettyBootstrapInit(){}
    public static Bootstrap getBootstrap(){
        return bootstrap;
    }
}
