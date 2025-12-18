package space.sunqian.common.invoke;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import space.sunqian.annotations.Nonnull;
import space.sunqian.common.Fs;
import space.sunqian.common.base.system.JvmKit;
import space.sunqian.common.reflect.BytesClassLoader;
import space.sunqian.common.reflect.ClassKit;
import space.sunqian.common.third.asm.AsmKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

final class ByAsm {

    private static final @Nonnull String INVOCABLE_NAME = JvmKit.toInternalName(Invocable.class);
    private static final @Nonnull String @Nonnull [] INTERFACES = new String[]{INVOCABLE_NAME};
    private static final @Nonnull Method INVOKE_CHECKED = Fs.uncheck(
        () -> Invocable.class.getMethod("invokeChecked", Object.class, Object[].class),
        InvocationException::new
    );
    private static final @Nonnull String @Nonnull [] INVOKE_CHECKED_EXCEPTIONS = {JvmKit.toInternalName(Throwable.class)};
    private static final @Nonnull String INVOKE_CHECKED_DESC = JvmKit.toDescriptor(INVOKE_CHECKED);

    static @Nonnull Invocable newInvocable(@Nonnull Method method) {
        String className = buildClassName();
        ClassWriter classWriter = generateClassBody(className);
        generateMethodInvoker(classWriter, method);
        classWriter.visitEnd();
        byte[] bytecode = classWriter.toByteArray();
        return generate(bytecode);
    }

    static @Nonnull Invocable newInvocable(@Nonnull Constructor<?> constructor) {
        String className = buildClassName();
        ClassWriter classWriter = generateClassBody(className);
        generateConstructorInvoker(classWriter, constructor);
        classWriter.visitEnd();
        byte[] bytecode = classWriter.toByteArray();
        return generate(bytecode);
    }

    private static String buildClassName() {
        Package pkg = ByAsm.class.getPackage();
        return AsmKit.newClassInternalName(pkg);
    }

    private static Invocable generate(byte[] bytecode) {
        BytesClassLoader classLoader = new BytesClassLoader();
        Class<?> cls = classLoader.loadClass(null, bytecode);
        return Fs.uncheck(() -> Fs.as(cls.getDeclaredConstructor().newInstance()), InvocationException::new);
    }

    private static ClassWriter generateClassBody(String className) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
            className,
            null,
            AsmKit.OBJECT_NAME,
            INTERFACES
        );
        {
            MethodVisitor visitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC,
                AsmKit.CONSTRUCTOR_NAME,
                AsmKit.EMPTY_CONSTRUCTOR_DESCRIPTOR,
                null,
                null
            );
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                AsmKit.OBJECT_NAME,
                AsmKit.CONSTRUCTOR_NAME,
                AsmKit.EMPTY_CONSTRUCTOR_DESCRIPTOR,
                false
            );
            visitor.visitInsn(Opcodes.RETURN);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
        return classWriter;
    }

    private static void generateMethodInvoker(ClassWriter classWriter, Method method) {
        MethodVisitor visitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
            INVOKE_CHECKED.getName(),
            INVOKE_CHECKED_DESC,
            null,
            INVOKE_CHECKED_EXCEPTIONS
        );
        String methodOwnerName = JvmKit.toInternalName(method.getDeclaringClass());
        boolean isStatic = ClassKit.isStatic(method);
        if (!isStatic) {
            // get object
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitTypeInsn(Opcodes.CHECKCAST, methodOwnerName);
        }
        // loads args
        loadParameters(visitor, method);
        // return object.doMethod(...) or Object.doMethod(...);
        if (isStatic) {
            visitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                methodOwnerName,
                method.getName(),
                JvmKit.toDescriptor(method),
                method.getDeclaringClass().isInterface()
            );
        } else {
            AsmKit.invokeVirtual(
                visitor,
                methodOwnerName,
                method.getName(),
                JvmKit.toDescriptor(method),
                method.getDeclaringClass().isInterface()
            );
        }
        Class<?> returnType = AsmKit.wrapToObject(visitor, method.getReturnType());
        AsmKit.visitReturn(visitor, returnType, false, true);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void generateConstructorInvoker(ClassWriter classWriter, Constructor<?> constructor) {
        MethodVisitor visitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
            INVOKE_CHECKED.getName(),
            INVOKE_CHECKED_DESC,
            null,
            INVOKE_CHECKED_EXCEPTIONS
        );
        String methodOwnerName = JvmKit.toInternalName(constructor.getDeclaringClass());
        // new Object();
        visitor.visitTypeInsn(Opcodes.NEW, methodOwnerName);
        visitor.visitInsn(Opcodes.DUP);
        // loads args
        loadParameters(visitor, constructor);
        // init new object
        visitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            methodOwnerName,
            AsmKit.CONSTRUCTOR_NAME,
            JvmKit.toDescriptor(constructor),
            false
        );
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void loadParameters(MethodVisitor visitor, Executable executable) {
        int pIndex = 0;
        for (Parameter parameter : executable.getParameters()) {
            // get args
            visitor.visitVarInsn(Opcodes.ALOAD, 2);
            AsmKit.visitConst(visitor, pIndex++);
            // get args[pIndex]
            visitor.visitInsn(Opcodes.AALOAD);
            AsmKit.convertObjectTo(visitor, parameter.getType());
        }
    }

    private ByAsm() {
    }
}
