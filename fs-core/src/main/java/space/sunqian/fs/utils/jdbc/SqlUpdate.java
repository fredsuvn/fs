package space.sunqian.fs.utils.jdbc;

/**
 * This interface represents the update result of a SQL execution.
 *
 * @author sunqian
 */
public interface SqlUpdate extends SqlResult {

    /**
     * Returns the number of rows affected by this SQL statement.
     *
     * @return the number of rows affected by this SQL statement
     */
    long affectedRows();
}
