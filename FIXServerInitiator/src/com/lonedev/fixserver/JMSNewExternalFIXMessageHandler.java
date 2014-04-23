package com.lonedev.fixserver;

import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

public class JMSNewExternalFIXMessageHandler implements NewExternalFIXMessageHandler {
    private ActiveMQConnectionFactory jmsConnectionFactory;
    private javax.jms.Session jmsSession;
    private javax.jms.MessageProducer jmsOutQueueSender;
    private String messageQueueOut;
    
    @Override
    public void handleFIXMessage(Message message, SessionID sessionId) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        try {
            javax.jms.ObjectMessage objectMessage = jmsSession.createObjectMessage(message);
            objectMessage.setStringProperty("SenderCompId", sessionId.getSenderCompID());
            jmsOutQueueSender.send(objectMessage);
        } catch (JMSException ex) {
            System.err.println("Unable to send message: " + ex);
        }
    }

    public void init() throws JMSException {
        javax.jms.Connection jmsConnection = getJmsConnectionFactory().createQueueConnection();
        jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        javax.jms.Queue jmsOutQueue = jmsSession.createQueue(getMessageQueueOut());
        jmsOutQueueSender = jmsSession.createProducer(jmsOutQueue);
        jmsConnection.start();
    }

    /**
     * @return the messageQueueOut
     */
    public String getMessageQueueOut() {
        return messageQueueOut;
    }

    /**
     * @param messageQueueOut the messageQueueOut to set
     */
    public void setMessageQueueOut(String messageQueueOut) {
        this.messageQueueOut = messageQueueOut;
    }

    /**
     * @return the jmsConnectionFactory
     */
    public ActiveMQConnectionFactory getJmsConnectionFactory() {
        return jmsConnectionFactory;
    }

    /**
     * @param jmsConnectionFactory the jmsConnectionFactory to set
     */
    public void setJmsConnectionFactory(ActiveMQConnectionFactory jmsConnectionFactory) {
        this.jmsConnectionFactory = jmsConnectionFactory;
    }
}
