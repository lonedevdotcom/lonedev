package com.lonedev.fixserver;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Properties;

public class SpringFIXSessionInputStream extends ByteArrayInputStream {

    public SpringFIXSessionInputStream(List<Properties> fixSessions) {
        // Have to do this, as the ByteArrayInputStream expects a byte[] array. 
        // So we'll just re-do the super-constructors work again later (after 
        // we've created the String).
        super(new byte[0]); 
        
        String fixSessionsAsString = getQuickFIXSettingsAsString(fixSessions);
        
        // This is the stuff in the super-class constructor!
        buf = fixSessionsAsString.getBytes();
        pos = 0;
        count = buf.length;
    }
    
    
    private String getQuickFIXSettingsAsString(List<Properties> fixSessions) {
        StringBuilder quickFIXDataStream = new StringBuilder();
        
        for (int i = 0; i < fixSessions.size(); i++) {
            if (i == 0) {
                quickFIXDataStream.append("[DEFAULT]\n");
            } else {
                quickFIXDataStream.append("[SESSION]\n");
            }
            
            Properties sessionProperties = fixSessions.get(i);
            
            for (Object propKey : sessionProperties.keySet()) {
                quickFIXDataStream.append(propKey.toString()).append("=").append(sessionProperties.get(propKey)).append("\n");
            }
            
            quickFIXDataStream.append("\n");
        }
        
        return quickFIXDataStream.toString();
    }    

}
