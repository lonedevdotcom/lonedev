package com.lonedev.vlcwebstatusparser;

import java.util.Properties;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLMetadataHandler extends DefaultHandler {
    private boolean captureThisElement = false;
    private String metaName;
    private Properties metaProperties = new Properties();
    private SAXParserFactory factory = SAXParserFactory.newInstance();
    private SAXParser saxParser;
    
    public XMLMetadataHandler() throws Exception {
        factory.setValidating(true);
        saxParser = factory.newSAXParser();
    }
    
    public void parse(String url) throws Exception {
        metaProperties.clear();
        saxParser.parse(url, this);
    }
    
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals("info")) {
            int indexPositionOfNameAttribute = attributes.getIndex("name");
            metaName = attributes.getValue(indexPositionOfNameAttribute);
            captureThisElement = true;
        } else if (qName.equals("state")) {
            metaName = "state";
            captureThisElement = true;
        } else if (qName.equals("length")) {
            metaName = "length";
            captureThisElement = true;
        } else {
            captureThisElement = false;
        }
    }
    
    @Override
    public void characters(char ch[], int start, int length) {
        if (captureThisElement) {
            String metaValue = new String(ch, start, length);
//            System.out.println(metaName + " = " + metaValue);
            metaProperties.setProperty(metaName, metaValue);
        }
    }
    
    public void endElement(String uri, String localName, String qName) {
        captureThisElement = false;
    }

    /**
     * @return the metaProperties
     */
    public Properties getMetaProperties() {
        return metaProperties;
    }

}
