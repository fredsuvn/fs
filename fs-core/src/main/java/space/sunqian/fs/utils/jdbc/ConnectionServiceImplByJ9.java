package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.object.pool.SimplePool;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.ShardingKey;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

enum ConnectionServiceImplByJ9 implements ConnectionService {
    INST;

    @Override
    public Connection newConnection(
        @Nonnull Connection delegate, @Nonnull SimplePool<Connection> pool
    ) throws SqlRuntimeException {
        return new PooledConnection(delegate, pool);
    }

    private static final class PooledConnection implements Connection {

        private final @Nonnull Connection delegate;
        private final @Nonnull SimplePool<Connection> pool;
        private volatile boolean closed;

        private PooledConnection(@Nonnull Connection delegate, @Nonnull SimplePool<Connection> pool) {
            this.delegate = delegate;
            this.pool = pool;
        }

        @Override
        public synchronized void close() throws SQLException {
            closed = true;
            pool.release(delegate);
        }

        @Override
        public synchronized boolean isClosed() throws SQLException {
            return closed;
        }

        private void checkClosed() throws SQLException {
            if (closed) {
                throw new SQLException("Connection is closed.");
            }
        }

        private void checkClosedForClientInfo() throws SQLClientInfoException {
            if (closed) {
                throw new SQLClientInfoException(Collections.emptyMap(), new SQLException("Connection is closed."));
            }
        }

        //----------------delegate methods: ----------------//

        @Override
        public Statement createStatement() throws SQLException {
            checkClosed();
            return delegate.createStatement();
        }

        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            checkClosed();
            return delegate.prepareStatement(sql);
        }

        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            checkClosed();
            return delegate.prepareCall(sql);
        }

        @Override
        public String nativeSQL(String sql) throws SQLException {
            checkClosed();
            return delegate.nativeSQL(sql);
        }

        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            checkClosed();
            delegate.setAutoCommit(autoCommit);
        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            checkClosed();
            return delegate.getAutoCommit();
        }

        @Override
        public void commit() throws SQLException {
            checkClosed();
            delegate.commit();
        }

        @Override
        public void rollback() throws SQLException {
            checkClosed();
            delegate.rollback();
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            checkClosed();
            return delegate.getMetaData();
        }

        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            checkClosed();
            delegate.setReadOnly(readOnly);
        }

        @Override
        public boolean isReadOnly() throws SQLException {
            checkClosed();
            return delegate.isReadOnly();
        }

        @Override
        public void setCatalog(String catalog) throws SQLException {
            checkClosed();
            delegate.setCatalog(catalog);
        }

        @Override
        public String getCatalog() throws SQLException {
            checkClosed();
            return delegate.getCatalog();
        }

        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            checkClosed();
            delegate.setTransactionIsolation(level);
        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            checkClosed();
            return delegate.getTransactionIsolation();
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            checkClosed();
            return delegate.getWarnings();
        }

        @Override
        public void clearWarnings() throws SQLException {
            checkClosed();
            delegate.clearWarnings();
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            checkClosed();
            return delegate.createStatement(resultSetType, resultSetConcurrency);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            checkClosed();
            return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            checkClosed();
            return delegate.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            checkClosed();
            return delegate.getTypeMap();
        }

        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            checkClosed();
            delegate.setTypeMap(map);
        }

        @Override
        public void setHoldability(int holdability) throws SQLException {
            checkClosed();
            delegate.setHoldability(holdability);
        }

        @Override
        public int getHoldability() throws SQLException {
            checkClosed();
            return delegate.getHoldability();
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            checkClosed();
            return delegate.setSavepoint();
        }

        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            checkClosed();
            return delegate.setSavepoint(name);
        }

        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            checkClosed();
            delegate.rollback(savepoint);
        }

        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            checkClosed();
            delegate.releaseSavepoint(savepoint);
        }

        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            checkClosed();
            return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            checkClosed();
            return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            checkClosed();
            return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            checkClosed();
            return delegate.prepareStatement(sql, autoGeneratedKeys);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            checkClosed();
            return delegate.prepareStatement(sql, columnIndexes);
        }

        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            checkClosed();
            return delegate.prepareStatement(sql, columnNames);
        }

        @Override
        public Clob createClob() throws SQLException {
            checkClosed();
            return delegate.createClob();
        }

        @Override
        public Blob createBlob() throws SQLException {
            checkClosed();
            return delegate.createBlob();
        }

        @Override
        public NClob createNClob() throws SQLException {
            checkClosed();
            return delegate.createNClob();
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            checkClosed();
            return delegate.createSQLXML();
        }

        @Override
        public boolean isValid(int timeout) throws SQLException {
            checkClosed();
            return delegate.isValid(timeout);
        }

        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            checkClosedForClientInfo();
            delegate.setClientInfo(name, value);
        }

        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            checkClosedForClientInfo();
            delegate.setClientInfo(properties);
        }

        @Override
        public String getClientInfo(String name) throws SQLException {
            checkClosed();
            return delegate.getClientInfo(name);
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            checkClosed();
            return delegate.getClientInfo();
        }

        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            checkClosed();
            return delegate.createArrayOf(typeName, elements);
        }

        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            checkClosed();
            return delegate.createStruct(typeName, attributes);
        }

        @Override
        public void setSchema(String schema) throws SQLException {
            checkClosed();
            delegate.setSchema(schema);
        }

        @Override
        public String getSchema() throws SQLException {
            checkClosed();
            return delegate.getSchema();
        }

        @Override
        public void abort(Executor executor) throws SQLException {
            checkClosed();
            delegate.abort(executor);
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            checkClosed();
            delegate.setNetworkTimeout(executor, milliseconds);
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            checkClosed();
            return delegate.getNetworkTimeout();
        }

        @Override
        public void beginRequest() throws SQLException {
            checkClosed();
            delegate.beginRequest();
        }

        @Override
        public void endRequest() throws SQLException {
            checkClosed();
            delegate.endRequest();
        }

        @Override
        public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout) throws SQLException {
            checkClosed();
            return delegate.setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
        }

        @Override
        public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
            checkClosed();
            return delegate.setShardingKeyIfValid(shardingKey, timeout);
        }

        @Override
        public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
            checkClosed();
            delegate.setShardingKey(shardingKey, superShardingKey);
        }

        @Override
        public void setShardingKey(ShardingKey shardingKey) throws SQLException {
            checkClosed();
            delegate.setShardingKey(shardingKey);
        }

        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            checkClosed();
            return delegate.unwrap(iface);
        }

        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            checkClosed();
            return delegate.isWrapperFor(iface);
        }
    }
}
