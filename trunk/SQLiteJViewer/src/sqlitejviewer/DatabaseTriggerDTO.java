package sqlitejviewer;

/**
 * Stub subclass of DatabaseObjectDTO representing a database trigger.
 *
 * @author Richard Hawkes
 */
public class DatabaseTriggerDTO extends DatabaseObjectDTO {

    @Override
    public String getObjectType() {
        return "trigger";
    }

}
