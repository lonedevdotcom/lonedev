package com.lonedev.fixserver.quickfix;

import com.lonedev.fixserver.FIXMessageHandler;
import com.lonedev.fixserver.FIXServer;
import java.io.FileInputStream;
import org.apache.log4j.Logger;
import quickfix.*;
import quickfix.field.Side;

public class QuickFIXServer implements quickfix.Application, FIXServer<Message> {

    private static final Logger logger = Logger.getLogger(QuickFIXServer.class.toString());
    private String configFilename;
    private static boolean keepRunning = true;
    
    private quickfix.Application application;
    private Initiator initiator;
    
    private FIXMessageHandler<Message> messageHandler;
    
    public void setConfigFilename(String configFilename) {
        this.configFilename = configFilename;
    }

    @Override
    public void onCreate(SessionID sessionId) {
        logger.debug("onCreate(sessionId) called");
    }

    @Override
    public void onLogon(SessionID sessionId) {
        logger.debug("onLogon(sessionId) called");
    }

    @Override
    public void onLogout(SessionID sessionId) {
        logger.debug("onLogout(sessionId) called");
    }

    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        logger.debug("toAdmin(message, sessionId) called");
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        logger.debug("fromAdmin(message, sessionId) called");
    }

    @Override
    public void toApp(Message message, SessionID sessionId) throws DoNotSend {
        logger.debug("toApp(message, sessionId) called");
    }

    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        logger.info("fromApp(message, sessionId) called");
        
        try {
            messageHandler.handleFIXMessage(message);
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    public void sendFIXMessage(Message message, String targetCompId) throws SessionNotFound {
        SessionID sessionId = getSessionId(targetCompId);

        if (sessionId == null) {
            throw new SessionNotFound("Unable to find session with targetCompId '" + targetCompId + "'");
        }

        Session.sendToTarget(message, sessionId);
    }

    private SessionID getSessionId(String targetCompId) {
        for (SessionID sId : initiator.getSessions()) {
            if (targetCompId.equals(sId.getTargetCompID())) {
                return sId;
            }
        }

        return null;
    }

    /**
     * @return the messageHandler
     */
    public FIXMessageHandler<Message> getMessageHandler() {
        return messageHandler;
    }

    /**
     * @param messageHandler the messageHandler to set
     */
    public void setMessageHandler(FIXMessageHandler<Message> messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Override
    public void startFIXServer() throws Exception {
        application = new QuickFIXServer();
        SessionSettings settings = new SessionSettings(new FileInputStream(configFilename));
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();

        initiator = new SocketInitiator(application, storeFactory, settings, logFactory, messageFactory);

        initiator.start();
        logger.info("QuickFIXServer initiated successfully.");
    }

    @Override
    public void stopFIXServer() throws Exception {
        initiator.stop();
        logger.info("QuickFIXServer stoppped successfully.");
    }

    @Override
    public boolean isFIXServerLoggedOn() throws Exception {
        return initiator.isLoggedOn();
    }
    
}
