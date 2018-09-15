package com.cc.server;

import com.cc.netty.RpcDecoder;
import com.cc.netty.RpcEncoder;
import com.cc.netty.RpcRequest;
import com.cc.netty.RpcResponse;
import com.cc.registry.ServerDiscovery;
import com.cc.registry.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class RpcServer implements ApplicationContextAware,InitializingBean {

    private static Logger log = LoggerFactory.getLogger(RpcServer.class);
    private ApplicationContext ac;

    private int port;
    private String registryAddress;

    public RpcServer(int port,String registryAddress){
        this.port = port;
        this.registryAddress = registryAddress;
    }

    public void start(){
        // 接收客户端请求，并把请求放到这个队列当中
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        // 处理已被接收的请求（经过三次握手后，请求转入这个队列，进行网络读写）
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try{
            // 启动 NIO 服务器的引导类
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workGroup)
                    .channel(NioServerSocketChannel.class)// 类似NIO中serverSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 1024)// 配置TCP参数
                    .option(ChannelOption.SO_BACKLOG, 1024) // 设置tcp缓冲区
                    .option(ChannelOption.SO_SNDBUF, 1024*32) // 设置发送缓冲大小
                    .option(ChannelOption.SO_RCVBUF, 1024*32) // 这是接收缓冲大小
                    .option(ChannelOption.SO_KEEPALIVE, true) // 保持连接
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 最后绑定I/O事件的处理类
                        @Override
                        protected void initChannel(SocketChannel sc) throws Exception {
                            /**
                             *    2. 接收 Client 发送的 RpcRequest 消息    ->  采用 RpcDecoder(RpcRequest.class) 将 二进制数据反序列化
                             *    3. 采用 ServerHandler 向 Client 发送 RpcResponse 数据
                             *    4.  采用 RpcEncoder(RpcResponse.class)  将对象数据序列化为 二进制数据
                             */
                            sc.pipeline().addLast(new RpcEncoder(RpcResponse.class));  // outbound
                            sc.pipeline().addLast(new RpcDecoder(RpcRequest.class));     // inbound
                            sc.pipeline().addLast(new ServerHandler());   // inbound
                        }
                    });
            log.debug("server started on port {}", port);
            // 异步绑定服务器，调用 sync() 方法阻塞等到知道绑定完成
            ChannelFuture f = bootstrap.bind(port).sync();
            // 获取 Channel 的 closeFuture,并且阻塞当前线程知道它完成
            f.channel().closeFuture().sync();
        }catch (Exception e){

        }finally {
            // 关闭 EventLoopGroup，释放所有的资源
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
            this.ac = applicationContext;
        System.out.println("注册的服务地址： " + registryAddress);

        if (registryAddress != null) {
            System.out.println("服务 -----------------------------------------");
            ServiceRegistry.registry(port,applicationContext); // 注册服务
//            ServerDiscovery.init();     // 服务发现
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        start();
    }
}
