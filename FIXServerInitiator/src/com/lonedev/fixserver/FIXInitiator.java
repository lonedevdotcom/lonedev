package com.lonedev.fixserver;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import quickfix.Initiator;

public class FIXInitiator {

    public static void main(String[] args) throws Exception {
        ApplicationContext context = new FileSystemXmlApplicationContext(args[0]);
        Initiator fixInitiator = (Initiator) context.getBean("fixInitiator");
        fixInitiator.start();
        
        // Wait a bit for the FIX server to start up....
        try { Thread.sleep(10000); } catch (Exception ex) { }

        Runnable newInternalFIXMessageHandler = (Runnable) context.getBean("newInternalFIXMessageHandler");
        Thread newInternalFIXMessageHandlerThread = new Thread(newInternalFIXMessageHandler);
        
        newInternalFIXMessageHandlerThread.start(); // This runs in a separate thread.
    }

}
