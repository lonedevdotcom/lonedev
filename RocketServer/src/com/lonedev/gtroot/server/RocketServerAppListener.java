package com.lonedev.gtroot.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.ManagedReference;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main entry point to the Rocket Server.
 *
 * @author Richard Hawkes
 */
public class RocketServerAppListener implements AppListener, Serializable {
    private static final Logger logger = Logger.getLogger(RocketServerAppListener.class.getName());

    private static final long serialVersionUID = 1L;

    public void initialize(Properties props) {
        ServerUtils.getInstance().initializeServer();
    }

    public ClientSessionListener loggedIn(ClientSession session) {
        return new RocketPlayer(session);
    }
}
