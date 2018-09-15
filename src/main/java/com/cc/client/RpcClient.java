package com.cc.client;

import com.cc.netty.RpcDecoder;
import com.cc.netty.RpcEncoder;
import com.cc.netty.RpcRequest;
import com.cc.netty.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.IOException;
import java.net.*;

public class RpcClient extends ChannelInboundHandlerAdapter{

    private Object o = new Object();

    private String host;
    private int port;
    private RpcResponse rpcResponse;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        this.rpcResponse = (RpcResponse) msg;
        synchronized (o){
            o.notifyAll();
        }
    }

    public RpcClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public RpcResponse start(RpcRequest request) throws IOException {
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            /**
                             *  1. 向 Server 端发送 RpcRequet 数据 -> 采用 RpcEncoder(RpcRequest.class) 将 发送数据序列化
                             *  5.  接收服务器端的 RpcResponse 数据 -> RpcDecoder(RpcResponse.class) 将数据反序列化
                             *  6.  调用 ClientHandler() 处理接收到的 RpcResponse 消息
                             */
                            sc.pipeline().addLast(new RpcEncoder(RpcRequest.class));   // outbound
                            sc.pipeline().addLast(new RpcDecoder(RpcResponse.class));   // inbound
                            sc.pipeline().addLast(RpcClient.this);   // inbound
                        }
                    });
            System.out.println(host +"   |   " + port);
            ChannelFuture cf = b.connect(host,port).sync();
            cf.channel().writeAndFlush(request);
            synchronized (o){
                o.wait();
            }
            if(rpcResponse!=null) {
                // 异步监听  channel 关闭
                cf.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
        return rpcResponse;
    }

}
