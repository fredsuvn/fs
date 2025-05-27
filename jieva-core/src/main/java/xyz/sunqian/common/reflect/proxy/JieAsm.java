package xyz.sunqian.common.reflect.proxy;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.sunqian.common.reflect.JieJvm;
import xyz.sunqian.common.base.exception.UnknownPrimitiveTypeException;

import java.lang.reflect.Parameter;
import java.util.Objects;

public class JieAsm {

    public static String generateSignature(Iterable<Class<?>> uppers) {
        StringBuilder sb = new StringBuilder();
        for (Class<?> upper : uppers) {
            sb.append(JieJvm.getSignature(upper));
        }
        return sb.toString();
    }

    public static void visitLoadParamAsObject(MethodVisitor visitor, Class<?> type, int i) {
        if (!type.isPrimitive()) {
            visitor.visitVarInsn(Opcodes.ALOAD, i);
            return;
        }
        visitLoadPrimitiveParamAsObject(visitor, type, i);
    }

    public static void visitLoadPrimitiveParamAsObject(MethodVisitor visitor, Class<?> type, int i) {
        if (Objects.equals(type, boolean.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitVarInsn(Opcodes.LLOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitVarInsn(Opcodes.FLOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitVarInsn(Opcodes.DLOAD, i);
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
            return;
        }
        throw new UnknownPrimitiveTypeException(type);
    }

    public static void visitObjectCast(MethodVisitor visitor, Class<?> type, boolean needReturn) {
        if (!type.isPrimitive()) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(type));
            if (needReturn) {
                visitor.visitInsn(Opcodes.ARETURN);
            }
            return;
        }
        visitObjectCastPrimitive(visitor, type, needReturn);
    }

    public static void visitObjectCastPrimitive(MethodVisitor visitor, Class<?> type, boolean needReturn) {
        if (Objects.equals(type, boolean.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Boolean");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, byte.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Byte");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, short.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Short");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, char.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Character");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Character", "charValue", "()C", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, int.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Integer");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.IRETURN);
            }
            return;
        }
        if (Objects.equals(type, long.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Long");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.LRETURN);
            }
            return;
        }
        if (Objects.equals(type, float.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Float");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Float", "floatValue", "()F", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.FRETURN);
            }
            return;
        }
        if (Objects.equals(type, double.class)) {
            visitor.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Double");
            visitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
            if (needReturn) {
                visitor.visitInsn(Opcodes.DRETURN);
            }
            return;
        }
        throw new UnknownPrimitiveTypeException(type);
    }

    public static void returnCastObject(MethodVisitor visitor, Class<?> type) {
        if (!type.isPrimitive()) {
            visitor.visitInsn(Opcodes.ARETURN);
            return;
        }
        returnPrimitiveCastObject(visitor, type);
    }

    public static void returnPrimitiveCastObject(MethodVisitor visitor, Class<?> type) {
        if (Objects.equals(type, boolean.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;", false);
        } else if (Objects.equals(type, byte.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;", false);
        } else if (Objects.equals(type, short.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;", false);
        } else if (Objects.equals(type, char.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Character", "valueOf", "(C)Ljava/lang/Character;", false);
        } else if (Objects.equals(type, int.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        } else if (Objects.equals(type, long.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        } else if (Objects.equals(type, float.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;", false);
        } else if (Objects.equals(type, double.class)) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
        } else if (Objects.equals(type, void.class)) {
            visitor.visitInsn(Opcodes.ACONST_NULL);
        } else {
            throw new UnknownPrimitiveTypeException(type);
        }
        visitor.visitInsn(Opcodes.ARETURN);
    }

    public static void visitPushNumber(MethodVisitor visitor, int i) {
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
        } else {
            visitor.visitIntInsn(Opcodes.SIPUSH, i);
        }
    }

    public static int countExtraLocalIndex(Parameter[] parameters) {
        int i = 1;
        for (Parameter parameter : parameters) {
            if (Objects.equals(parameter.getType(), long.class) || Objects.equals(parameter.getType(), double.class)) {
                i += 2;
            } else {
                i++;
            }
        }
        return i;
    }
}
