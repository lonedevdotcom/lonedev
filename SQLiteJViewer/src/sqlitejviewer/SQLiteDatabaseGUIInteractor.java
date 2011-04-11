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

public class SQLiteDatabaseGUIInteractor implements DatabaseGUIInteractor {
    private DatabaseTableDTO[] databaseTables = new DatabaseTableDTO[0];
    private Connection conn;
    private File databaseFile;

    public SQLiteDatabaseGUIInteractor(File databaseFile) throws Exception {
        this.databaseFile = databaseFile;

        connect();
//        createTestTrigger();
//        dumpDatabase();
    }
    
    public void setDatabaseTables(DatabaseTableDTO[] databaseTables) {
        this.databaseTables = databaseTables;
    }
    
    private void connect() throws Exception {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection("jdbc:sqlite:" + getDatabaseFile().getAbsolutePath());
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
                databaseTable.setColumns(getDatabaseColumns(rs.getString("name")));
                databaseTables.add(databaseTable);
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
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

    private DatabaseColumnDTO[] getDatabaseColumns(String tableName) throws Exception {
        List<DatabaseColumnDTO> databaseColumns = new ArrayList<DatabaseColumnDTO>();

        Statement stat = conn.createStatement();
        ResultSet rs = stat.executeQuery("PRAGMA table_info( " + tableName + " )");

        while (rs.next()) {
            DatabaseColumnDTO databaseColumn = new DatabaseColumnDTO();
            databaseColumn.setColumnName(rs.getString("name"));
            databaseColumn.setNullableColumn(rs.getInt("notnull") == 0);
            databaseColumn.setPrimaryKey(rs.getInt("pk") == 1);
            databaseColumn.setDefaultValue(rs.getString("dflt_value"));

            String columnTypeString = rs.getString("type");
            if (columnTypeString.equals("INTEGER")) {
                databaseColumn.setColumnDataType(DatabaseDataType.INTEGER);
            } else if (columnTypeString.equals("TEXT")) {
                databaseColumn.setColumnDataType(DatabaseDataType.TEXT);
            } else if (columnTypeString.equals("REAL")) {
                databaseColumn.setColumnDataType(DatabaseDataType.REAL);
            } else if (columnTypeString.equals("NUMERIC")) {
                databaseColumn.setColumnDataType(DatabaseDataType.NUMERIC);
            } else if (columnTypeString.equals("BLOB")) {
                databaseColumn.setColumnDataType(DatabaseDataType.BLOB);
            } else {
                databaseColumn.setColumnDataType(DatabaseDataType.NONE);
            }

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
                databaseViews.add(databaseView);
            }
        } catch (Exception ex) {
            System.err.println(ex.toString());
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

    public TableModel getTableData(String tableName) {
        try {
            Statement stat = conn.createStatement();
            ResultSet rs = stat.executeQuery("select * from " + tableName);
            TableModel tm = new ResultSetTableModel(rs);
            rs.close();
            stat.close();
            return tm;
        } catch (Exception ex) {
            throw new RuntimeException("Unable to load table: " + ex);
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

//    private void createTestTrigger() {
//        try {
//            boolean execute = conn.createStatement().execute("CREATE TRIGGER test_trigger AFTER INSERT ON profile BEGIN update profile set profile_id = 10000 where profile_id = -1; END");
//        } catch (Exception ex) {
//            System.err.println("Error creating trigger: " + ex);
//        }
//    }

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
}
