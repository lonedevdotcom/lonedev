package sqlitejviewer.datatypes;

/**
 * Stub subclass of DatabaseObjectDTO representing a database view.
 *
 * @author Richard Hawkes
 */
public class DatabaseViewDTO extends DatabaseDataDTO {
    @Override
    public String getObjectType() {
        return "view";
    }
}
