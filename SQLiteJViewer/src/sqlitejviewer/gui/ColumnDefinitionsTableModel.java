package sqlitejviewer.gui;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.table.AbstractTableModel;
import sqlitejviewer.datatypes.DatabaseColumnDTO;

/**
 *
 * @author Richard
 */
public class ColumnDefinitionsTableModel extends AbstractTableModel {
    public String columns[] = new String[] { "name", "type", "constraint" };
    public List<DatabaseColumnDTO> columnDefinitions = new ArrayList<DatabaseColumnDTO>();

    JComboBox jcb = new JComboBox(new String[] { "", "REAL", "INTEGER", "TEXT", "BLOB" });

    public ColumnDefinitionsTableModel() {
        DatabaseColumnDTO columnDef = new DatabaseColumnDTO();
        columnDef.setColumnName("col_1");
        columnDef.setColumnDataType("INTEGER");
        columnDef.setDefaultValue("not null");
        columnDefinitions.add(columnDef);
    }

    public int getRowCount() {
        return columnDefinitions.size();
    }

    public int getColumnCount() {
        return columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0: return columnDefinitions.get(rowIndex).getColumnName();
            case 1: return columnDefinitions.get(rowIndex).getColumnDataType();
            case 2: return columnDefinitions.get(rowIndex).getDefaultValue();
            default: return "?";
        }
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return true;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        switch (col) {
            case 0: columnDefinitions.get(row).setColumnName(value.toString()); break;
            case 1: columnDefinitions.get(row).setColumnDataType(value.toString()); break;
            case 2: columnDefinitions.get(row).setDefaultValue(value.toString()); break;
        }
        fireTableCellUpdated(row, col);
    }

}
