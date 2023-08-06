package com.lrpc.handler;

import com.lrpc.LRPCBootstrap;
import com.lrpc.conf.ServiceConfig;
import com.lrpc.transport.message.LRPCRequest;
import com.lrpc.transport.message.RequestPayload;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
@Slf4j
public class ServiceInvokeHandler extends SimpleChannelInboundHandler<LRPCRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, LRPCRequest lrpcRequest) throws Exception {
        //先获取负载内容
        RequestPayload payload = lrpcRequest.getPayload();
        //根据负载内容调用方法
        Object returnValue=invoke(payload);
        //封装相应

        //写出相应
        channelHandlerContext.channel().writeAndFlush(returnValue);
    }

    private Object invoke(RequestPayload payload) {
        Object returnValue=null;
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
            log.error("方法调用失败:{}",e.getMessage());
        }
        return null;
    }
}