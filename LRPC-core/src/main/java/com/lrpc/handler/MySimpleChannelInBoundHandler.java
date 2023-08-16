package com.lrpc.handler;

import com.lrpc.LRPCBootstrap;
import com.lrpc.transport.message.response.LRPCResponse;
import com.lrpc.transport.message.response.ResponsePayload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j

public class MySimpleChannelInBoundHandler extends SimpleChannelInboundHandler<LRPCResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LRPCResponse response) throws Exception {
        ResponsePayload returnValue = (ResponsePayload)response.getPayload();
        LRPCBootstrap.getInstance().PENDING_REQUEST.get(response.getRequestId()).complete(returnValue.getPayload());
    }
}
