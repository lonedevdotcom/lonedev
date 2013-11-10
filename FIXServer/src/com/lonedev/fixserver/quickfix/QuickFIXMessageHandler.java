package com.lonedev.fixserver.quickfix;

import com.lonedev.fixserver.FIXMessageHandler;
import quickfix.Message;

/**
 * An implementation of the FIXMessageHandler class specifically for QuickFIXJ
 * messages.
 * 
 * @author Richard Hawkes
 */
public class QuickFIXMessageHandler implements FIXMessageHandler<Message> {

    @Override
    public void handleFIXMessage(Message message) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
