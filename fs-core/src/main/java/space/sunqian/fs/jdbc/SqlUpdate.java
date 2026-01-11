package space.sunqian.fs.jdbc;

/**
 * This interface represents the update for a SQL statement.
 *
 * @author sunqian
 */
public interface SqlUpdate extends SqlOperation {

    /**
     * Returns the number of rows affected by this SQL statement.
     *
     * @return the number of rows affected by this SQL statement
     */
    long affectedRows();
}
