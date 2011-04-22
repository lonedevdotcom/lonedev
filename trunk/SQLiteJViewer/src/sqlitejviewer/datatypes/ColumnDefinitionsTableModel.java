package sqlitejviewer.datatypes;

import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Richard
 */
public class ColumnDefinitionsTableModel extends AbstractTableModel {
    public String columns[] = new String[] { "name", "type", "constraint" };

    JComboBox jcb = new JComboBox(new String[] { "", "REAL", "INTEGER", "TEXT", "BLOB" });

    public int getRowCount() {
        return 0;
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }
}
