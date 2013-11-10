package com.lonedev.fixserver;

public interface FIXServer<M> {
    public void sendFIXMessage(M message, String targetCompId) throws Exception;
    public void startFIXServer() throws Exception;
    public void stopFIXServer() throws Exception;
    public boolean isFIXServerLoggedOn() throws Exception;
}
