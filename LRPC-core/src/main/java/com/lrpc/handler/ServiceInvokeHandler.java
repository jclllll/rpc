package com.lrpc.handler;

import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.transport.message.request.LRPCRequest;
import com.lrpc.transport.message.request.RequestPayload;
import com.lrpc.transport.message.response.LRPCResponse;
import com.lrpc.transport.message.response.ResponsePayload;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

@Slf4j
public class ServiceInvokeHandler extends SimpleChannelInboundHandler<LRPCRequest> {
	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, LRPCRequest lrpcRequest) throws Exception {
		//先获取负载内容
		RequestPayload payload = lrpcRequest.getPayload();
		//根据负载内容调用方法
		ResponsePayload.ResponsePayloadBuilder builder = ResponsePayload.builder();
		//封装相应
		try {
			Object returnValue = invoke(payload);
			builder.payload(returnValue);
			builder.code((byte) 0);
		} catch (Exception e) {
			builder.code((byte) 1);
			builder.msg(e.getMessage());
			log.error("{} invoke error:{}", payload, e.getMessage());
		}
		//封装响应
		LRPCResponse.LRPCResponseBuilder LRPCResponseBuilder = LRPCResponse.builder();
		LRPCResponseBuilder.magic(lrpcRequest.getMagic());
		LRPCResponseBuilder.compressSerializeMsgType(lrpcRequest.getCompressSerializeMsgType());
		LRPCResponseBuilder.version(lrpcRequest.getVersion());
		LRPCResponseBuilder.payload(builder.build());
		LRPCResponseBuilder.requestId(lrpcRequest.getRequestId());
		//写出相应
		channelHandlerContext.pipeline().channel().writeAndFlush(LRPCResponseBuilder.build());
	}

	private Object invoke(RequestPayload payload) {
		Object returnValue = null;
		try {
			String interfaceName = payload.getInterfaceName();
			Class<?>[] paramsType = payload.getParamsType();
			String functionName = payload.getFunctionName();
			Object[] params = payload.getParams();
			//通过反射调用类的的方法
			ServiceConfig<?> serviceConfig = LRPCBootstrap.getInstance().SERVICE_MAP.get(interfaceName);
			Object res = serviceConfig.getRes();
			Class<?> aClass = res.getClass();
			Method method = aClass.getMethod(functionName, paramsType);
			returnValue = method.invoke(res, params);
			return returnValue;
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			log.error("方法调用失败:{}", e.getMessage());
			throw new RuntimeException(e);
		}
	}
}