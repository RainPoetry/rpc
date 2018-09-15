package com.cc.zktools;

public interface ZkConstants {
    String ZK_HOSTS = "172.18.1.0:2181,172.18.1.2:2181,172.18.1.3:2181";
    int SESSION_TIMEOUT = 5000;
    String REGISTRY_PATH="/rpc/registry";
    String REGISTRY_SERVICE=REGISTRY_PATH + "/data";
}