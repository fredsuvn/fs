package xyz.sunqian.common.asm;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.sunqian.annotations.NonExported;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.ArrayKit;
import xyz.sunqian.common.reflect.JieClass;
import xyz.sunqian.common.reflect.JieJvm;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Objects;

/**
 * Static utility class for <a href="https://asm.ow2.io/">ASM</a>. The runtime environment must have asm package
 * {@code org.objectweb.asm}.
 *
 * @author sunqian
 */
@NonExported
public class JieAsm {

    /**
     * The internal name of {@link Object}.
     */
    public static final @Nonnull String OBJECT_NAME = "java/lang/Object";

    /**
     * The method name of constructor.
     */
    public static final @Nonnull String CONSTRUCTOR_NAME = "<init>";

    /**
     * The descriptor of constructor with empty parameter.
     */
    public static final @Nonnull String EMPTY_CONSTRUCTOR_DESCRIPTOR = "()V";

    /**
     * Returns whether the current runtime environment has asm package.
     *
     * @return whether the current runtime environment has asm package
     */
    public static boolean supportsAsm() {
        return JieClass.classExists("org.objectweb.asm.ClassWriter");
    }

    /**
     * Loads a constant by {@code MethodVisitor.visitInsn}.
     *
     * @param visitor the {@link MethodVisitor} to be invoked
     * @param i       the constant
     */
    public static void loadConst(@Nonnull MethodVisitor visitor, int i) {
        switch (i) {
            case 0:
                visitor.visitInsn(Opcodes.ICONST_0);
                return;
            case 1:
                visitor.visitInsn(Opcodes.ICONST_1);
                return;
            case 2:
                visitor.visitInsn(Opcodes.ICONST_2);
                return;
            case 3:
                visitor.visitInsn(Opcodes.ICONST_3);
                return;
            case 4:
                visitor.visitInsn(Opcodes.ICONST_4);
                return;
            case 5:
                visitor.visitInsn(Opcodes.ICONST_5);
                return;
        }
        if (i <= Byte.MAX_VALUE) {
            visitor.visitIntInsn(Opcodes.BIPUSH, i);
            return;
        }
        visitor.visitIntInsn(Opcodes.SIPUSH, i);
    }

    /**
     * Loads a var by {@code MethodVisitor.visitVarInsn} with the specified type and slot index.
     *
     * @param visitor the {@link MethodVisitor} to be invoked
     * @param type    the specified type
     * @param i       the slot index
     */
    public static void loadVar(@Nonnull MethodVisitor visitor, @Nonnull Class<?> type, int i) {
        if (Objects.equals(type, boolean.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitVarInsn(Opcodes.LLOAD, i);
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitVarInsn(Opcodes.FLOAD, i);
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitVarInsn(Opcodes.DLOAD, i);
            return;
        }
        visitor.visitVarInsn(Opcodes.ALOAD, i);
    }

    /**
     * Converts the object var at the top of the stack to the specified type.
     *
     * @param visitor the {@link MethodVisitor}
     * @param type    the specified type
     */
    public static void convertObjectTo(@Nonnull MethodVisitor visitor, @Nonnull Class<?> type) {
        if (Objects.equals(type, boolean.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            return;
        }
        visitor.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(type));
    }

    /**
     * Wraps the primitive var at the top of the stack. If the var is an object type, then it has no effect. Returns the
     * wrapper type.
     *
     * @param visitor the {@link MethodVisitor} to be invoked
     * @param type    the type of the var
     */
    public static Class<?> wrapToObject(@Nonnull MethodVisitor visitor, @Nonnull Class<?> type) {
        if (Objects.equals(type, boolean.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            return Boolean.class;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            return Byte.class;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            return Short.class;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            return Character.class;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            return Integer.class;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            return Long.class;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            return Float.class;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            return Double.class;
        }
        return type;
    }

    /**
     * Returns var at the top of the stack. If the return type is {@code void}, that means no var at the stack.
     *
     * @param visitor      the {@link MethodVisitor}
     * @param type         the return type
     * @param requiresCast whether to use the {@code CHECKCAST} for object types
     * @param returnNull   {@code true} for returning {@code null} if the return type is {@code void}
     */
    public static void visitReturn(
        @Nonnull MethodVisitor visitor,
        @Nonnull Class<?> type,
        boolean requiresCast,
        boolean returnNull
    ) {
        if (Objects.equals(type, boolean.class)) {
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitInsn(Opcodes.IRETURN);
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitInsn(Opcodes.LRETURN);
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitInsn(Opcodes.FRETURN);
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitInsn(Opcodes.DRETURN);
            return;
        }
        if (Objects.equals(type, void.class)) {
            if (returnNull) {
                visitor.visitInsn(Opcodes.ACONST_NULL);
            } else {
                visitor.visitInsn(Opcodes.RETURN);
                return;
            }
        }
        if (requiresCast) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(type));
        }
        visitor.visitInsn(Opcodes.ARETURN);
    }

    /**
     * Invokes method by {@code INVOKEINTERFACE} or {@code INVOKEVIRTUAL}.
     *
     * @param visitor     the {@link MethodVisitor}
     * @param owner       the internal name of the method's owner class
     * @param name        the method name
     * @param descriptor  the method descriptor
     * @param isInterface whether the method's owner class is an interface.
     */
    public static void invokeVirtual(
        @Nonnull MethodVisitor visitor,
        @Nonnull String owner,
        @Nonnull String name,
        @Nonnull String descriptor,
        boolean isInterface
    ) {
        if (isInterface) {
            visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                owner,
                name,
                descriptor,
                true
            );
        } else {
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                owner,
                name,
                descriptor,
                false
            );
        }
    }

    /**
     * Returns the slot count of the specified type.
     *
     * @param type the specified type
     */
    public static int varSize(@Nonnull Class<?> type) {
        if (Objects.equals(type, long.class) || Objects.equals(type, double.class)) {
            return 2;
        }
        return 1;
    }

    /**
     * Returns the all slot count of the specified parameters.
     *
     * @param parameters the specified parameters
     */
    public static int paramSize(@Nonnull Parameter @Nonnull [] parameters) {
        int size = 0;
        for (Parameter parameter : parameters) {
            size += varSize(parameter.getType());
        }
        return size;
    }

    /**
     * Returns the exception internal names of the specified method.
     *
     * @param method the specified method
     * @return the exception internal names of the specified method
     */
    public static @Nonnull String @Nullable [] getExceptions(Method method) {
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (ArrayKit.isEmpty(exceptionTypes)) {
            return null;
        }
        return Jie.stream(exceptionTypes).map(JieJvm::getInternalName).toArray(String[]::new);
    }

    /**
     * Generates and returns a class simple name with the given count.
     *
     * @param count the given count
     */
    public static String generateClassSimpleName(long count) {
        return "ClassGeneratedBy" + Jie.LIB_NAME
            + "$V" + Jie.LIB_VERSION.replace('.', '_')
            + "$C" + count;
    }
}
