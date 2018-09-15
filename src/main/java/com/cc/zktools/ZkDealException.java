package com.cc.zktools;

public class ZkDealException extends RuntimeException{

    public ZkDealException(String msg){
        super(msg);
    }

    public ZkDealException(String msg,Throwable t){
        super(msg,t);
    }
}
