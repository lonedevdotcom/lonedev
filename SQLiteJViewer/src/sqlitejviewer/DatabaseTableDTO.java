package sqlitejviewer;

/**
 * Stub subclass of DatabaseObjectDTO representing a database table.
 *
 * @author Richard Hawkes
 */
public class DatabaseTableDTO extends DatabaseDataDTO {
    @Override
    public String getObjectType() {
        return "table";
    }
}
