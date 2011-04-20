package sqlitejviewer;

/**
 * The common component that is contained within SQLite objects (ie tables,
 * triggers, views etc). All DatabaseObjectDTO's have a name and you can obtain
 * the SQL to create the object via the sqlite_master table.
 *
 * @author Richard Hawkes
 */
public abstract class DatabaseObjectDTO {
    private String name, sql;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the sql
     */
    public String getSql() {
        return sql;
    }

    /**
     * @param sql the sql to set
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public abstract String getObjectType();
}
