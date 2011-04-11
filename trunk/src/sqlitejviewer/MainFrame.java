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

    /**
     * Class that reacts to a new node being selected. It allows for information
     * to be displayed as and when appropriate.
     */
    private class DatabaseTreeSelectionHandler implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent tse) {
            databaseObjectDetailsTextArea.setText(""); // Remove text first, then "maybe" add it later.
            
            Object selectedNode = databaseViewTreePanel.getSelectedNodesUserObject();

            if (selectedNode instanceof DatabaseObjectDTO) {
                DatabaseObjectDTO databaseObject = (DatabaseObjectDTO) selectedNode;
                databaseObjectDetailsTextArea.setText(databaseObject.getSql());
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
                    System.out.println("View data!" + databaseViewTreePanel.getSelectedNodesUserObject());
                    DatabaseTableDTO selectedTable = (DatabaseTableDTO)databaseViewTreePanel.getSelectedNodesUserObject();
                    ResultSetDialogBox rsdb = new ResultSetDialogBox(MainFrame.this, true, selectedTable.getName());
                    rsdb.setTitle(selectedTable.getName());
                    rsdb.setVisible(true);
                }
            });

            JMenuItem dumpSchemaMenuItem = new JMenuItem("Dump Schema");
            databasePopupMenu.add(dumpSchemaMenuItem);
            dumpSchemaMenuItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String dumpResults = MainFrame.dbInteractor.dumpDatabase();
                    databaseObjectDetailsTextArea.setText(dumpResults);
                }
            });

//            JMenuItem vacuumMenuItem = new JMenuItem("Vacuum");
//            JMenuItem exportMenuItem = new JMenuItem("Export to SQL");
//            databasePopupMenu.add(vacuumMenuItem);
//            databasePopupMenu.add(exportMenuItem);
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
            // Firstly, the user may have right-clicked over a node that wasn't
            // selected, we should select the node over which they are right-
            // clicking.
            int row = databaseViewTreePanel.getDatabaseViewTree().getRowForLocation(me.getX(), me.getY());
            if (row >= 0) {
                databaseViewTreePanel.getDatabaseViewTree().setSelectionRow(row);

                // Now we have the selected row. If it's the "Tables" tree, show
                // the tablePopup.
                if (databaseViewTreePanel.getSelectedNodesUserObject().toString().equals("Database")) {
                    databasePopupMenu.show(me.getComponent(), me.getX(), me.getY());
                } else if (databaseViewTreePanel.getSelectedNodesUserObject() instanceof DatabaseTableDTO) {
                    individualTablePopupMenu.show(me.getComponent(), me.getX(), me.getY());
                }
            }
        }
    }

    /** Creates new form MainFrame */
    public MainFrame(DatabaseGUIInteractor interactor) {
        dbInteractor = interactor;
        initComponents();
        
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 554, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 412, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane databaseObjectDetailsScrollPane;
    private javax.swing.JTextArea databaseObjectDetailsTextArea;
    private sqlitejviewer.DatabaseViewTreePanel databaseViewTreePanel;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables

}
