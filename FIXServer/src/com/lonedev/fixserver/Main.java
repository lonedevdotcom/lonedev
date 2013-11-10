package com.lonedev.fixserver;

import com.lonedev.fixserver.upstream.UpstreamRequestHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {
    public static void main(String[] args) throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("fixspringconfig.xml");
        FIXServer fixServer = (FIXServer)context.getBean("fixServer");
        fixServer.startFIXServer();
        
        UpstreamRequestHandler upstreamRequestHandler = (UpstreamRequestHandler)context.getBean("upstreamRequestHandler");
        Thread upstreamRequestHandlerThread = new Thread(upstreamRequestHandler);
        upstreamRequestHandlerThread.start();
    }
}
