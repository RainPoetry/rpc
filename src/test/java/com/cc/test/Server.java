package com.cc.test;

import com.cc.Annotation.RpcService;
import com.cc.WebService.HelloImpl;
import com.cc.client.RpcClient;
import com.cc.netty.RpcRequest;
import com.cc.registry.ServerDiscovery;
import com.cc.registry.ServiceRegistry;
import com.cc.server.RpcServer;
import com.cc.zktools.BaseZkUtils;
import com.cc.zktools.CommonsWatcher;
import com.cc.zktools.ZkConstants;
import com.cc.zktools.ZkUtils;
import net.redhogs.cronparser.CronExpressionDescriptor;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

public class Server {

    public static void main(String[] args){
            new ClassPathXmlApplicationContext("classpath:spring/Spring.xml");
    }
}
