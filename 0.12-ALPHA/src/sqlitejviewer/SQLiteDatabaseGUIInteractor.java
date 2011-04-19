package sqlitejviewer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.TableModel;

/**
 * The implementer of the DatabaseGUIInteractor. Does the necessary grunt work
 * to/from the database to get the details required.
 *
 * @author Richard Hawkes
 */
public class SQLiteDatabaseGUIInteractor implements DatabaseGUIInteractor {

    private Connection conn;
    private File databaseFile;
    private boolean showingRowCounts;

    public SQLiteDatabaseGUIInteractor(File databaseFile, boolean showRowCounts) throws Exception {
        this.showingRowCounts = showRowCounts;
        this.databaseFile = databaseFile;
        connect();
    }
    
    private void connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + getDatabaseFile().getAbsolutePath());
    }
    
    public void disconnect() throws Exception {
        conn.close();
    }

    @Override
    public void finalize() {
        if (conn != null) {
            try {
                disconnect();
            } catch (Exception ex) {
                System.err.println("Errors closing connection: " + ex);
            }
        }
    }

    public DatabaseTableDTO[] getDatabaseTables() {
        Statement stat = null;
        ResultSet rs = null;
        List<DatabaseTableDTO> databaseTables = new ArrayList<DatabaseTableDTO>();

        try {
            stat = conn.createStatement();

            rs = stat.executeQuery("select * from sqlite_master where type = 'table'");

            while (rs.next()) {
                DatabaseTableDTO databaseTable = new DatabaseTableDTO();
                databaseTable.setName(rs.getString("name"));
                databaseTable.setSql(rs.getString("sql"));

                if (isShowingRowCounts()) {
                    databaseTable.setRowCount(getRowCount(rs.getString("name")));
                } else {
                    databaseTable.setRowCount(-1);
                }

                databaseTable.setColumns(getDatabaseColumns(rs.getString("name")));
                databaseTables.add(databaseTable);
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (Exception ex) {
                // we tried!
            }

            return databaseTables.toArray(new DatabaseTableDTO[0]);
        }
    }

    private DatabaseColumnDTO[] getDatabaseColumns(String dataObjectName) throws Exception {        
        List<DatabaseColumnDTO> databaseColumns = new ArrayList<DatabaseColumnDTO>();

        Statement stat = conn.createStatement();

        // TODO: PRAGMA table_info works for views as well, but is this the right way to do it !?
        ResultSet rs = stat.executeQuery("PRAGMA table_info( " + dataObjectName + " )");

        while (rs.next()) {
            DatabaseColumnDTO databaseColumn = new DatabaseColumnDTO();
            databaseColumn.setColumnName(rs.getString("name"));
            databaseColumn.setNullableColumn(rs.getInt("notnull") == 0);
            databaseColumn.setPrimaryKey(rs.getInt("pk") == 1);
            databaseColumn.setDefaultValue(rs.getString("dflt_value"));
            databaseColumn.setColumnDataType(rs.getString("type").toUpperCase());

            databaseColumns.add(databaseColumn);
        }

        rs.close();
        stat.close();

        return databaseColumns.toArray(new DatabaseColumnDTO[0]);

    }

    public DatabaseIndexDTO[] getDatabaseIndexes() {
        Statement stat = null;
        ResultSet rs = null;
        List<DatabaseIndexDTO> databaseIndexes = new ArrayList<DatabaseIndexDTO>();

        try {
            stat = conn.createStatement();

            rs = stat.executeQuery("select * from sqlite_master where type = 'index'");

            while (rs.next()) {
                DatabaseIndexDTO databaseIndex = new DatabaseIndexDTO();
                databaseIndex.setName(rs.getString("name"));
                databaseIndex.setSql(rs.getString("sql"));
                databaseIndexes.add(databaseIndex);
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (Exception ex) {
                // we tried!
            }

            return databaseIndexes.toArray(new DatabaseIndexDTO[0]);
        }
    }

    public DatabaseViewDTO[] getDatabaseViews() {
        Statement stat = null;
        ResultSet rs = null;
        List<DatabaseViewDTO> databaseViews = new ArrayList<DatabaseViewDTO>();

        try {
            stat = conn.createStatement();

            rs = stat.executeQuery("select * from sqlite_master where type = 'view'");

            while (rs.next()) {
                DatabaseViewDTO databaseView = new DatabaseViewDTO();
                databaseView.setName(rs.getString("name"));
                databaseView.setSql(rs.getString("sql"));

                if (isShowingRowCounts()) {
                    databaseView.setRowCount(getRowCount(rs.getString("name")));
                } else {
                    databaseView.setRowCount(-1);
                }

                databaseView.setColumns(getDatabaseColumns(rs.getString("name")));
                databaseViews.add(databaseView);
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (Exception ex) {
                // we tried!
            }

            return databaseViews.toArray(new DatabaseViewDTO[0]);
        }
    }

    public TableModel getAllData(String dataObjectName) {
        try {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select * from " + dataObjectName);
            TableModel tm = new ResultSetTableModel(rs);
            rs.close();
            stat.close();
            return tm;
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load data: " + ex);
        }
    }

    public DatabaseTriggerDTO[] getDatabaseTriggers() {
        Statement stat = null;
        ResultSet rs = null;
        List<DatabaseTriggerDTO> databaseTriggers = new ArrayList<DatabaseTriggerDTO>();

        try {
            stat = conn.createStatement();

            rs = stat.executeQuery("select * from sqlite_master where type = 'trigger'");

            while (rs.next()) {
                DatabaseTriggerDTO databaseTrigger = new DatabaseTriggerDTO();
                databaseTrigger.setName(rs.getString("name"));
                databaseTrigger.setSql(rs.getString("sql"));
                databaseTriggers.add(databaseTrigger);
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (Exception ex) {
                // we tried!
            }

            return databaseTriggers.toArray(new DatabaseTriggerDTO[0]);
        }
    }

    public String dumpDatabase() {
        String results = null;
        String output = "";
        String errors = "";

        try {
            Process proc = Runtime.getRuntime().exec("sqlite3 \"" + getDatabaseFile().getAbsolutePath() + "\" .dump");

            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

            // read the output from the command
            while ((results = stdInput.readLine()) != null) {
                output += results + "\n";
            }

            // read any errors from the attempted command
            while ((results = stdError.readLine()) != null) {
                errors += results += "\n";
            }

            if (errors.length() > 0) {
                throw new Exception(errors);
            }

            return output;

        } catch (Exception ex) {
            results = "FAILED: " + ex;
            return results;
        }
    }

    public File getDatabaseFile() {
        return this.databaseFile;
    }

    public int getRowCount(String dataObjectName) {
        int numRows = 0;
        
        Statement stat = null;
        ResultSet rs = null;

        try {
            stat = conn.createStatement();
            rs = stat.executeQuery("select count(*) from " + dataObjectName);

            while (rs.next()) {
                numRows = rs.getInt(1);
            }
        } catch (Exception ex) {
            System.err.println("Error getting row count for " + dataObjectName + ": " + ex);
        } finally {
            try {
                rs.close();
                stat.close();
            } catch (Exception ex) {
                // We tried to disconnect!
            }
        }

        return numRows;
    }

    /**
     * @return the showingRowCounts
     */
    public boolean isShowingRowCounts() {
        return showingRowCounts;
    }

    /**
     * @param showingRowCounts the showingRowCounts to set
     */
    public void setShowingRowCounts(boolean showingRowCounts) {
        this.showingRowCounts = showingRowCounts;
    }
}
