package com.lrpc.transport.message.decoder;

import com.lrpc.transport.message.request.LRPCRequest;
import com.lrpc.transport.message.MessageFormatConstant;
import com.lrpc.transport.message.request.RequestPayload;
import com.lrpc.transport.message.serialize.impl.SerializeFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class LRPCMessageDecoder extends LengthFieldBasedFrameDecoder {
    public LRPCMessageDecoder() {
        //最大帧的长度，超过这个length会直接丢弃
        super(
            MessageFormatConstant.MAX_FRAME_LENGTH,
            //长度字段的偏移量
            MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.SHORT_LENGTH,
            //长度字段的长度
            MessageFormatConstant.INT_LENGTH,
            //负载的适配长度
            -(MessageFormatConstant.HEADER_LENGTH - MessageFormatConstant.BYTE_LENGTH - MessageFormatConstant.LONG_LENGTH),
            0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        Object obj = super.decode(ctx, in);
        if (obj instanceof ByteBuf) {
            return decodeFrame((ByteBuf) obj);
        }
        return null;
    }

    private Object decodeFrame(ByteBuf obj) {
        LRPCRequest.LRPCRequestBuilder builder = LRPCRequest.builder();
        byte[] magicByteBuf = new byte[MessageFormatConstant.MAGIC.length];
        obj.readBytes(magicByteBuf);
        //读取并比对magic值
        for (int i = 0; i < magicByteBuf.length; i++) {
            if (magicByteBuf[i] != MessageFormatConstant.MAGIC[i]) {
                throw new RuntimeException("magic 不正确");
            }
        }
        builder.magic(magicByteBuf);
        //读取并判断version是否支持
        byte version = obj.readByte();
        if (version > MessageFormatConstant.VERSION) {
            throw new RuntimeException("版本不支持");
        }
        builder.version(version);
        int headLength = obj.readShort();
        int fullLength = obj.readInt();
        byte compressSerializeMsg = obj.readByte();
        byte compress = (byte) (compressSerializeMsg >> 5);
        byte serialize = (byte) (0b111 & (compressSerializeMsg >> 2));
        byte msgType = (byte) (0b11 & compressSerializeMsg);
        long requestId = obj.readLong();
        builder.requestId(requestId);
        int bodyLength = fullLength - headLength;
        if (msgType == 0) {
            return builder.build();
        }
        byte[] bodyByte = new byte[bodyLength];
        obj.readBytes(bodyByte);
        //解压缩
        if (compress == 1) {

        }
        //反序列化
        RequestPayload payload = SerializeFactory.getSerialize((int) serialize).deSerialize(bodyByte);
        builder.compressSerializeMsgType(compressSerializeMsg);
        builder.payload(payload);
        return builder.build();
    }
}
