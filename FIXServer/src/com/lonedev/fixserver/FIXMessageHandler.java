package com.lonedev.fixserver;

/**
 * Implementors of this interface need to handle ANY FIX message sent by the FIX
 * server to it.
 * 
 * @author Richard Hawkes
 */
public interface FIXMessageHandler<M> {
    public void handleFIXMessage(M message) throws Exception;
}
