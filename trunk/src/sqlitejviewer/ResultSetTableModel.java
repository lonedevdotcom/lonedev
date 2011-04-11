package sqlitejviewer;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class ResultSetTableModel extends AbstractTableModel {
    private String[] columnNames;
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

        columnNames = new String[rsmd.getColumnCount()];
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            columnNames[i-1] = rsmd.getColumnName(i);
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

    public Object getValueAt(int rowIndex, int columnIndex) {
        return cells.get(rowIndex).get(columnIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

}
