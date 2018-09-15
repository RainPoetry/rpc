package com.cc.server;


import com.cc.netty.RpcRequest;
import com.cc.netty.RpcResponse;
import com.cc.registry.ServiceRegistry;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.Method;

public class ServerHandler extends SimpleChannelInboundHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

/*    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }*/

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object o) throws Exception {
        RpcRequest request = (RpcRequest) o;
        System.out.println("服务器端获取到的 request:  " + request);
        String className = request.getClasssName();
        className = ServiceRegistry.getService(className);
        String method = request.getMethodName();
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());

        Class c = Class.forName(className);
        Method m = c.getDeclaredMethod(method,request.getParameterTypes());
        System.out.println(c.getName()+" | " + m.getName()+ "  |   " + request.getParameters());
        Object result = m.invoke(c.newInstance(),request.getParameters());
        response.setResult(result);
        System.out.println("服务器 的 response : " + response);
        ctx.writeAndFlush(response);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
}
