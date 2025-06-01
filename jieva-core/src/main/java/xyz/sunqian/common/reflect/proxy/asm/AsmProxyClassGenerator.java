package xyz.sunqian.common.reflect.proxy.asm;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.collect.JieArray;
import xyz.sunqian.common.collect.JieStream;
import xyz.sunqian.common.reflect.JieJvm;
import xyz.sunqian.common.reflect.proxy.JieAsm;
import xyz.sunqian.common.reflect.proxy.ProxyClass;
import xyz.sunqian.common.reflect.proxy.ProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyException;
import xyz.sunqian.common.reflect.proxy.ProxyInvoker;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * The <a href="https://asm.ow2.io/">ASM</a> implementation for {@link ProxyClassGenerator}.
 *
 * @author sunqian
 */
public class AsmProxyClassGenerator implements ProxyClassGenerator {

    private static final String OBJECT_INTERNAL_NAME = "java/lang/Object";
    private static final String[] INVOKER_INTERFACE_INTERNAL_NAME = {JieJvm.getInternalName(AsmProxyInvoker.class)};
    private static final String INVOKER_DESCRIPTOR = JieJvm.getDescriptor(AsmProxyInvoker.class);
    private static final String HANDLER_INTERNAL_NAME = JieJvm.getInternalName(ProxyMethodHandler.class);
    private static final String HANDLER_DESCRIPTOR = JieJvm.getDescriptor(ProxyMethodHandler.class);
    private static final String METHODS_DESCRIPTOR = JieJvm.getDescriptor(Method[].class);

    @Override
    public ProxyClass generate(Class<?>[] proxied, ProxyMethodHandler methodHandler) throws ProxyException {
        return null;
    }

    private void generateSuperInvokerMethod(
        @Nonnull ProxyMethodInfo pmInfo,
        @Nonnull ClassWriter classWriter
    ) {
        MethodVisitor visitor = classWriter.visitMethod(
            Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
            pmInfo.superInvokerName,
            pmInfo.superInvokerDescriptor,
            pmInfo.superInvokerSignature,
            pmInfo.exceptions
        );
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        int pIndex = 1;
        for (Parameter parameter : pmInfo.method.getParameters()) {
            JieAsm.loadVar(visitor, parameter.getType(), pIndex);
            pIndex += JieAsm.varSize(parameter.getType());
        }
        visitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            JieJvm.getInternalName(pmInfo.method.getDeclaringClass()),
            pmInfo.method.getName(),
            pmInfo.descriptor,
            pmInfo.isInterface
        );
        JieAsm.visitReturn(visitor, pmInfo.method.getReturnType(), false, false);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private void generateProxyMethod(
        @Nonnull ProxyClassInfo pcInfo,
        @Nonnull ProxyMethodInfo pmInfo,
        int i,
        @Nonnull ClassWriter classWriter
    ) throws Exception {
        MethodVisitor visitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC,
            pmInfo.method.getName(),
            pmInfo.descriptor,
            pmInfo.signature,
            pmInfo.exceptions
        );
        // AsmProxyInvoker invoker = invokers[i];
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.outerName, "invokers", INVOKER_DESCRIPTOR);
        JieAsm.loadConst(visitor, i);
        visitor.visitInsn(Opcodes.AALOAD);
        int invokerPos = JieAsm.paramSize(pmInfo.method.getParameters()) + 1;
        visitor.visitVarInsn(Opcodes.ASTORE, invokerPos);
        visitor.visitVarInsn(Opcodes.ALOAD, invokerPos);
        // if (invoker == null)
        Label ifNonnull = new Label();
        visitor.visitJumpInsn(Opcodes.IFNONNULL, ifNonnull);
        // new Invoker1(i);
        visitor.visitTypeInsn(Opcodes.NEW, pcInfo.fullInnerName);
        // new Invoker1(i).init();
        visitor.visitInsn(Opcodes.DUP);
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        JieAsm.loadConst(visitor, i);
        visitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            pcInfo.fullInnerName,
            "<init>",
            "(" + pcInfo.outerDescriptor + "I)V",
            false
        );
        visitor.visitVarInsn(Opcodes.ASTORE, invokerPos);
        // get invokers
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.outerName, "invokers", INVOKER_DESCRIPTOR);
        // invokers[i] = invoker;
        JieAsm.loadConst(visitor, i);
        visitor.visitVarInsn(Opcodes.ALOAD, invokerPos);
        visitor.visitInsn(Opcodes.AASTORE);
        // endif
        visitor.visitLabel(ifNonnull);
        visitor.visitFrame(Opcodes.F_APPEND, 1, INVOKER_INTERFACE_INTERNAL_NAME, 0, null);
        // get handler
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.outerName, "handler", HANDLER_DESCRIPTOR);
        // get this
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        // get methods[i]
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.outerName, "methods", METHODS_DESCRIPTOR);
        JieAsm.loadConst(visitor, i);
        visitor.visitInsn(Opcodes.AALOAD);
        // get invoker
        visitor.visitVarInsn(Opcodes.ALOAD, invokerPos);
        // new Object[params.size]
        JieAsm.loadConst(visitor, pmInfo.method.getParameterCount());
        visitor.visitTypeInsn(Opcodes.ANEWARRAY, OBJECT_INTERNAL_NAME);
        // set array
        int pIndex = 0;
        for (Parameter parameter : pmInfo.method.getParameters()) {
            // array[i] = param[i]
            visitor.visitInsn(Opcodes.DUP);
            JieAsm.loadConst(visitor, pIndex);
            JieAsm.loadVarToObject(visitor, parameter.getType(), pIndex + 1);
            visitor.visitInsn(Opcodes.AASTORE);
            pIndex += JieAsm.varSize(parameter.getType());
        }
        Method invoke = ProxyMethodHandler.class.getMethod("invoke", Object.class, Method.class, ProxyInvoker.class, Object[].class);
        visitor.visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            HANDLER_INTERNAL_NAME,
            invoke.getName(),
            JieJvm.getDescriptor(invoke),
            true
        );
        Class<?> returnType = pmInfo.method.getReturnType();
        if (Jie.equals(returnType, void.class)) {
            visitor.visitInsn(Opcodes.ACONST_NULL);
        }
        JieAsm.visitReturn(visitor, returnType, true, true);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private byte[] generateProxyInvokerClass(@Nonnull ProxyClassInfo pcInfo) throws Exception {
        // String innerClassName = pcInfo.outerName + "$" + pcInfo.innerName;
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_SUPER,
            pcInfo.fullInnerName,
            null,
            OBJECT_INTERNAL_NAME,
            INVOKER_INTERFACE_INTERNAL_NAME
        );
        classWriter.visitInnerClass(
            pcInfo.fullInnerName,
            pcInfo.outerName,
            pcInfo.innerName,
            Opcodes.ACC_PRIVATE
        );
        {
            // int index;
            FieldVisitor visitor = classWriter.visitField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "index",
                "I",
                null,
                null
            );
            visitor.visitEnd();
        }
        {
            // Outer outer;
            FieldVisitor visitor = classWriter.visitField(
                Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC,
                "this$0",
                pcInfo.outerDescriptor,
                null,
                null);
            visitor.visitEnd();
        }
        {
            // constructor
            MethodVisitor visitor = classWriter.visitMethod(
                0,
                "<init>",
                "(" + pcInfo.outerDescriptor + "I)V",
                null,
                null
            );
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.fullInnerName, "this$0", pcInfo.outerDescriptor);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ILOAD, 2);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.fullInnerName, "index", "I");
            visitor.visitInsn(Opcodes.RETURN);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
        {
            // invoke(Object inst, Object... args);
            Method invoke = AsmProxyInvoker.class.getMethod("invoke", Object.class, Object[].class);
            MethodVisitor visitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
                invoke.getName(),
                JieJvm.getDescriptor(invoke),
                null,
                getExceptions(invoke)
            );
            // Outer outer = (Outer) inst;
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitTypeInsn(Opcodes.CHECKCAST, pcInfo.outerName);
            visitor.visitVarInsn(Opcodes.ASTORE, 3);
            // get index;
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.fullInnerName, "index", "I");
            // switch (index) {}
            Label[] labels = new Label[pcInfo.methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            Label labelLast = new Label();
            visitor.visitTableSwitchInsn(0, pcInfo.methods.size() - 1, labelLast, labels);
            int i = 0;
            for (ProxyMethodInfo pmInfo : pcInfo.methods) {
                visitor.visitLabel(labels[i]);
                if (i++ == 0) {
                    visitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{pcInfo.outerName}, 0, null);
                } else {
                    visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
                // outer.invoke((Long) args[0], (String) args[1]);
                // get outer
                visitor.visitVarInsn(Opcodes.ALOAD, 3);
                int pIndex = 0;
                for (Parameter parameter : pmInfo.method.getParameters()) {
                    // get args
                    visitor.visitVarInsn(Opcodes.ALOAD, 2);
                    JieAsm.loadConst(visitor, pIndex++);
                    // get args[pIndex]
                    visitor.visitInsn(Opcodes.AALOAD);
                    JieAsm.unwrapVar(visitor, parameter.getType(), true);
                }
                JieAsm.invokeVirtual(
                    visitor, pcInfo.outerName, pmInfo.method.getName(), pmInfo.descriptor, pmInfo.isInterface);
                JieAsm.visitReturn(visitor, pmInfo.method.getReturnType(), false, true);
            }
            visitor.visitLabel(labelLast);
            visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            visitor.visitInsn(Opcodes.ACONST_NULL);
            visitor.visitInsn(Opcodes.ARETURN);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
        {
            // invokeSuper(Object inst, Object... args);
            Method invokeSuper = AsmProxyInvoker.class.getMethod("invokeSuper", Object.class, Object[].class);
            MethodVisitor visitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
                invokeSuper.getName(),
                JieJvm.getDescriptor(invokeSuper),
                null,
                getExceptions(invokeSuper)
            );
            // Outer outer = (Outer) inst;
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitTypeInsn(Opcodes.CHECKCAST, pcInfo.outerName);
            visitor.visitVarInsn(Opcodes.ASTORE, 3);
            // get index;
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.fullInnerName, "index", "I");
            // switch (index) {}
            Label[] labels = new Label[pcInfo.methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            Label labelLast = new Label();
            visitor.visitTableSwitchInsn(0, pcInfo.methods.size() - 1, labelLast, labels);
            int i = 0;
            for (ProxyMethodInfo pmInfo : pcInfo.methods) {
                visitor.visitLabel(labels[i]);
                if (i++ == 0) {
                    visitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{pcInfo.outerName}, 0, null);
                } else {
                    visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
                // outer.invoke((Long) args[0], (String) args[1]);
                // get outer
                visitor.visitVarInsn(Opcodes.ALOAD, 3);
                int pIndex = 0;
                for (Parameter parameter : pmInfo.method.getParameters()) {
                    // get args
                    visitor.visitVarInsn(Opcodes.ALOAD, 2);
                    JieAsm.loadConst(visitor, pIndex++);
                    // get args[pIndex]
                    visitor.visitInsn(Opcodes.AALOAD);
                    JieAsm.unwrapVar(visitor, parameter.getType(), true);
                }
                visitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    pcInfo.outerName,
                    pmInfo.superInvokerName,//"access$001",
                    pmInfo.superInvokerDescriptor,
                    false
                );
                Class<?> returnType = pmInfo.method.getReturnType();
                if (Jie.equals(returnType, void.class)) {
                    visitor.visitInsn(Opcodes.ACONST_NULL);
                }
                //JieAsm.visitReturn(visitor, returnType, false);
            }
            visitor.visitLabel(labelLast);
            visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            visitor.visitInsn(Opcodes.ACONST_NULL);
            visitor.visitInsn(Opcodes.ARETURN);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private @Nonnull String @Nullable [] getExceptions(Method method) {
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (JieArray.isEmpty(exceptionTypes)) {
            return null;
        }
        return JieStream.stream(exceptionTypes).map(JieJvm::getInternalName).toArray(String[]::new);
    }

    private static final class ProxyClassInfo {

        private final @Nonnull String outerName;
        private final @Nonnull String outerDescriptor;
        private final @Nonnull String innerName;
        private final @Nonnull String fullInnerName;
        private final @Nonnull List<ProxyMethodInfo> methods;

        private ProxyClassInfo(
            @Nonnull String outerName,
            @Nonnull String outerDescriptor,
            @Nonnull String innerName,
            @Nonnull String fullInnerName,
            @Nonnull List<ProxyMethodInfo> methods
        ) {
            this.outerName = outerName;
            this.outerDescriptor = outerDescriptor;
            this.innerName = innerName;
            this.fullInnerName = fullInnerName;
            this.methods = methods;
        }
    }

    private static final class ProxyMethodInfo {

        private final @Nonnull Method method;
        private final @Nonnull String descriptor;
        private final @Nonnull String signature;
        private final @Nullable String[] exceptions;
        private final @Nonnull String superInvokerName;
        private final @Nonnull String superInvokerDescriptor;
        private final @Nullable String superInvokerSignature;
        private final boolean isInterface;

        private ProxyMethodInfo(
            @Nonnull Method method,
            @Nonnull String descriptor,
            @Nonnull String signature,
            @Nullable String[] exceptions,
            @Nonnull String superInvokerName,
            @Nonnull String superInvokerDescriptor,
            @Nullable String superInvokerSignature,
            boolean isInterface
        ) {
            this.method = method;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            this.superInvokerName = superInvokerName;
            this.superInvokerDescriptor = superInvokerDescriptor;
            this.superInvokerSignature = superInvokerSignature;
            this.isInterface = isInterface;
        }
    }
}
