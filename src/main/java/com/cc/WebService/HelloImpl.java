package com.cc.WebService;

import com.cc.Annotation.RpcService;

@RpcService(HelloService.class)
public class HelloImpl implements HelloService{
    @Override
    public void say(String data) {
        System.out.println("you are say: " + data);
    }
}
