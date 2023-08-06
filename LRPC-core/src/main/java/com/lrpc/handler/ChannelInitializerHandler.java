package com.lrpc.handler;

import com.lrpc.transport.message.LRPCMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class ChannelInitializerHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
            .addLast(new LoggingHandler(LogLevel.INFO))
            .addLast(new LRPCMessageEncoder());
    }
}
