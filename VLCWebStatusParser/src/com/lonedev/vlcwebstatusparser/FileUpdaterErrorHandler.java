package com.lonedev.vlcwebstatusparser;

/**
 * An interface that defines a callback should any errors occur in the 
 * FileUpdaterThread
 * 
 * @author Richard Hawkes
 */
public interface FileUpdaterErrorHandler {
    public void handleError(String error);
}
