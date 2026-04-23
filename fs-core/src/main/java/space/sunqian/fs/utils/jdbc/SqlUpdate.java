package space.sunqian.fs.utils.jdbc;

import space.sunqian.fs.Fs;

import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * This interface represents the update operation of a SQL statement.
 *
 * @author sunqian
 */
public interface SqlUpdate extends SqlOperation {

    /**
     * Executes this update operation and returns the number of rows affected by this operation.
     *
     * @return the number of rows affected by this operation
     * @throws SqlRuntimeException if any error occurs
     * @implNote The default implementation commits the connection by {@link Connection#commit()}.
     */
    @SuppressWarnings("resource")
    default long execute() throws SqlRuntimeException {
        return Fs.uncheck(() -> {
            PreparedStatement statement = preparedStatement();
            long count = statement.executeLargeUpdate();
            statement.getConnection().commit();
            return count;
        }, SqlRuntimeException::new);
    }
}
