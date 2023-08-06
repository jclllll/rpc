package com.lrpc.transport.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class LRPCMessageDecoder extends LengthFieldBasedFrameDecoder {
    public LRPCMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        //最大帧的长度，超过这个length会直接丢弃
        super(
            MessageFormatConstant.MAX_FRAME_LENGTH,
            //长度字段的偏移量
            MessageFormatConstant.MAGIC.length + MessageFormatConstant.VERSION_LENGTH + MessageFormatConstant.SHORT_LENGTH,
            //长度字段的长度
            MessageFormatConstant.INT_LENGTH,
            //负载的适配长度
            MessageFormatConstant.HEADER_LENGTH,
            0);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        return super.decode(ctx, in);
    }
}
