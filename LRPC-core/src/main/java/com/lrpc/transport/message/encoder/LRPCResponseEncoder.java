package com.lrpc.transport.message.encoder;

import com.lrpc.transport.message.compress.impl.CompressFactory;
import com.lrpc.transport.message.response.LRPCResponse;
import com.lrpc.transport.message.MessageFormatConstant;
import com.lrpc.transport.message.response.ResponsePayload;
import com.lrpc.transport.message.serialize.impl.SerializeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Slf4j
public class LRPCResponseEncoder extends MessageToByteEncoder<LRPCResponse> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LRPCResponse lrpcResponse, ByteBuf byteBuf) throws Exception {
        //ac765c9b魔术值
        byteBuf.writeBytes(lrpcResponse.getMagic());
        //version
        byteBuf.writeByte(lrpcResponse.getVersion());
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);
        //总长度,先空着
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);
        byteBuf.writeByte(lrpcResponse.getCompressSerializeMsgType());
        byteBuf.writeLong(lrpcResponse.getRequestId());
        //判断是否为心跳
        if ((lrpcResponse.getCompressSerializeMsgType() & 1) == 0) {
            //魔术值+version+head_length
            byteBuf.writerIndex(11);
            byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH);
            return;
        }
        //头写完了，该写body了
        byte[] body = SerializeFactory.getSerialize(
                (lrpcResponse.getCompressSerializeMsgType() >> 2) & 0b111)
            .serialize(lrpcResponse.getPayload());
        //序列化之后压缩
        body = CompressFactory.getCompress(
                lrpcResponse.getCompressSerializeMsgType() >> 5)
            .compress(body);
        //先把总长度写上
        int writerIndex = byteBuf.writerIndex();
        //魔术值+version+head_length
        byteBuf.writerIndex(11);
        byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH + body.length);
        byteBuf.writerIndex(writerIndex);
        byteBuf.writeBytes(body);
        channelHandlerContext.pipeline().writeAndFlush(byteBuf);
    }

    private byte[] getPayloadBytes(ResponsePayload o) {
        if (o == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            return baos.toByteArray();
        } catch (IOException e) {
            log.error("序列化失败：{},error:{}", o, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
