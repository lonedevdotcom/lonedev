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
    private static final int NUM_TABLES = 20;

    private static final Logger logger = Logger.getLogger(RocketServerAppListener.class.getName());
    private ManagedReference<RocketRoom> vestibuleRef = null;
    private final Set<ManagedReference<RocketTable>> tables = new HashSet<ManagedReference<RocketTable>>();

    private static final long serialVersionUID = 1L;

    public void initialize(Properties props) {
        logger.log(Level.INFO, "Initializing Rocket Server");

        logger.log(Level.CONFIG, "Creating 'Vestibule' (aka the lobby)");
        RocketRoom rocketRoom = new RocketRoom("Vestibule");
        vestibuleRef = AppContext.getDataManager().createReference(rocketRoom);
        logger.log(Level.CONFIG, "'Vestibule' created");

        logger.log(Level.CONFIG, "Creating {0} tables", new Object[] { NUM_TABLES });
        for (int i = 1; i <= NUM_TABLES; i++) {
            RocketTable rt = new RocketTable(i);
            tables.add(AppContext.getDataManager().createReference(rt));
        }
        logger.log(Level.CONFIG, "Tables created");

        logger.log(Level.CONFIG, "Rocket Server initialization COMPLETE!");
    }

    public ClientSessionListener loggedIn(ClientSession session) {
        return new RocketPlayer(session);
    }
}
