package com.cc.commonUtils;

import com.cc.client.RpcClient;
import com.cc.netty.RpcRequest;
import com.cc.netty.RpcResponse;
import com.cc.registry.ServerDiscovery;
import com.cc.registry.ServiceRegistry;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class RpcProxy {

    public static Object proxy(Class<?> t){
        return Proxy.newProxyInstance(t.getClassLoader(), new Class[]{t}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                RpcRequest request = new RpcRequest();
                String inter  = t.getName();
                String address = ServerDiscovery.discovery(inter);
                System.out.println("地址： " + address);
                request.setClasssName(inter);
                request.setMethodName(method.getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);
                request.setRequestId("nihaoa1");
                String host = address.split(":")[0];
                String port = address.split(":")[1];
                System.out.println(host + "-" +port);
                RpcClient client = new RpcClient(host,Integer.parseInt(port));
                RpcResponse response = client.start(request);
                System.out.println("代理结束  +++++++++++++++++++");
                return response.getResult();
            }
        });
    }
}
