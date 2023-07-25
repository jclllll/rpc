package org.example;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.nio.charset.StandardCharsets;

public class NettyServer {
  private final int port;

  public NettyServer(int port) {
    this.port = port;
  }

  public void start(){
    EventLoopGroup boss=new NioEventLoopGroup();
    EventLoopGroup worker=new NioEventLoopGroup();
    ServerBootstrap serverBootstrap=new ServerBootstrap()
        .group(boss,worker)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {

          @Override
          protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new MyHandler());
          }
        });
    System.out.println("fuck server ");
    try {
      ChannelFuture channelFuture=serverBootstrap.bind(port).sync();
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }finally {
      try {
        boss.shutdownGracefully().sync();
        worker.shutdownGracefully();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }


  public static void main(String[] args) {
    new NettyServer(8080).start();
  }

}
class MyHandler extends ChannelInboundHandlerAdapter{

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    ByteBuf byteBuf=(ByteBuf) msg;
    System.out.println("server get mssage："+byteBuf.toString(StandardCharsets.UTF_8));
    ctx.channel().writeAndFlush(Unpooled.copiedBuffer("hello I fuck you".getBytes(StandardCharsets.UTF_8))).sync();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}