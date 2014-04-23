package com.lonedev.fixserver;

import javax.jms.JMSException;
import javax.jms.Session;
import org.apache.activemq.ActiveMQConnectionFactory;
import quickfix.SessionNotFound;

public class JMSNewInternalFIXMessageHandler implements NewInternalFIXMessageHandler {
    private boolean keepRunning = true;
    private ActiveMQConnectionFactory jmsConnectionFactory;
    private String messageQueueIn;
    private javax.jms.Session jmsSession;
    private javax.jms.MessageConsumer jmsInQueueReceiver;
    
    @Override
    public void run() {
        try {
            while (keepRunning) {
                pollForNewJMSMessage();
            }
        } catch (JMSException jmsex) {
            System.err.println("Serious error polling messages: " + jmsex);
        } catch (SessionNotFound snf) {
            System.err.println("Serious error sending FIX message: " + snf);
        } finally {
            keepRunning = false;
        }
    }

    @Override
    public void stop() {
        keepRunning = false;
    }
    
    public void init() throws JMSException {
        javax.jms.Connection jmsConnection = getJmsConnectionFactory().createQueueConnection();
        jmsSession = jmsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        javax.jms.Queue jmsInQueue = jmsSession.createQueue(getMessageQueueIn());
        jmsInQueueReceiver = jmsSession.createConsumer(jmsInQueue);
        jmsConnection.start();
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

    /**
     * @return the messageQueueIn
     */
    public String getMessageQueueIn() {
        return messageQueueIn;
    }

    /**
     * @param messageQueueIn the messageQueueIn to set
     */
    public void setMessageQueueIn(String messageQueueIn) {
        this.messageQueueIn = messageQueueIn;
    }

    private void pollForNewJMSMessage() throws SessionNotFound, JMSException {
        javax.jms.Message message = jmsInQueueReceiver.receive(1000);
        
        if (message != null) {
            if (message instanceof javax.jms.ObjectMessage && ((javax.jms.ObjectMessage)message).getObject() instanceof quickfix.Message) {
                javax.jms.ObjectMessage objectMessage = (javax.jms.ObjectMessage)message;
                quickfix.Message fixMessage = (quickfix.Message)objectMessage.getObject();
                String targetCompId = objectMessage.getStringProperty("targetCompId");
                String senderCompId = objectMessage.getStringProperty("senderCompId");
                
                if (targetCompId == null || targetCompId.length() == 0) {
                    throw new JMSException("targetCompId not specified in message header.");
                } else if (senderCompId == null || senderCompId.length() == 0) {
                    throw new JMSException("senderCompId not specified in message header.");
                }
                
                quickfix.Session.sendToTarget(fixMessage, senderCompId, targetCompId);
            } else {
                throw new JMSException("Message " + message.getJMSMessageID() + " is not a quickfix.Message");
            }
        }
    }
    
}
