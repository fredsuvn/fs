package xyz.sunqian.common.invoke;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.asm.AsmKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.system.JvmKit;
import xyz.sunqian.common.reflect.BytesClassLoader;
import xyz.sunqian.common.reflect.ClassKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.atomic.AtomicLong;

final class OfAsm {

    private static final @Nonnull String INVOCABLE_NAME = JvmKit.getInternalName(Invocable.class);
    private static final @Nonnull String @Nonnull [] INTERFACES = new String[]{INVOCABLE_NAME};
    private static final @Nonnull Method INVOKE_CHECKED = Jie.uncheck(
        () -> Invocable.class.getMethod("invokeChecked", Object.class, Object[].class),
        InvocationException::new
    );
    private static final @Nonnull String @Nonnull [] INVOKE_CHECKED_EXCEPTIONS = {JvmKit.getInternalName(Throwable.class)};
    private static final @Nonnull String INVOKE_CHECKED_DESC = JvmKit.getDescriptor(INVOKE_CHECKED);
    // private static final @Nonnull String THROWABLE_NAME = JieJvm.getInternalName(Throwable.class);
    // private static final @Nonnull String EXCEPTION_NAME = JieJvm.getInternalName(InvocationException.class);
    // private static final @Nonnull String EXCEPTION_CONSTRUCTOR_DESCRIPTOR = Jie.uncheck(
    //     () -> JieJvm.getDescriptor(InvocationException.class.getConstructor(Throwable.class)),
    //     InvocationException::new
    // );

    private static final @Nonnull AtomicLong classCounter = new AtomicLong();

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
        return OfAsm.class.getPackage().getName().replace('.', '/')
            + "/" + AsmKit.generateClassSimpleName(classCounter.incrementAndGet());
    }

    private static Invocable generate(byte[] bytecode) {
        BytesClassLoader classLoader = new BytesClassLoader();
        Class<?> cls = classLoader.loadClass(null, bytecode);
        return Jie.uncheck(() -> Jie.as(cls.getDeclaredConstructor().newInstance()), InvocationException::new);
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
        // Label start = new Label();
        // Label end = new Label();
        // Label handler = new Label();
        // visitor.visitTryCatchBlock(start, end, handler, THROWABLE_NAME);
        // visitor.visitLabel(start);
        String methodOwnerName = JvmKit.getInternalName(method.getDeclaringClass());
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
                JvmKit.getDescriptor(method),
                method.getDeclaringClass().isInterface()
            );
        } else {
            AsmKit.invokeVirtual(
                visitor,
                methodOwnerName,
                method.getName(),
                JvmKit.getDescriptor(method),
                method.getDeclaringClass().isInterface()
            );
        }
        // visitor.visitLabel(end);
        Class<?> returnType = AsmKit.wrapToObject(visitor, method.getReturnType());
        AsmKit.visitReturn(visitor, returnType, false, true);
        // visitor.visitLabel(handler);
        // visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{THROWABLE_NAME});
        // visitor.visitVarInsn(Opcodes.ASTORE, 3);
        // visitor.visitTypeInsn(Opcodes.NEW, EXCEPTION_NAME);
        // visitor.visitInsn(Opcodes.DUP);
        // visitor.visitVarInsn(Opcodes.ALOAD, 3);
        // visitor.visitMethodInsn(
        //     Opcodes.INVOKESPECIAL,
        //     EXCEPTION_NAME,
        //     JieAsm.CONSTRUCTOR_NAME,
        //     EXCEPTION_CONSTRUCTOR_DESCRIPTOR,
        //     false
        // );
        // visitor.visitInsn(Opcodes.ATHROW);
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
        // Label start = new Label();
        // Label end = new Label();
        // Label handler = new Label();
        // visitor.visitTryCatchBlock(start, end, handler, THROWABLE_NAME);
        // visitor.visitLabel(start);
        String methodOwnerName = JvmKit.getInternalName(constructor.getDeclaringClass());
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
            JvmKit.getDescriptor(constructor),
            false
        );
        // visitor.visitLabel(end);
        visitor.visitInsn(Opcodes.ARETURN);
        // visitor.visitLabel(handler);
        // visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{THROWABLE_NAME});
        // visitor.visitVarInsn(Opcodes.ASTORE, 3);
        // visitor.visitTypeInsn(Opcodes.NEW, EXCEPTION_NAME);
        // visitor.visitInsn(Opcodes.DUP);
        // visitor.visitVarInsn(Opcodes.ALOAD, 3);
        // visitor.visitMethodInsn(
        //     Opcodes.INVOKESPECIAL,
        //     EXCEPTION_NAME,
        //     JieAsm.CONSTRUCTOR_NAME,
        //     EXCEPTION_CONSTRUCTOR_DESCRIPTOR,
        //     false
        // );
        // visitor.visitInsn(Opcodes.ATHROW);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private static void loadParameters(MethodVisitor visitor, Executable executable) {
        int pIndex = 0;
        for (Parameter parameter : executable.getParameters()) {
            // get args
            visitor.visitVarInsn(Opcodes.ALOAD, 2);
            AsmKit.loadConst(visitor, pIndex++);
            // get args[pIndex]
            visitor.visitInsn(Opcodes.AALOAD);
            AsmKit.convertObjectTo(visitor, parameter.getType());
        }
    }
}
