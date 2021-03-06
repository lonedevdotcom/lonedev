package sqlitejviewer.gui;

import sqlitejviewer.datatypes.DatabaseIndexDTO;
import sqlitejviewer.datatypes.DatabaseTableDTO;
import sqlitejviewer.datatypes.DatabaseTriggerDTO;
import sqlitejviewer.datatypes.DatabaseColumnDTO;
import sqlitejviewer.datatypes.DatabaseViewDTO;
import javax.swing.JTree;
import javax.swing.tree.*;

public class DatabaseViewTreePanel extends javax.swing.JPanel {

    DefaultTreeModel treeModel;

    /** Creates new form DatabaseViewTreePanel */
    public DatabaseViewTreePanel() {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("Database");
        treeModel = new DefaultTreeModel(top);

        initComponents();
        createTopLevelNodes();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        databaseViewScrollPane = new javax.swing.JScrollPane();
        databaseViewTree = new JTree(treeModel);

        databaseViewScrollPane.setViewportView(databaseViewTree);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(databaseViewScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(databaseViewScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane databaseViewScrollPane;
    private javax.swing.JTree databaseViewTree;
    // End of variables declaration//GEN-END:variables

    private void createTopLevelNodes() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)treeModel.getRoot();
        root.removeAllChildren();

        DefaultMutableTreeNode tablesTreeNode = new DefaultMutableTreeNode();
        int numTables = populateTablesTreeNode(tablesTreeNode);
        tablesTreeNode.setUserObject("Tables (" + numTables + ")");

        DefaultMutableTreeNode indexesTreeNode = new DefaultMutableTreeNode();
        int numIndexes = populateIndexTreeNode(indexesTreeNode);
        indexesTreeNode.setUserObject("Indexes (" + numIndexes + ")");

        DefaultMutableTreeNode triggersTreeNode = new DefaultMutableTreeNode();
        int numTriggers = populateTriggerTreeNode(triggersTreeNode);
        triggersTreeNode.setUserObject("Triggers (" + numTriggers + ")");

        DefaultMutableTreeNode viewsTreeNode = new DefaultMutableTreeNode();
        int numViews = populateViewsTreeNode(viewsTreeNode);
        viewsTreeNode.setUserObject("Views (" + numViews + ")");

        root.add(tablesTreeNode);
        root.add(indexesTreeNode);
        root.add(triggersTreeNode);
        root.add(viewsTreeNode);

        // Because (I hope) we are currently running in the Swing Dispatcher
        // thread, we don't need to call "SwingUtiltities.invokeLater()".
        treeModel.reload();
    }

    private int populateTablesTreeNode(DefaultMutableTreeNode tablesTreeNode) {
        int tableCount = 0;

        if (MainFrame.dbInteractor == null) {
            return 0;
        }

        for (DatabaseTableDTO databaseTable : MainFrame.dbInteractor.getDatabaseTables()) {
            DefaultMutableTreeNode tableTreeNode = new DefaultMutableTreeNode(databaseTable);

            for (DatabaseColumnDTO databaseColumn : databaseTable.getColumns()) {
                DefaultMutableTreeNode tableColumnTreeNode = new DefaultMutableTreeNode(databaseColumn);
                tableTreeNode.add(tableColumnTreeNode);
            }

            tableCount++;
            tablesTreeNode.add(tableTreeNode);
        }

        return tableCount;
    }

    private int populateIndexTreeNode(DefaultMutableTreeNode indexesTreeNode) {
        int indexCount = 0;

        if (MainFrame.dbInteractor == null) {
            return 0;
        }

        for (DatabaseIndexDTO databaseIndex : MainFrame.dbInteractor.getDatabaseIndexes()) {
            DefaultMutableTreeNode databaseIndexTreeNode = new DefaultMutableTreeNode(databaseIndex);

            indexCount++;
            indexesTreeNode.add(databaseIndexTreeNode);
        }

        return indexCount;
    }

    private int populateViewsTreeNode(DefaultMutableTreeNode viewsTreeNode) {
        int viewCount = 0;

        if (MainFrame.dbInteractor == null) {
            return 0;
        }

        for (DatabaseViewDTO databaseView : MainFrame.dbInteractor.getDatabaseViews()) {
            DefaultMutableTreeNode databaseViewTreeNode = new DefaultMutableTreeNode(databaseView);

            for (DatabaseColumnDTO databaseColumn : databaseView.getColumns()) {
                DefaultMutableTreeNode tableColumnTreeNode = new DefaultMutableTreeNode(databaseColumn);
                databaseViewTreeNode.add(tableColumnTreeNode);
            }
            
            viewCount++;
            viewsTreeNode.add(databaseViewTreeNode);
        }

        return viewCount;
    }

    public JTree getDatabaseViewTree() {
        return databaseViewTree;
    }

    public void refresh() {
        createTopLevelNodes();
    }

    /**
     * Returns the object that is the selected nodes "userObject"
     * (eg DatabaseObjectDTO, DatabaseColumnDTO etc).
     *
     * @return The userObject of the currently selected node.
     */
    public Object getSelectedNodesUserObject() {
        // It might be possible that nothing is selected (eg if the user clicks
        // the "plus" box). Therefore, getLastSelectedPathComponent() would
        // return null, and this will blow up the "getUserObject()" method. So
        // we'll put a check in here to keep it honest :)
        if (databaseViewTree.getLastSelectedPathComponent() != null) {
            return ((DefaultMutableTreeNode) databaseViewTree.getLastSelectedPathComponent()).getUserObject();
        } else {
            return null;
        }
    }

    private int populateTriggerTreeNode(DefaultMutableTreeNode triggersTreeNode) {
        int triggerCount = 0;

        if (MainFrame.dbInteractor == null) {
            return 0;
        }

        for (DatabaseTriggerDTO databaseTrigger : MainFrame.dbInteractor.getDatabaseTriggers()) {
            DefaultMutableTreeNode databaseTriggerTreeNode = new DefaultMutableTreeNode(databaseTrigger);

            triggerCount++;
            triggersTreeNode.add(databaseTriggerTreeNode);
        }

        return triggerCount;
    }
}
