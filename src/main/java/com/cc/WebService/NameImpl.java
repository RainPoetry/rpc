package com.cc.WebService;

import com.cc.Annotation.RpcService;

@RpcService(Person.class)
public class NameImpl implements Person{
    @Override
    public void name(String name) {
        System.out.println("your name : " + name);
    }
}
