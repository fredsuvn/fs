package space.sunqian.fs.utils.sql;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;

import java.sql.PreparedStatement;

/**
 * This interface represents the batch operation of a list of SQL statements.
 *
 * @author sunqian
 */
public interface SqlBatch extends SqlOperation {

    /**
     * Returns an array of which each element is the number of rows affected by this operation.
     *
     * @return an array of which each element is the number of rows affected by this operation
     * @throws SqlRuntimeException if any error occurs
     */
    @SuppressWarnings("resource")
    default long @Nonnull [] execute() throws SqlRuntimeException {
        return Fs.uncheck(() -> {
            PreparedStatement statement = preparedStatement();
            long[] counts = statement.executeLargeBatch();
            statement.getConnection().commit();
            return counts;
        }, SqlRuntimeException::new);
    }
}
