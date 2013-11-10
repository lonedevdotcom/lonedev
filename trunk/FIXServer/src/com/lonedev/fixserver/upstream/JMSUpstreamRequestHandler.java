package com.lonedev.fixserver.upstream;

import com.lonedev.oms.daos.OMSDAO;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import org.apache.log4j.Logger;

/**
 * This class will poll the MQ queue for any new messages that need to be sent
 * to the FIX server. Apologies for the name, but the term "server" here is 
 * somewhat overloaded. I wanted a clear distinction between what class is
 * handling upstream requests (eg Placements, cancellations etc).
 * 
 * @author Richard Hawkes
 */
public class JMSUpstreamRequestHandler extends UpstreamRequestHandler {

    private static final Logger logger = Logger.getLogger(JMSUpstreamRequestHandler.class.toString());
    private ConnectionFactory jmsConnectionFactory;
    private Connection jmsConnection;
    private Session queueSession;
    private Destination queueDestination;
    private MessageConsumer queueConsumer;
    private boolean keepRunning = true;


    @Override
    public void run() {
        Thread.currentThread().setName("ServerRequestHandler");
        logger.info("UpstreamRequestHandler starting.");

        try {
            jmsConnection = jmsConnectionFactory.createConnection();
            jmsConnection.start(); 
            queueSession = jmsConnection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            queueDestination = queueSession.createQueue("q.omsmessages");
            queueConsumer = queueSession.createConsumer(queueDestination);
            
        } catch(Exception ex) {
            logger.error("Error initiating message queue: " + ex);
            return;
        }
                
        try {
            try { Thread.sleep(5000); } catch (Exception ex) { }
            while (! fixServer.isFIXServerLoggedOn()) {
                logger.info("Fix server is saying it's not logged on yet... Waiting");
                try { Thread.sleep(5000); } catch (Exception ex) { }
            }
        } catch (Exception ex) {
            logger.error(ex);
            return;
        }
        
        javax.jms.Message jmsMessage = null;
        
        while (keepRunning) {
            try {
                if ((jmsMessage = queueConsumer.receive(1000)) != null) {
                    if (jmsMessage instanceof ObjectMessage) {
                        Object objMessage = ((ObjectMessage)jmsMessage).getObject();
                        if (objMessage instanceof OMSDAO) {
                            // TODO Finally we can translate the message and send it to the FIX Server.
                        } else {
                            logger.error("Received a non 'OMSDAO' object message (" + objMessage.getClass() + ")");
                        }
                    } else {
                        logger.error("Received a non ObjectMessage type (" + jmsMessage.getClass() + ")");
                    }
                }
            } catch (Exception ex) {
                logger.error(ex);
            }
        }
       
    }

    /**
     * @return the jmsConnectionFactory
     */
    public ConnectionFactory getJmsConnectionFactory() {
        return jmsConnectionFactory;
    }

    /**
     * @param jmsConnectionFactory the jmsConnectionFactory to set
     */
    public void setJmsConnectionFactory(ConnectionFactory jmsConnectionFactory) {
        this.jmsConnectionFactory = jmsConnectionFactory;
    }
}
