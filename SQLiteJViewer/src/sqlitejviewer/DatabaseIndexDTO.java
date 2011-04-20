package sqlitejviewer;

/**
 * Stub subclass of DatabaseObjectDTO representing a database index.
 *
 * @author Richard Hawkes
 */
public class DatabaseIndexDTO extends DatabaseObjectDTO {

    @Override
    public String getObjectType() {
       return "index";
    }
}
