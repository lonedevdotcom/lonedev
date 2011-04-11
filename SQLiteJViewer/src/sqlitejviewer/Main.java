/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sqlitejviewer;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 *
 * @author hawkric
 */
public class Main {

//    private static final String DB_PATH = "C:\\Documents and Settings\\hawkric\\Desktop\\androsync.sldb";
    public static final String DB_PATH = "androsync.sldb";

    public static void main(String[] args) throws Exception {
        DatabaseGUIInteractor databaseInteractor = new SQLiteDatabaseGUIInteractor(DB_PATH);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            // We tried to change the look and feel. Oh well, print a message and carry on.
            System.err.println("Unable to set system look and feel: " + ex);
        }
        
        JFrame mainFrame = new MainFrame(databaseInteractor);
        mainFrame.setTitle("SQLiteJViewer");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(800, 600);
//        mainFrame.pack();
        centerScreen(mainFrame);
        mainFrame.setVisible(true);
    }

    public static void centerScreen(JFrame frame) {
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        double screenHeight = screenSize.getHeight();
        double screenWidth = screenSize.getWidth();

        int frameWidth = frame.getWidth();
        int frameHeight = frame.getHeight();

        frame.setLocation((int) ((screenWidth / 2) - (frameWidth / 2)), (int) ((screenHeight / 2) - (frameHeight / 2)));
    }
}
