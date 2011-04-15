package sqlitejviewer;

/**
 * This object is the representation of DabaseObjectDTO's that can return 'data'
 * such as tables and views (which will be subclasses of this).
 *
 * @author Richard Hawkes
 */
public class DatabaseDataDTO extends DatabaseObjectDTO {
    private DatabaseColumnDTO[] columns;
    private int rowCount; // -1 indicates "Unknown"

    /**
     * @return the columns
     */
    public DatabaseColumnDTO[] getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(DatabaseColumnDTO[] columns) {
        this.columns = columns;
    }

    /**
     * @return the rowCount
     */
    public int getRowCount() {
        return rowCount;
    }

    /**
     * Sets the number of available rows for this data object. If set to -1,
     * then the value is assumed to be "Unknown" and the "toString()" method
     * won't display anything.
     *
     * @param rowCount the rowCount to set
     */
    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public String toString() {
        String output = getName();

        if (getRowCount() >= 0) {
            output += " - " + getRowCount() + " row(s)";
        }
        
        return output;
    }
}
