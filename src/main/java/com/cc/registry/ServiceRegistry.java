package com.cc.registry;


import com.cc.Annotation.RpcService;
import com.cc.zktools.ZkConstants;
import com.cc.zktools.ZkUtils;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;


public class ServiceRegistry {

    private static ZkUtils zk;
    private static ConcurrentHashMap<String,Object> registerServer;


    public static void registry(int port, ApplicationContext ac){
        registerServer = new ConcurrentHashMap<>();
        zk = new ZkUtils();
        zk.init(ZkConstants.ZK_HOSTS);
        service(port,ac);
    }

    public static void service(int port,ApplicationContext ac) {
        try {
            Map<String, Object> beanMap = ac.getBeansWithAnnotation(RpcService.class);
            for (Map.Entry<String, Object> enytry : beanMap.entrySet()) {
                String inter = enytry.getValue().getClass().getAnnotation(RpcService.class).value().getName();
                // 注册服务信息到 ZK 节点
                InetAddress address = InetAddress.getLocalHost();
                zk.createNode(ZkConstants.REGISTRY_SERVICE, inter + "|"+address.getHostAddress()+":"+port, false);
                // 将服务信息 和 实现类缓存到本地中
                registerServer.put(inter, enytry.getValue());
                System.out.println(inter + "  |   " + enytry.getValue().getClass().getName());
            }
        }catch (Exception e){}

    }

    public static String getService(String inter){
        return registerServer.get(inter).getClass().getName();
    }

    public void destroy(){

    }




}
