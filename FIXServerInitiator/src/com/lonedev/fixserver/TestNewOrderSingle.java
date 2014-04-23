package com.lonedev.fixserver;

import java.util.Calendar;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import quickfix.field.ClOrdID;
import quickfix.field.EmailThreadID;
import quickfix.field.EmailType;
import quickfix.field.OrdType;
import quickfix.field.OrderQty;
import quickfix.field.QuoteReqID;
import quickfix.field.SecurityIDSource;
import quickfix.field.Side;
import quickfix.field.Subject;
import quickfix.field.Symbol;
import quickfix.field.TestReqID;
import quickfix.field.Text;
import quickfix.field.TradeDate;
import quickfix.field.TransactTime;

public class TestNewOrderSingle {
    private static javax.jms.Session jmsSession;
    private static javax.jms.MessageProducer jmsQueueSender;
    
//    private static final String BROKER_URL = "tcp://localhost:61616?jms.useAsyncSend=true";
    private static final String BROKER_URL = "tcp://localhost:61616";
    
    public static void main(String[] args) throws Exception {
        ActiveMQConnectionFactory jmsConnectionFactory = new ActiveMQConnectionFactory(BROKER_URL);
        javax.jms.Connection jmsConnection = jmsConnectionFactory.createQueueConnection();
        jmsSession = jmsConnection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
        javax.jms.Queue jmsQueue = jmsSession.createQueue("quickfix.in");
        jmsQueueSender = jmsSession.createProducer(jmsQueue);
        jmsConnection.start();        
        
        int numMessages = 1;
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < numMessages; i++) {
//            sendNewOrderSingle();
//            sendTestMessage();
//            sendEmailMessage();
            sendQuoteRequest();
        }
        
        System.out.println("Sent " + numMessages + " messages in " + (System.currentTimeMillis()-startTime) + "ms");
        
        jmsConnection.stop();
        jmsConnection.close();
    }
    
    private static void sendNewOrderSingle() throws javax.jms.JMSException {
        quickfix.Message nos = new quickfix.fix44.NewOrderSingle(new ClOrdID("" + System.currentTimeMillis()), new Side(Side.BUY), new TransactTime(Calendar.getInstance().getTime()), new OrdType(OrdType.MARKET_ON_CLOSE));
        nos.setField(new Symbol("VOD.L"));
        nos.setField(new SecurityIDSource(SecurityIDSource.RIC_CODE));
        nos.setField(new OrderQty(120));
        nos.setField(new TradeDate("20140317"));
        sendFIXMessageToJMSInQueue(nos);
    }

    private static void sendTestMessage() throws JMSException {
        quickfix.Message testRequest = new quickfix.fix44.TestRequest(new TestReqID("TR" + System.currentTimeMillis()));
        sendFIXMessageToJMSInQueue(testRequest);
    }
    
    private static void sendQuoteRequest() throws JMSException {
        // TO-DO Fix this. It's currently missing tags.
        quickfix.Message quoteRequest = new quickfix.fix44.QuoteRequest(new QuoteReqID("1234500001"));
        quoteRequest.setField(new Symbol("VOD.L"));
        quickfix.fix44.QuoteRequest.NoRelatedSym relatedSymbolsGroup = new quickfix.fix44.QuoteRequest.NoRelatedSym();
        relatedSymbolsGroup.set(new quickfix.fix44.component.Instrument(new Symbol("VOD.L")));
        quoteRequest.addGroup(relatedSymbolsGroup);
        sendFIXMessageToJMSInQueue(quoteRequest);
    }
    
    private static void sendEmailMessage() throws JMSException {
        quickfix.Message email = new quickfix.fix44.Email(new EmailThreadID("1001 "), new EmailType(EmailType.NEW), new Subject("Hello fred"));
        quickfix.fix44.Email.LinesOfText text = new quickfix.fix44.Email.LinesOfText();
        text.set(new Text("Hey Fred, how are you?"));
        email.addGroup(text);
        text.set(new Text("I'm wondering. How do you send e-mails on this blasted FIX thing ?!"));
        email.addGroup(text);
        System.out.println(email.toXML());
        sendFIXMessageToJMSInQueue(email);
    }
    
    private static void sendFIXMessageToJMSInQueue(quickfix.Message fixMessage) throws JMSException {
        ObjectMessage fixObjectMessage = jmsSession.createObjectMessage(fixMessage);
        fixObjectMessage.setStringProperty("senderCompId", "LD");
        fixObjectMessage.setStringProperty("targetCompId", "LDA");
        jmsQueueSender.send(fixObjectMessage);
    }
    
}
