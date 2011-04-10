package sqlitejviewer;

public class DatabaseTableDTO extends DatabaseObjectDTO {
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
