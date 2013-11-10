package com.lonedev.fixserver.upstream;

import com.lonedev.fixserver.FIXServer;

public abstract class UpstreamRequestHandler<M> implements Runnable {
    protected FIXServer<M> fixServer;

    /**
     * @return the fixServer
     */
    public FIXServer<M> getFixServer() {
        return fixServer;
    }

    /**
     * @param fixServer the fixServer to set
     */
    public void setFixServer(FIXServer<M> fixServer) {
        this.fixServer = fixServer;
    }
}
