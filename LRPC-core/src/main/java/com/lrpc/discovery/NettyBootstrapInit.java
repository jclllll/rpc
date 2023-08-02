package com.lrpc.discovery;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyBootstrapInit {
    private static final Bootstrap bootstrap = new Bootstrap();
    static {
        NioEventLoopGroup group=new NioEventLoopGroup();
        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    socketChannel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                        @Override
                        protected void  channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
                            log.info("msg->{}",msg);
                        }
                    });
                }
            });
    }
    private NettyBootstrapInit(){}
    public static Bootstrap getBootstrap(){
        return bootstrap;
    }
}
