package com.lonedev.fixserver.quickfix;

import java.io.FileInputStream;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import quickfix.Acceptor;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.UnsupportedMessageType;

public class TestAcceptor implements quickfix.Application {

    private static final Logger logger = Logger.getLogger(QuickFIXServer.class.toString());
    private static final String configFilename = "config/testacceptorconfig.dat";
    private static boolean keepRunning = true;

    public static void main(String[] args) throws Exception {
        quickfix.Application application = new QuickFIXServer();
        SessionSettings settings = new SessionSettings(new FileInputStream(configFilename));
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        Acceptor acceptor = new SocketAcceptor(application, storeFactory, settings, logFactory, messageFactory);
        acceptor.start();
        
        while( keepRunning ) { 
            try { Thread.sleep(1000); } catch (Exception ex) { }
        }
        
        acceptor.stop();
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
    }
}
