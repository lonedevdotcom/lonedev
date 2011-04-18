/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlitejviewer;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * Starting point for the application. Allows for the database path to be
 * specified at runtime too.
 *
 * @author Richard Hawkes
 */
public class Main {

    /**
     * The entry point in to the SQLiteJViewer application.
     *
     * @param args Can contain the filename of the database if you like!
     * @throws Exception Catches any DB connection / file-load issues.
     */
    public static void main(String[] args) throws Exception {
        DatabaseGUIInteractor dbInteractor = null;

        if (args.length > 0) {
            File dbFile = new File(args[0]);
            if (!dbFile.exists() || !dbFile.isFile()) {
                System.out.println("ERROR: " + args[0] + " is not a file!");
                System.exit(1);
            } else {
                dbInteractor = new SQLiteDatabaseGUIInteractor(dbFile, false);
            }
        }
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // We tried to change the look and feel. Oh well, print a message and carry on.
            System.err.println("Unable to set system look and feel: " + ex);
        }
        
        JFrame mainFrame = new MainFrame(dbInteractor);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(640, 480);
        centerScreen(mainFrame);
        mainFrame.setVisible(true);
    }

    /**
     * Derives the current users screen size and puts the JFrame smack in the
     * middle. Without this, it goes annoyingly to the top-left (at least it
     * does in Windows). This gives a better user experience.
     *
     * @param frame The JFrame to reposition.
     */
    private static void centerScreen(JFrame frame) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        double screenHeight = screenSize.getHeight();
        double screenWidth = screenSize.getWidth();

        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();

        frame.setLocation((int) ((screenWidth / 2) - (frameWidth / 2)), (int) ((screenHeight / 2) - (frameHeight / 2)));
    }
}
