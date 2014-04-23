package com.lonedev.fixserver;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

public class DefaultQuickFIXApplication extends QuickFIXApplicationAdapter {
//    private Map<String,Long> testRequestSendTimes = new HashMap<String, Long>();
//    final static Logger logger = LoggerFactory.getLogger(DefaultQuickFIXApplication.class);
    
    /**
     * The instance of the interface that will handle any new messages that the
     * QuickFIX server receives. Note that the implementing class will be 
     * injected via Spring.
     */
    private NewExternalFIXMessageHandler newExternalFIXMessageHandler;
    
    @Override
    public void fromApp(Message msg, SessionID sid) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        // Because Spring will inject the handler for any new methods, we can
        // simply make the call here. This class has no idea how the FIX message
        // gets handled!
        newExternalFIXMessageHandler.handleFIXMessage(msg, sid);
    }
    
    @Override
    public void toAdmin(Message msg, SessionID sid) {
//        if (msg instanceof quickfix.fix44.TestRequest) {
//            try {
//                quickfix.fix44.TestRequest tr = (quickfix.fix44.TestRequest) msg;
//                TestReqID trId = new TestReqID();
//                tr.getField(trId);
//                logger.debug("Test request " + trId.getValue() + " being sent. Storing for speed test later :-)");
//                testRequestSendTimes.put(trId.getValue(), System.currentTimeMillis());
//            } catch (FieldNotFound ex) {
//                logger.error("Could not get test request id: " + ex);
//            }
//        }
    }
    
    @Override
    public void fromAdmin(Message msg, SessionID sid) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
//        if (msg instanceof quickfix.fix44.Heartbeat) {
//            long receiveTime = System.currentTimeMillis();
//            
//            quickfix.fix44.Heartbeat hb = (quickfix.fix44.Heartbeat) msg;
//            TestReqID trId = new TestReqID();
//            
//            if (hb.isSet(trId)) {
//                hb.getField(trId);
//                
//                logger.debug("Received test request response from remote session.");
//                
//                if (testRequestSendTimes.containsKey(trId.getValue())) {
//                    long sendTime = testRequestSendTimes.get(trId.getValue());
//                    long roundRobinTime = receiveTime - sendTime;
//                    logger.info("Round robin time for test request " + trId.getValue() + " is " + roundRobinTime + "ms");
//                    testRequestSendTimes.remove(trId.getValue());
//                } else {
//                    logger.error("What??? We've received a test id " + trId.getValue() + " for a test we never sent!!");
//                }
//                
//            }
//        }
    }

    /**
     * @return the newIncomingFIXMessageHandler
     */
    public NewExternalFIXMessageHandler getNewExternalFIXMessageHandler() {
        return newExternalFIXMessageHandler;
    }

    /**
     * @param newIncomingFIXMessageHandler the newIncomingFIXMessageHandler to set
     */
    public void setNewExternalFIXMessageHandler(NewExternalFIXMessageHandler newIncomingFIXMessageHandler) {
        this.newExternalFIXMessageHandler = newIncomingFIXMessageHandler;
    }
}
