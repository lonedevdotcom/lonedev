/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame.java
 *
 * Created on 07-Apr-2011, 11:55:37
 */

package sqlitejviewer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author hawkric
 */
public class MainFrame extends javax.swing.JFrame {

    static DatabaseGUIInteractor dbInteractor;
    
    // Creating a boolean flag so that listeners don't do anything whilst a 
    // refresh of the tree, panel etc is taking place. Otherwise, they're going
    // to get a NullPointerException.
    private boolean undergoingRefresh;

    /**
     * Class that reacts to a new node being selected. It allows for information
     * to be displayed as and when appropriate.
     */
    private class DatabaseTreeSelectionHandler implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent tse) {
            if (!undergoingRefresh) {
                databaseObjectDetailsTextArea.setText(""); // Remove text first, then "maybe" add it later.
            
                Object selectedNode = databaseViewTreePanel.getSelectedNodesUserObject();

                if (selectedNode instanceof DatabaseObjectDTO) {
                    DatabaseObjectDTO databaseObject = (DatabaseObjectDTO) selectedNode;
                    databaseObjectDetailsTextArea.setText(databaseObject.getSql());
                }
            }
        }
    }

    /**
     * The popup handler. Evaluates whether or not right-clicking should bring
     * up a popup menu.
     */
    private class DatabaseTreePopupMenuHandler extends MouseAdapter {
        JPopupMenu databasePopupMenu = new JPopupMenu();
        JPopupMenu individualTablePopupMenu = new JPopupMenu();

        public DatabaseTreePopupMenuHandler() {
            JMenuItem viewDataMenuItem = new JMenuItem("View Data");
            individualTablePopupMenu.add(viewDataMenuItem);
            viewDataMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    DatabaseDataDTO selectedDataObject = (DatabaseDataDTO)databaseViewTreePanel.getSelectedNodesUserObject();
                    ResultSetDialogBox rsdb = new ResultSetDialogBox(MainFrame.this, true, selectedDataObject.getName());
                    rsdb.setTitle(selectedDataObject.getName());
                    rsdb.setVisible(true);
                }
            });

            JMenuItem dumpSchemaMenuItem = new JMenuItem("Dump Schema");
            databasePopupMenu.add(dumpSchemaMenuItem);
            dumpSchemaMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (MainFrame.dbInteractor != null) {
                        String dumpResults = MainFrame.dbInteractor.dumpDatabase();
                        databaseObjectDetailsTextArea.setText(dumpResults);
                    }
                }
            });
        }

        @Override
        public void mousePressed(MouseEvent me) { 
            if (me.isPopupTrigger()) {
                maybeShowPopup(me);
            }
        }

        @Override
        public void mouseReleased(MouseEvent me) { 
            if (me.isPopupTrigger()) {
                maybeShowPopup(me);  
            }
        }

        private void maybeShowPopup(MouseEvent me) {
            if (! undergoingRefresh) {
                // Firstly, the user may have right-clicked over a node that wasn't
                // selected, we should select the node over which they are right-
                // clicking.
                int row = databaseViewTreePanel.getDatabaseViewTree().getRowForLocation(me.getX(), me.getY());
                if (row >= 0) {
                    databaseViewTreePanel.getDatabaseViewTree().setSelectionRow(row);

                    if (databaseViewTreePanel.getSelectedNodesUserObject().toString().equals("Database")) {
                        databasePopupMenu.show(me.getComponent(), me.getX(), me.getY());
                    } else if (databaseViewTreePanel.getSelectedNodesUserObject() instanceof DatabaseDataDTO) {
                        individualTablePopupMenu.show(me.getComponent(), me.getX(), me.getY());
                    }
                }
            }
        }
    }

    /** Creates new form MainFrame */
    public MainFrame(DatabaseGUIInteractor interactor) {
        dbInteractor = interactor;
        initComponents();

        if (dbInteractor != null) {
            setTitle("SQLiteJViewer - " + dbInteractor.getDatabaseFile().getName());
        } else {
            setTitle("SQLiteJViewer");
        }
        
        databaseViewTreePanel.getDatabaseViewTree().addTreeSelectionListener(new DatabaseTreeSelectionHandler());
        databaseViewTreePanel.getDatabaseViewTree().addMouseListener(new DatabaseTreePopupMenuHandler());
    }

    /** 
     * This method is called from within the constructor to initialise the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        databaseViewTreePanel = new sqlitejviewer.DatabaseViewTreePanel();
        databaseObjectDetailsScrollPane = new javax.swing.JScrollPane();
        databaseObjectDetailsTextArea = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openDatabaseMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        databaseMenu = new javax.swing.JMenu();
        showRowCountCheckBoxMenuItem = new javax.swing.JCheckBoxMenuItem();
        refreshDatabaseMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setDividerSize(3);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setTopComponent(databaseViewTreePanel);

        databaseObjectDetailsTextArea.setColumns(20);
        databaseObjectDetailsTextArea.setLineWrap(true);
        databaseObjectDetailsTextArea.setRows(5);
        databaseObjectDetailsTextArea.setWrapStyleWord(true);
        databaseObjectDetailsScrollPane.setViewportView(databaseObjectDetailsTextArea);

        jSplitPane1.setRightComponent(databaseObjectDetailsScrollPane);

        fileMenu.setText("File");

        openDatabaseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openDatabaseMenuItem.setText("Open");
        openDatabaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openDatabaseMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openDatabaseMenuItem);
        fileMenu.add(jSeparator1);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        databaseMenu.setText("Database");

        showRowCountCheckBoxMenuItem.setText("Show Row Count");
        showRowCountCheckBoxMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRowCountCheckBoxMenuItemActionPerformed(evt);
            }
        });
        databaseMenu.add(showRowCountCheckBoxMenuItem);

        refreshDatabaseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        refreshDatabaseMenuItem.setText("Refresh");
        refreshDatabaseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshDatabaseMenuItemActionPerformed(evt);
            }
        });
        databaseMenu.add(refreshDatabaseMenuItem);

        menuBar.add(databaseMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 391, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void openDatabaseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openDatabaseMenuItemActionPerformed
        JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadDatabaseFile(fc.getSelectedFile());
        }
    }//GEN-LAST:event_openDatabaseMenuItemActionPerformed

    private void refreshDatabaseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshDatabaseMenuItemActionPerformed
        if (MainFrame.dbInteractor != null) {
            loadDatabaseFile(dbInteractor.getDatabaseFile());
        }
    }//GEN-LAST:event_refreshDatabaseMenuItemActionPerformed

    private void showRowCountCheckBoxMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRowCountCheckBoxMenuItemActionPerformed
        // On reload of the database file, the menu item is evaluated to see
        // whether or not to get the row counts. As I have now realised, there
        // is no point in setting the "dbInteractorshowRowCounts" variable here
        // because a new instance of SQLGUIInteractor is created anyway, blowing
        // away the previous setting.
        
        if (MainFrame.dbInteractor != null) {
            loadDatabaseFile(dbInteractor.getDatabaseFile());
        }
    }//GEN-LAST:event_showRowCountCheckBoxMenuItemActionPerformed

    private void loadDatabaseFile(File databaseFile) {
        undergoingRefresh = true;
        
        try {
            MainFrame.dbInteractor = new SQLiteDatabaseGUIInteractor(databaseFile, showRowCountCheckBoxMenuItem.getState());
            databaseViewTreePanel.refresh();
            this.setTitle("SQLiteJViewer - " + databaseFile.getName());
        } catch (Exception ex) {
            databaseObjectDetailsTextArea.setText("Unable to open database: " + ex);
            ex.printStackTrace();
        } finally {
            undergoingRefresh = false;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu databaseMenu;
    private javax.swing.JScrollPane databaseObjectDetailsScrollPane;
    private javax.swing.JTextArea databaseObjectDetailsTextArea;
    private sqlitejviewer.DatabaseViewTreePanel databaseViewTreePanel;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openDatabaseMenuItem;
    private javax.swing.JMenuItem refreshDatabaseMenuItem;
    private javax.swing.JCheckBoxMenuItem showRowCountCheckBoxMenuItem;
    // End of variables declaration//GEN-END:variables

}
