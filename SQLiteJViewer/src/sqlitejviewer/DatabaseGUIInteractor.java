package sqlitejviewer;

import javax.swing.table.TableModel;

public interface DatabaseGUIInteractor {
    public DatabaseTableDTO[] getDatabaseTables();
    public DatabaseIndexDTO[] getDatabaseIndexes();
    public DatabaseViewDTO[] getDatabaseViews();
    public DatabaseTriggerDTO[] getDatabaseTriggers();
    public TableModel getTableData(String tableName);
    public String dumpDatabase();
}
