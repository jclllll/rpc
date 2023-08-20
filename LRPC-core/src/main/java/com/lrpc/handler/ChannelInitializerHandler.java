package com.lrpc.handler;

import com.lrpc.transport.message.decoder.LRPCResponseDecoder;
import com.lrpc.transport.message.encoder.LRPCMessageEncoder;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelInitializerHandler extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
//            .addLast(new LoggingHandler(LogLevel.INFO))
            .addLast(new LRPCMessageEncoder())
            .addLast(new LRPCResponseDecoder())
            .addLast(new MySimpleChannelInBoundHandler());

    }
}
