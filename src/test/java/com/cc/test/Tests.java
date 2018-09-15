package com.cc.test;


import com.cc.WebService.HelloService;
import com.cc.commonUtils.RpcProxy;
import com.cc.zktools.BaseZkUtils;
import com.cc.zktools.CommonsWatcher;
import com.cc.zktools.ZkConstants;
import com.cc.zktools.ZkUtils;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.management.BadAttributeValueExpException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/Spring-client.xml")
public class Tests {


    @Test
    public void service() throws InterruptedException {

        System.out.println("调用服务>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
         HelloService service = (HelloService) RpcProxy.proxy(HelloService.class);
         service.say("are you ok?");
        System.out.println("调用结束 <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }

}
