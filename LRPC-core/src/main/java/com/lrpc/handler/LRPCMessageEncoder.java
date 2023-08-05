package com.lrpc.handler;

import com.lrpc.transport.message.LRPCRequest;
import com.lrpc.transport.message.MessageFormatConstant;
import com.lrpc.transport.message.RequestPayload;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;

@Slf4j
public class LRPCMessageEncoder extends MessageToByteEncoder<LRPCRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LRPCRequest lrpcRequest, ByteBuf byteBuf) throws Exception {
        //ac765c9b魔术值
        byteBuf.writeBytes("ac765c9b".getBytes(StandardCharsets.UTF_8));
        //version
        byteBuf.writeByte(MessageFormatConstant.VERSION);
        byteBuf.writeByte(MessageFormatConstant.HEADER_LENGTH);
        //总长度,先空着
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);
        byteBuf.writeByte(lrpcRequest.getCompressSerializeMsgType());
        byteBuf.writeLong(lrpcRequest.getRequestId());
        //头写完了，该写body了
        byte[] body = getPayloadBytes(lrpcRequest.getPayload());
        //先把总长度写上
        int writerIndex = byteBuf.writerIndex();
        byteBuf.writerIndex(11);
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + body.length);
        byteBuf.writerIndex(writerIndex);
        byteBuf.writeBytes(body);
    }

    private byte[] getPayloadBytes(RequestPayload payload) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(payload);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("序列化异常");
            throw new RuntimeException(e);
        }
    }
}
