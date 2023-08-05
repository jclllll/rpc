package com.lrpc.handler;

import com.lrpc.LRPCBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
@Slf4j
public class MySimpleChannelInBoundHandler extends SimpleChannelInboundHandler<ByteBuf> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf msg) throws Exception {
        String result = msg.toString(StandardCharsets.UTF_8);
        LRPCBootstrap.getInstance().PENDING_REQUEST.get(1L).complete(result);
        log.info("服务端反馈：{}", result);
    }
}
