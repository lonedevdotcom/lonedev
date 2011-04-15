package sqlitejviewer;

import java.io.File;
import javax.swing.table.TableModel;

public interface DatabaseGUIInteractor {
    public DatabaseTableDTO[] getDatabaseTables();
    public DatabaseIndexDTO[] getDatabaseIndexes();
    public DatabaseViewDTO[] getDatabaseViews();
    public DatabaseTriggerDTO[] getDatabaseTriggers();
    public TableModel getAllData(String dataObjectName);
    public String dumpDatabase();
    public File getDatabaseFile();
}
