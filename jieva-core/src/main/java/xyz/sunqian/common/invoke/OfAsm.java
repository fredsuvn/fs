package xyz.sunqian.common.invoke;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.reflect.BytesClassLoader;
import xyz.sunqian.common.reflect.JieJvm;
import xyz.sunqian.common.reflect.proxy.JieAsm;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.concurrent.atomic.AtomicLong;

final class OfAsm implements InvocableGenerator {

    private static final @Nonnull String OBJECT_INTERNAL_NAME = "java/lang/Object";
    private static final @Nonnull String INVOCABLE_INTERNAL_NAME = JieJvm.getInternalName(Invocable.class);
    private static final @Nonnull String @Nonnull [] INTERFACES = new String[]{INVOCABLE_INTERNAL_NAME};
    private static final @Nonnull String INVOKE_DESCRIPTOR = Jie.uncheck(
        () ->
            JieJvm.getDescriptor(Invocable.class.getMethod("invoke", Object.class, Object[].class)
            ),
        InvocationException::new);
    private static final @Nonnull String THROWABLE_NAME = JieJvm.getInternalName(Throwable.class);
    private static final @Nonnull String EXCEPTION_NAME = JieJvm.getInternalName(InvocationException.class);
    private static final @Nonnull String @Nonnull [] EXCEPTIONS = new String[]{EXCEPTION_NAME};

    private static final AtomicLong counter = new AtomicLong();

    @Override
    public @Nonnull Invocable generate(@Nonnull Method method) {
        String className = buildClassName();
        ClassWriter classWriter = generateClassBody(className);
        generateMethodInvoker(classWriter, method);
        classWriter.visitEnd();
        byte[] bytecode = classWriter.toByteArray();
        return generate(bytecode);
    }

    @Override
    public @Nonnull Invocable generate(@Nonnull Constructor<?> constructor) {
        String className = buildClassName();
        ClassWriter classWriter = generateClassBody(className);
        generateConstructorInvoker(classWriter, constructor);
        classWriter.visitEnd();
        byte[] bytecode = classWriter.toByteArray();
        return generate(bytecode);
    }

    private String buildClassName() {
        return getClass().getPackage().getName().replace('.', '/')
            + "/" + JieAsm.generateClassSimpleName(counter.incrementAndGet());
    }

    private Invocable generate(byte[] bytecode) {
        BytesClassLoader classLoader = new BytesClassLoader();
        Class<?> cls = classLoader.loadClass(null, bytecode);
        return Jie.uncheck(() -> Jie.as(cls.getDeclaredConstructor().newInstance()), InvocationException::new);
    }

    private ClassWriter generateClassBody(String className) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
            className,
            null,
            OBJECT_INTERNAL_NAME,
            INTERFACES
        );
        {
            MethodVisitor visitor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
            visitor.visitInsn(Opcodes.RETURN);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
        return classWriter;
    }

    private void generateMethodInvoker(ClassWriter classWriter, Method method) {
        MethodVisitor visitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
            "invoke",
            INVOKE_DESCRIPTOR,
            null,
            EXCEPTIONS
        );
        Label start = new Label();
        Label end = new Label();
        Label handler = new Label();
        visitor.visitTryCatchBlock(start, end, handler, THROWABLE_NAME);
        visitor.visitLabel(start);
        String methodOwnerName = JieJvm.getInternalName(method.getDeclaringClass());
        boolean isStatic = Modifier.isStatic(method.getModifiers());
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
                JieJvm.getDescriptor(method),
                method.getDeclaringClass().isInterface()
            );
        } else {
            JieAsm.invokeVirtual(
                visitor, methodOwnerName, method.getName(), JieJvm.getDescriptor(method), method.getDeclaringClass().isInterface()
            );
        }
        visitor.visitLabel(end);
        Class<?> returnType = JieAsm.wrapToObject(visitor, method.getReturnType());
        JieAsm.visitReturn(visitor, returnType, false, true);
        visitor.visitLabel(handler);
        visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{THROWABLE_NAME});
        visitor.visitVarInsn(Opcodes.ASTORE, 3);
        visitor.visitTypeInsn(Opcodes.NEW, EXCEPTION_NAME);
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitVarInsn(Opcodes.ALOAD, 3);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, EXCEPTION_NAME, "<init>", "(Ljava/lang/Throwable;)V", false);
        visitor.visitInsn(Opcodes.ATHROW);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private void generateConstructorInvoker(ClassWriter classWriter, Constructor<?> constructor) {
        MethodVisitor visitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
            "invoke",
            INVOKE_DESCRIPTOR,
            null,
            EXCEPTIONS
        );
        Label start = new Label();
        Label end = new Label();
        Label handler = new Label();
        visitor.visitTryCatchBlock(start, end, handler, THROWABLE_NAME);
        visitor.visitLabel(start);
        String methodOwnerName = JieJvm.getInternalName(constructor.getDeclaringClass());
        // new Object();
        visitor.visitTypeInsn(Opcodes.NEW, methodOwnerName);
        visitor.visitInsn(Opcodes.DUP);
        // loads args
        loadParameters(visitor, constructor);
        // init new object
        visitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            methodOwnerName,
            "<init>",
            JieJvm.getDescriptor(constructor),
            false
        );
        visitor.visitLabel(end);
        visitor.visitInsn(Opcodes.ARETURN);
        visitor.visitLabel(handler);
        visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{THROWABLE_NAME});
        visitor.visitVarInsn(Opcodes.ASTORE, 3);
        visitor.visitTypeInsn(Opcodes.NEW, EXCEPTION_NAME);
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitVarInsn(Opcodes.ALOAD, 3);
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, EXCEPTION_NAME, "<init>", "(Ljava/lang/Throwable;)V", false);
        visitor.visitInsn(Opcodes.ATHROW);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private void loadParameters(MethodVisitor visitor, Executable executable) {
        int pIndex = 0;
        for (Parameter parameter : executable.getParameters()) {
            // get args
            visitor.visitVarInsn(Opcodes.ALOAD, 2);
            JieAsm.loadConst(visitor, pIndex++);
            // get args[pIndex]
            visitor.visitInsn(Opcodes.AALOAD);
            JieAsm.convertObjectTo(visitor, parameter.getType());
        }
    }
}
