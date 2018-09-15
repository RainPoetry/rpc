package com.cc.zktools;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ZkUtils {

//    private  final Logger logger = LoggerFactory.getLogger(BaseZkUtils.class);
    private  CountDownLatch latch = new CountDownLatch(1);
    private  ZooKeeper zk = null;
    private  ZkUtils zkUtils = null;

//    private static String zkServer;

    public  void init(String zkServer) {
        try {
            zk = new ZooKeeper(zkServer, ZkConstants.SESSION_TIMEOUT, new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (Exception e) {
            throw new ZkDealException("zk对象创建异常", e);
        }
    }

    public  void destroy() {
        zkUtils = null;
        if (zk != null) {
            try {
                zk.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 获取 zk 对象
     *
     * @return
     */
    public  ZooKeeper getZk() {
        return zk;
    }

    /**
     * 创建 zk 节点
     *
     * @param path         节点路径
     * @param data         节点数据
     * @param ifPersistent 节点是否持久存在
     */
    public  void createNode(String path, String data, boolean ifPersistent) {
        try {
            if (ifPersistent) {
                zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } else {
                zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            }
        } catch (Exception e) {
            throw new ZkDealException("zk创建节点异常：" + path, e);
        }
    }

    /**
     * 节点是否
     *
     * @param path
     * @return
     */
    public  boolean existNode(String path) {
        Stat s = null;
        try {
            s = zk.exists(path, false);
        } catch (Exception e) {
            throw new ZkDealException("zk判断节点是否存在异常：" + path, e);
        }
        if (s == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 删除节点
     *
     * @param path
     */
    public  void delNode(String path) {
        try {
            zk.delete(path, -1);
        } catch (Exception e) {
            throw new ZkDealException("zk删除节点异常：" + path, e);
        }
    }

    /**
     * 获取节点数据
     *
     * @param path 节点路径
     * @return
     */
    public String getData(String path) {
        byte[] data = null;
        try {
            data = zk.getData(path, false, null);
        } catch (Exception e) {
            throw new ZkDealException("zk获取节点数据异常：" + path, e);
        }
        return new String(data);
    }

    /**
     * 关闭节点
     */
    public  void close() {
        try {
            zk.close();
        } catch (InterruptedException e) {
            throw new ZkDealException("zk关闭异常", e);
        }
    }

    /**
     * 监听子节点的变化
     */
    public  void watchChildChange(String path) {
        try {
            List<String> childNode = zk.getChildren(path, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    Event.EventType eType = event.getType();
                    Event.KeeperState keeperState = event.getState();
                    String path = event.getPath();
                    if (keeperState == Event.KeeperState.SyncConnected && eType == Event.EventType.NodeChildrenChanged) {
                        watchChildChange(path);
                    }
                }
            });
        }catch (Exception e){

        }
    }


}
