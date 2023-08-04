package com.lrpc.discovery;

import com.lrpc.LRPCBootstrap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

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
                            String result =msg.toString(StandardCharsets.UTF_8);
                            LRPCBootstrap.getInstance().PENDING_REQUEST.get(1L).complete(result);
                            log.info("服务端反馈：{}",result);
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
