package sqlitejviewer;

/**
 * This object is the representation of DabaseObjectDTO's that can return 'data'
 * such as tables and views (which will be subclasses of this).
 *
 * @author Richard Hawkes
 */
public class DatabaseDataDTO extends DatabaseObjectDTO {
    private DatabaseColumnDTO[] columns;

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
}
