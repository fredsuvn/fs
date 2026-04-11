package space.sunqian.fs.utils.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.asm.ClassWriter;
import space.sunqian.fs.asm.FieldVisitor;
import space.sunqian.fs.asm.Label;
import space.sunqian.fs.asm.MethodVisitor;
import space.sunqian.fs.asm.Opcodes;
import space.sunqian.fs.base.system.JvmKit;
import space.sunqian.fs.dynamic.DynamicClassLoader;
import space.sunqian.fs.object.pool.SimplePool;
import space.sunqian.fs.reflect.TypeRef;
import space.sunqian.fs.third.asm.AsmKit;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.util.Arrays;

final class AsmConnectionWrapperFactory {

    static final @Nonnull SimpleJdbcPool.ConnectionWrapperFactory INST = AsmGenerator.newWrapperFactory();

    private AsmConnectionWrapperFactory() {
    }

    private static final class AsmGenerator {

        private static final @Nonnull String POOLED_NAME = buildClassName("PooledConnection");
        private static final @Nonnull String PROVIDER_NAME = buildClassName("PooledConnectionWrapperFactory");
        // Connection
        private static final @Nonnull String CONNECTION_INTERNAL_NAME = JvmKit.toInternalName(Connection.class);
        private static final @Nonnull String CONNECTION_DESCRIPTOR = JvmKit.toDescriptor(Connection.class);
        // SimplePool
        private static final @Nonnull String POOL_INTERNAL_NAME = JvmKit.toInternalName(SimplePool.class);
        private static final @Nonnull String POOL_DESCRIPTOR = JvmKit.toDescriptor(SimplePool.class);
        private static final @Nullable String POOL_SIGNATURE = JvmKit.toSignature(new TypeRef<SimplePool<Connection>>() {}.type());
        // Exceptions
        private static final @Nonnull String SQL_EXCEPTION_INTERNAL_NAME = JvmKit.toInternalName(SQLException.class);
        private static final @Nonnull String SQL_CLIENT_EXCEPTION_INTERNAL_NAME = JvmKit.toInternalName(SQLClientInfoException.class);
        private static final @Nonnull String @Nonnull [] SQL_EXCEPTIONS = {SQL_EXCEPTION_INTERNAL_NAME};
        private static final @Nonnull String @Nonnull [] SQL_CLIENT_EXCEPTIONS = {SQL_CLIENT_EXCEPTION_INTERNAL_NAME};
        // Provider
        private static final @Nonnull String PROVIDER_INTERNAL_NAME = JvmKit.toInternalName(SimpleJdbcPool.ConnectionWrapperFactory.class);
        private static final @Nonnull String PROVIDER_DESCRIPTOR = JvmKit.toDescriptor(SimpleJdbcPool.ConnectionWrapperFactory.class);
        // Others
        private static final @Nonnull String STRING_DESCRIPTOR = JvmKit.toDescriptor(String.class);
        // fields
        private static final @Nonnull String FIELD_DELEGATE = "delegate";
        private static final @Nonnull String FIELD_POOL = "pool";

        static SimpleJdbcPool.ConnectionWrapperFactory newWrapperFactory() throws SqlRuntimeException {
            byte[] pooledBytes = PooledAsm.bytecode();
            byte[] providerBytes = ProviderAsm.bytecode();
            DynamicClassLoader classLoader = new DynamicClassLoader();
            classLoader.loadClass(null, pooledBytes);
            Class<?> providerClass = classLoader.loadClass(null, providerBytes);
            Object inst = Fs.uncheck(() ->
                    providerClass.getMethod("getInstance").invoke(null),
                SqlRuntimeException::new);
            return (SimpleJdbcPool.ConnectionWrapperFactory) inst;
        }

        private static class ProviderAsm {

            static byte @Nonnull [] bytecode() {

                ClassWriter classWriter = new ClassWriter(0);
                MethodVisitor methodVisitor;

                classWriter.visit(
                    Opcodes.V1_8,
                    Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
                    PROVIDER_NAME,
                    null,
                    AsmKit.OBJECT_NAME,
                    new String[]{PROVIDER_INTERNAL_NAME}
                );

                {
                    methodVisitor = classWriter.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        AsmKit.CONSTRUCTOR_NAME,
                        AsmKit.EMPTY_METHOD_DESCRIPTOR,
                        null,
                        null
                    );
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        AsmKit.OBJECT_NAME,
                        AsmKit.CONSTRUCTOR_NAME,
                        AsmKit.EMPTY_METHOD_DESCRIPTOR,
                        false
                    );
                    methodVisitor.visitInsn(Opcodes.RETURN);
                    methodVisitor.visitMaxs(1, 1);
                    methodVisitor.visitEnd();
                }
                {
                    methodVisitor = classWriter.visitMethod(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                        "getInstance",
                        "()" + PROVIDER_DESCRIPTOR,
                        null,
                        null
                    );
                    methodVisitor.visitTypeInsn(Opcodes.NEW, PROVIDER_NAME);
                    methodVisitor.visitInsn(Opcodes.DUP);
                    methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        PROVIDER_NAME,
                        AsmKit.CONSTRUCTOR_NAME,
                        AsmKit.EMPTY_METHOD_DESCRIPTOR,
                        false
                    );
                    methodVisitor.visitInsn(Opcodes.ARETURN);
                    methodVisitor.visitMaxs(2, 0);
                    methodVisitor.visitEnd();
                }
                {
                    methodVisitor = classWriter.visitMethod(
                        Opcodes.ACC_PUBLIC,
                        "wrap",
                        "(" + CONNECTION_DESCRIPTOR + POOL_DESCRIPTOR + ")" + CONNECTION_DESCRIPTOR,
                        "(" + CONNECTION_DESCRIPTOR + POOL_SIGNATURE + ")" + CONNECTION_DESCRIPTOR,
                        new String[]{JvmKit.toInternalName(SqlRuntimeException.class)}
                    );
                    methodVisitor.visitCode();
                    methodVisitor.visitTypeInsn(Opcodes.NEW, POOLED_NAME);
                    methodVisitor.visitInsn(Opcodes.DUP);
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
                    methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
                        POOLED_NAME,
                        AsmKit.CONSTRUCTOR_NAME,
                        "(" + CONNECTION_DESCRIPTOR + POOL_DESCRIPTOR + ")V",
                        false
                    );
                    methodVisitor.visitInsn(Opcodes.ARETURN);
                    methodVisitor.visitMaxs(4, 3);
                    methodVisitor.visitEnd();
                }
                classWriter.visitEnd();

                return classWriter.toByteArray();
            }
        }

        private static class PooledAsm {

            static byte @Nonnull [] bytecode() {
                ClassWriter classWriter = initClassWriter();
                initFields(classWriter);
                initMethods(classWriter);
                for (Method method : Connection.class.getMethods()) {
                    if (method.getParameterCount() == 0) {
                        String methodName = method.getName();
                        if ("close".equals(methodName) || "isClosed".equals(methodName)) {
                            continue;
                        }
                    }
                    initDelegateMethods(method, classWriter);
                }
                classWriter.visitEnd();
                return classWriter.toByteArray();
            }

            private static @Nonnull ClassWriter initClassWriter() {
                ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
                classWriter.visit(
                    Opcodes.V1_8,
                    Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
                    POOLED_NAME,
                    null,
                    AsmKit.OBJECT_NAME,
                    new String[]{CONNECTION_INTERNAL_NAME}
                );
                return classWriter;
            }

            private static void initFields(@Nonnull ClassWriter classWriter) {
                FieldVisitor fieldVisitor;
                {
                    // private static final String CONNECTION_CLOSED = "Connection has closed.";
                    fieldVisitor = classWriter.visitField(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL | Opcodes.ACC_STATIC,
                        "CONNECTION_CLOSED",
                        STRING_DESCRIPTOR,
                        null,
                        "Connection has closed."
                    );
                    fieldVisitor.visitEnd();
                }
                {
                    // private final Connection delegate;
                    fieldVisitor = classWriter.visitField(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                        FIELD_DELEGATE,
                        CONNECTION_DESCRIPTOR,
                        null,
                        null
                    );
                    fieldVisitor.visitEnd();
                }
                {
                    // private final @Nonnull SimplePool<Connection> pool;
                    fieldVisitor = classWriter.visitField(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                        FIELD_POOL,
                        POOL_DESCRIPTOR,
                        POOL_SIGNATURE,
                        null
                    );
                    fieldVisitor.visitEnd();
                }
                {
                    // private volatile boolean closed;
                    fieldVisitor = classWriter.visitField(
                        Opcodes.ACC_PRIVATE | Opcodes.ACC_VOLATILE,
                        "closed",
                        "Z",
                        null,
                        null
                    );
                    fieldVisitor.visitEnd();
                }
            }

            private static void initMethods(@Nonnull ClassWriter classWriter) {
                MethodVisitor methodVisitor;
                {
                    /*
                     * PooledConnection(@Nonnull Connection delegate, @Nonnull SimplePool<Connection> pool) {
                     *     this.delegate = delegate;
                     *     this.pool = pool;
                     * }
                     */
                    methodVisitor = classWriter.visitMethod(
                        0,
                        AsmKit.CONSTRUCTOR_NAME,
                        "(" + CONNECTION_DESCRIPTOR + POOL_DESCRIPTOR + ")V",
                        "(" + CONNECTION_DESCRIPTOR + POOL_SIGNATURE + ")V",
                        null
                    );
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        AsmKit.OBJECT_NAME,
                        AsmKit.CONSTRUCTOR_NAME,
                        AsmKit.EMPTY_METHOD_DESCRIPTOR,
                        false
                    );
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
                    methodVisitor.visitFieldInsn(
                        Opcodes.PUTFIELD,
                        POOLED_NAME,
                        FIELD_DELEGATE,
                        CONNECTION_DESCRIPTOR
                    );
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
                    methodVisitor.visitFieldInsn(
                        Opcodes.PUTFIELD,
                        POOLED_NAME,
                        FIELD_POOL,
                        POOL_DESCRIPTOR
                    );
                    methodVisitor.visitInsn(Opcodes.RETURN);
                    methodVisitor.visitMaxs(2, 3);
                    methodVisitor.visitEnd();
                }
                {
                    /*
                     * @Override
                     * public synchronized void close() throws SQLException {
                     *     closed = true;
                     *     pool.release(delegate);
                     * }
                     */
                    methodVisitor = classWriter.visitMethod(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNCHRONIZED,
                        "close",
                        AsmKit.EMPTY_METHOD_DESCRIPTOR,
                        null,
                        SQL_EXCEPTIONS
                    );
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitInsn(Opcodes.ICONST_1);
                    methodVisitor.visitFieldInsn(
                        Opcodes.PUTFIELD,
                        POOLED_NAME,
                        "closed",
                        "Z"
                    );
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitFieldInsn(
                        Opcodes.GETFIELD,
                        POOLED_NAME,
                        FIELD_POOL,
                        POOL_DESCRIPTOR
                    );
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitFieldInsn(
                        Opcodes.GETFIELD,
                        POOLED_NAME,
                        FIELD_DELEGATE,
                        CONNECTION_DESCRIPTOR
                    );
                    methodVisitor.visitMethodInsn(
                        Opcodes.INVOKEINTERFACE,
                        POOL_INTERNAL_NAME,
                        "release",
                        "(Ljava/lang/Object;)Z",
                        true
                    );
                    methodVisitor.visitInsn(Opcodes.POP);
                    methodVisitor.visitInsn(Opcodes.RETURN);
                    methodVisitor.visitMaxs(2, 1);
                    methodVisitor.visitEnd();
                }
                {
                    /*
                     * @Override
                     * public synchronized boolean isClosed() throws SQLException {
                     *     return closed;
                     * }
                     */
                    methodVisitor = classWriter.visitMethod(
                        Opcodes.ACC_PUBLIC | Opcodes.ACC_SYNCHRONIZED,
                        "isClosed",
                        "()Z",
                        null,
                        SQL_EXCEPTIONS
                    );
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitFieldInsn(
                        Opcodes.GETFIELD,
                        POOLED_NAME,
                        "closed",
                        "Z"
                    );
                    methodVisitor.visitInsn(Opcodes.IRETURN);
                    methodVisitor.visitMaxs(1, 1);
                    methodVisitor.visitEnd();
                }
                {
                    /*
                     * private void checkClosed() throws SQLException {
                     *     if (closed) {
                     *         throw new SQLException(CONNECTION_CLOSED);
                     *     }
                     * }
                     */
                    methodVisitor = classWriter.visitMethod(
                        Opcodes.ACC_PRIVATE,
                        "checkClosed",
                        AsmKit.EMPTY_METHOD_DESCRIPTOR,
                        null,
                        SQL_EXCEPTIONS
                    );
                    methodVisitor.visitCode();
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitFieldInsn(
                        Opcodes.GETFIELD,
                        POOLED_NAME,
                        "closed",
                        "Z"
                    );
                    Label ifLabel = new Label();
                    methodVisitor.visitJumpInsn(Opcodes.IFEQ, ifLabel);
                    methodVisitor.visitTypeInsn(Opcodes.NEW, SQL_EXCEPTION_INTERNAL_NAME);
                    methodVisitor.visitInsn(Opcodes.DUP);
                    methodVisitor.visitLdcInsn("Connection has closed.");
                    methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        SQL_EXCEPTION_INTERNAL_NAME,
                        AsmKit.CONSTRUCTOR_NAME,
                        "(Ljava/lang/String;)V",
                        false
                    );
                    methodVisitor.visitInsn(Opcodes.ATHROW);
                    methodVisitor.visitLabel(ifLabel);
                    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    methodVisitor.visitInsn(Opcodes.RETURN);
                    methodVisitor.visitMaxs(3, 1);
                    methodVisitor.visitEnd();
                }
                {
                    /*
                     * private void checkClosedForClientInfo() throws SQLClientInfoException {
                     *     if (closed) {
                     *         throw new SQLClientInfoException(CONNECTION_CLOSED, Collections.emptyMap());
                     *     }
                     * }
                     */
                    methodVisitor = classWriter.visitMethod(
                        Opcodes.ACC_PRIVATE,
                        "checkClosedForClientInfo",
                        AsmKit.EMPTY_METHOD_DESCRIPTOR,
                        null,
                        SQL_CLIENT_EXCEPTIONS
                    );
                    methodVisitor.visitCode();
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                    methodVisitor.visitFieldInsn(
                        Opcodes.GETFIELD,
                        POOLED_NAME,
                        "closed", "Z"
                    );
                    Label ifLabel = new Label();
                    methodVisitor.visitJumpInsn(Opcodes.IFEQ, ifLabel);
                    methodVisitor.visitTypeInsn(Opcodes.NEW, SQL_CLIENT_EXCEPTION_INTERNAL_NAME);
                    methodVisitor.visitInsn(Opcodes.DUP);
                    methodVisitor.visitLdcInsn("Connection has closed.");
                    methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESTATIC,
                        "java/util/Collections",
                        "emptyMap",
                        "()Ljava/util/Map;",
                        false
                    );
                    methodVisitor.visitMethodInsn(
                        Opcodes.INVOKESPECIAL,
                        SQL_CLIENT_EXCEPTION_INTERNAL_NAME,
                        AsmKit.CONSTRUCTOR_NAME,
                        "(Ljava/lang/String;Ljava/util/Map;)V",
                        false
                    );
                    methodVisitor.visitInsn(Opcodes.ATHROW);
                    methodVisitor.visitLabel(ifLabel);
                    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                    methodVisitor.visitInsn(Opcodes.RETURN);
                    methodVisitor.visitMaxs(4, 1);
                    methodVisitor.visitEnd();
                }
            }

            private static void initDelegateMethods(@Nonnull Method method, @Nonnull ClassWriter classWriter) {
                /*
                 * @Override
                 * public PreparedStatement prepareStatement(String sql) throws SQLException {
                 *     checkClosed();
                 *     return delegate.prepareStatement(sql);
                 * }
                 */
                int modifiers = method.getModifiers() & ~java.lang.reflect.Modifier.ABSTRACT;
                MethodVisitor methodVisitor;
                String methodDescriptor = JvmKit.toDescriptor(method);
                String[] exceptions = JvmKit.toExceptions(method);
                String checkClosedMethod = Arrays.equals(SQL_CLIENT_EXCEPTIONS, exceptions) ?
                    "checkClosedForClientInfo" : "checkClosed";
                methodVisitor = classWriter.visitMethod(
                    modifiers,
                    method.getName(),
                    methodDescriptor,
                    JvmKit.toSignature(method),
                    exceptions
                );
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESPECIAL,
                    POOLED_NAME,
                    checkClosedMethod,
                    AsmKit.EMPTY_METHOD_DESCRIPTOR,
                    false
                );
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
                methodVisitor.visitFieldInsn(
                    Opcodes.GETFIELD,
                    POOLED_NAME,
                    FIELD_DELEGATE,
                    CONNECTION_DESCRIPTOR
                );
                Parameter[] parameters = method.getParameters();
                int pIndex = 1;
                for (Parameter parameter : parameters) {
                    AsmKit.visitLoad(methodVisitor, parameter.getType(), pIndex);
                    pIndex += AsmKit.varSize(parameter.getType());
                }
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKEINTERFACE,
                    CONNECTION_INTERNAL_NAME,
                    method.getName(),
                    methodDescriptor,
                    true
                );
                AsmKit.visitReturn(methodVisitor, method.getReturnType(), false, false);
                methodVisitor.visitMaxs(0, 0);
                methodVisitor.visitEnd();
            }
        }

        private static String buildClassName(@Nonnull String name) {
            Package pkg = AsmGenerator.class.getPackage();
            return AsmKit.newClassInternalName(pkg, name);
        }

        private AsmGenerator() {
        }
    }
}
