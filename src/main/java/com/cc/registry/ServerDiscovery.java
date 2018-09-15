package com.cc.registry;

import com.cc.zktools.BaseZkUtils;
import com.cc.zktools.ZkConstants;
import com.cc.zktools.ZkUtils;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZKUtil;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.server.ZooKeeperCriticalThread;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *  服务器启动监听注册端口
 */
public class ServerDiscovery {

    private static ConcurrentHashMap<String,String> registryMap = null;
    private static ZkUtils zk;

    public static void init(){
        System.out.println("服务发现");
        zk = new ZkUtils();
        zk.init(ZkConstants.ZK_HOSTS);
        registryMap = new ConcurrentHashMap<>();
        watch();
    }

    public static void watch(){
        try{
            ZooKeeper z = zk.getZk();
            List<String> nodes = z.getChildren(ZkConstants.REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getType()== Event.EventType.NodeChildrenChanged){
                        watch();
                    }
                }
            });
            ConcurrentHashMap<String,String> newMap = new ConcurrentHashMap<>();
            for(String s : nodes){
                String data = zk.getData(ZkConstants.REGISTRY_PATH+"/"+s);
                System.out.println(data);
                String inter = data.split("\\|")[0];
                String hosts = data.split("\\|")[1];
                newMap.put(inter,hosts);
            }
            registryMap = newMap;
        }catch (Exception e){}
    }

    public static String discovery(String inter){
        System.out.println("服务发现");
        System.out.println(inter);
        System.out.println(registryMap.toString());
        return registryMap.get(inter);
    }

}
