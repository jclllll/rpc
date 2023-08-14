package com.lrpc.transport.message.encoder;

import com.lrpc.transport.message.compress.impl.CompressFactory;
import com.lrpc.transport.message.request.LRPCRequest;
import com.lrpc.transport.message.MessageFormatConstant;
import com.lrpc.transport.message.request.RequestPayload;
import com.lrpc.transport.message.serialize.impl.SerializeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

@Slf4j
public class LRPCMessageEncoder extends MessageToByteEncoder<LRPCRequest> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, LRPCRequest lrpcRequest, ByteBuf byteBuf) throws Exception {
        //ac765c9b魔术值
        byteBuf.writeBytes(lrpcRequest.getMagic());
        //version
        byteBuf.writeByte(lrpcRequest.getVersion());
        byteBuf.writeShort(MessageFormatConstant.HEADER_LENGTH);
        //总长度,先空着
        byteBuf.writerIndex(byteBuf.writerIndex() + 4);
        byteBuf.writeByte(lrpcRequest.getCompressSerializeMsgType());
        byteBuf.writeLong(lrpcRequest.getRequestId());
        //判断是否为心跳
        if ((lrpcRequest.getCompressSerializeMsgType() & 1) == 0) {
            //魔术值+version+head_length
            byteBuf.writerIndex(11);
            byteBuf.writeInt(MessageFormatConstant.HEADER_LENGTH);
            return;
        }
        //头写完了，该写body了
        byte[] body = SerializeFactory.getSerialize(
                (lrpcRequest.getCompressSerializeMsgType() >> 2) & 0b111)
            .serialize(lrpcRequest.getPayload());
        //序列化完成之后就压缩
        body = CompressFactory.getCompress(
                (lrpcRequest.getCompressSerializeMsgType() >> 5) & 0b111)
            .compress(body);
        //先把总长度写上
        int writerIndex = byteBuf.writerIndex();
        //魔术值+version+head_length
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
