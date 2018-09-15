package com.cc.zktools;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.concurrent.atomic.AtomicInteger;

public class CommonsWatcher implements Watcher{

    private AtomicInteger atomicInteger = new AtomicInteger(0);
    public void process(WatchedEvent event) {
        Event.EventType eType = event.getType();
        Event.KeeperState keeperState = event.getState();
        String path = event.getPath();
        switch (keeperState){
            case SyncConnected:
                switch (eType){
                    case None:
                        System.out.println("[Thread-"+atomicInteger.addAndGet(1)+"] 建立连接");
                        break;
                    case NodeCreated:
                        System.out.println("[Thread-"+atomicInteger.addAndGet(1)+"] 创建节点：" +path);
                        break;
                    case NodeDeleted:
                        System.out.println("[Thread-"+atomicInteger.addAndGet(1)+"] 删除节点：" +path);
                        break;
                    case NodeChildrenChanged:
                        System.out.println("[Thread-"+atomicInteger.addAndGet(1)+"]子节点被修改：" +path);
                        break;
                    case NodeDataChanged:
                        System.out.println("[Thread-"+atomicInteger.addAndGet(1)+"]节点数据被修改：" +path);
                        break;
                }
                break;
            case Disconnected:
                System.out.println("[main] 断开连接");
                break;
            case Expired:
                System.out.println("[main] 连接失活");
                break;
            case AuthFailed:
                System.out.println("[main] 认证失败");
                break;
        }

    }
}
