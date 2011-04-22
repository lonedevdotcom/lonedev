package sqlitejviewer.gui;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * This is my convenient way to grab an SQL ResultSet and convert it straight to
 * a TableModel. I suspect this has been done many times before, and it really
 * makes it easy to run a SQL statement and pass the ResultSet into this method.
 * It can then be readily displayed.
 *
 * @author Richard Hawkes
 */
public class ResultSetTableModel extends AbstractTableModel {
    private String[] columnNames;
    private Class[] columnClasses;
    private List<List<Object>> cells = new ArrayList<List<Object>>();

    public ResultSetTableModel() {
        columnNames = new String[1];
        columnNames[0] = "Result Set";

        List<Object> row = new ArrayList<Object>();
        row.add("No data");
        cells.add(row);
    }

    public ResultSetTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();

        // TODO: Keep an eye on ResultSetMetaData.
        // It looks like the ResultSetMetaData object screws up once the
        // ResultSet itself has been read (ie by rs.next() ). Putting any
        // rsmd.XXXX commands after the "while" loop at the bottom throws a
        // nasty exception. A bug on the SQLite side I think.

        columnNames = new String[rsmd.getColumnCount()];
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columnNames[i-1] = rsmd.getColumnName(i);
        }

        columnClasses = new Class[rsmd.getColumnCount()];
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            int columnType = rsmd.getColumnType(i);
            
            switch (columnType) {
                case java.sql.Types.INTEGER:
                case java.sql.Types.NUMERIC:
                    columnClasses[i-1] = Integer.class;
                    break;
                case java.sql.Types.VARCHAR:
                case java.sql.Types.CHAR:
                    columnClasses[i-1] = String.class;
                    break;
                case java.sql.Types.DECIMAL:
                case java.sql.Types.FLOAT:
                    columnClasses[i-1] = Float.class;
                default:
                    columnClasses[i-1] = Object.class;
                    break;
            }
        }

        while (rs.next()) {
            List<Object> row = new ArrayList<Object>();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                row.add(rs.getString(i));
            }
            cells.add(row);
        }
    }

    public int getRowCount() {
        return cells.size();
    }

    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnClasses != null) {
            return columnClasses[columnIndex];
        } else {
            return Object.class;
        }
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return cells.get(rowIndex).get(columnIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }
}
