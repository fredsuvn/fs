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

    @Override
    public ProxyClass generate(Class<?>[] proxied, ProxyMethodHandler methodHandler) throws ProxyException {
        return null;
    }

    private void createSyntheticSuperMethod(
        @Nonnull String syntheticName,
        @Nonnull Method method,
        @Nonnull ClassWriter classWriter
    ) {
        MethodVisitor methodVisitor = classWriter.visitMethod(
            Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
            syntheticName,
            JieJvm.getDescriptor(method),
            JieJvm.getSignature(method),
            buildExceptions(method)
        );
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
        int i = 0;
        for (Parameter parameter : method.getParameters()) {
            JieAsm.loadVar(methodVisitor, parameter.getType(), i);
            i += JieAsm.slotCount(parameter.getType());
        }
        methodVisitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            JieJvm.getInternalName(method.getDeclaringClass()),
            method.getName(),
            JieJvm.getDescriptor(method),
            method.getDeclaringClass().isInterface()
        );
        JieAsm.unwrapVar(methodVisitor, method.getReturnType(), false);
        JieAsm.visitReturn(methodVisitor, method.getReturnType(), false);
        methodVisitor.visitMaxs(0, 0);
        methodVisitor.visitEnd();
    }

    private static final String OBJECT_INTERNAL_NAME = "java/lang/Object";
    private static final String[] INVOKER_INTERFACE_INTERNAL_NAME = {JieJvm.getInternalName(AsmProxyInvoker.class)};
    // private static final String INVOKE_DESCRIPTOR;
    // private static final String INVOKE_SUPER_DESCRIPTOR;
    //
    // static {
    //     try {
    //         Method invoke = AsmProxyInvoker.class.getMethod("invoke", Object.class, Object[].class);
    //         INVOKE_DESCRIPTOR = JieJvm.getDescriptor(invoke);
    //         Method invokeSuper = AsmProxyInvoker.class.getMethod("invokeSuper", Object.class, Object[].class);
    //         INVOKE_SUPER_DESCRIPTOR = JieJvm.getDescriptor(invokeSuper);
    //     } catch (NoSuchMethodException e) {
    //         throw new AsmProxyException(e);
    //     }
    // }

    private byte[] createProxyInvoker(
        @Nonnull String outerName,
        @Nonnull String innerName,
        @Nonnull List<Method> methods
    ) throws Exception {
        String className = outerName + "$" + innerName;
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_SUPER,
            className,
            null,
            OBJECT_INTERNAL_NAME,
            INVOKER_INTERFACE_INTERNAL_NAME
        );
        classWriter.visitInnerClass(
            className,
            outerName,
            innerName,
            Opcodes.ACC_PRIVATE
        );
        {
            FieldVisitor fieldVisitor = classWriter.visitField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "index",
                "I",
                null,
                null
            );
            fieldVisitor.visitEnd();
        }
        String outerDescriptor = "L" + outerName + ";";
        {
            FieldVisitor fieldVisitor = classWriter.visitField(
                Opcodes.ACC_FINAL | Opcodes.ACC_SYNTHETIC,
                "this$0",
                outerDescriptor,
                null,
                null);
            fieldVisitor.visitEnd();
        }
        {
            MethodVisitor methodVisitor = classWriter.visitMethod(
                Opcodes.ACC_PRIVATE,
                "<init>",
                "(" + outerDescriptor + "I)V",
                null,
                null
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, className, "this$0", outerDescriptor);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitMethodInsn(Opcodes.INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitVarInsn(Opcodes.ILOAD, 2);
            methodVisitor.visitFieldInsn(Opcodes.PUTFIELD, className, "index", "I");
            methodVisitor.visitInsn(Opcodes.RETURN);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
        {
            Method invoke = AsmProxyInvoker.class.getMethod("invoke", Object.class, Object[].class);
            String invokeDescriptor = JieJvm.getDescriptor(invoke);
            MethodVisitor methodVisitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
                invoke.getName(),
                invokeDescriptor,
                null,
                buildExceptions(invoke)
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, outerName);
            // ProxyInstance _this = (ProxyInstance) inst;
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 3);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, className, "index", "I");
            Label[] labels = new Label[methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            Label labelLast = new Label();
            methodVisitor.visitTableSwitchInsn(0, methods.size() - 1, labelLast, labels);
            int i = 0;
            for (Method method : methods) {
                methodVisitor.visitLabel(labels[i]);
                if (i++ == 0) {
                    methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{outerName}, 0, null);
                } else {
                    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
                //_this.invoke((Long) args[0], (String) args[1]);
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 3);
                int pIndex = 0;
                for (Parameter parameter : method.getParameters()) {
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
                    JieAsm.loadConst(methodVisitor, pIndex++);
                    methodVisitor.visitInsn(Opcodes.AALOAD);
                    JieAsm.unwrapVar(methodVisitor, parameter.getType(), true);
                }
                JieAsm.invokeVirtual(methodVisitor, method);
                Class<?> returnType = method.getReturnType();
                if (Jie.equals(returnType, void.class)) {
                    methodVisitor.visitInsn(Opcodes.ACONST_NULL);
                }
                JieAsm.visitReturn(methodVisitor, returnType, false);
            }
            methodVisitor.visitLabel(labelLast);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
        {
            Method invokeSuper = AsmProxyInvoker.class.getMethod("invokeSuper", Object.class, Object[].class);
            String invokeDescriptor = JieJvm.getDescriptor(invokeSuper);
            MethodVisitor methodVisitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
                invokeSuper.getName(),
                invokeDescriptor,
                null,
                buildExceptions(invokeSuper)
            );
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 1);
            methodVisitor.visitTypeInsn(Opcodes.CHECKCAST, outerName);
            // ProxyInstance _this = (ProxyInstance) inst;
            methodVisitor.visitVarInsn(Opcodes.ASTORE, 3);
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, className, "index", "I");
            Label[] labels = new Label[methods.size()];
            for (int i = 0; i < labels.length; i++) {
                labels[i] = new Label();
            }
            Label labelLast = new Label();
            methodVisitor.visitTableSwitchInsn(0, methods.size() - 1, labelLast, labels);
            int i = 0;
            for (Method method : methods) {
                methodVisitor.visitLabel(labels[i]);
                if (i++ == 0) {
                    methodVisitor.visitFrame(Opcodes.F_APPEND, 1, new Object[]{outerName}, 0, null);
                } else {
                    methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                }
                //_this.invoke((Long) args[0], (String) args[1]);
                methodVisitor.visitVarInsn(Opcodes.ALOAD, 3);
                int pIndex = 0;
                for (Parameter parameter : method.getParameters()) {
                    methodVisitor.visitVarInsn(Opcodes.ALOAD, 2);
                    JieAsm.loadConst(methodVisitor, pIndex++);
                    methodVisitor.visitInsn(Opcodes.AALOAD);
                    JieAsm.unwrapVar(methodVisitor, parameter.getType(), true);
                }
                methodVisitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    outerName,
                    "access$001",
                    JieJvm.getDescriptor(method),
                    false
                );
                Class<?> returnType = method.getReturnType();
                if (Jie.equals(returnType, void.class)) {
                    methodVisitor.visitInsn(Opcodes.ACONST_NULL);
                }
                JieAsm.visitReturn(methodVisitor, returnType, false);
            }
            methodVisitor.visitLabel(labelLast);
            methodVisitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
            methodVisitor.visitInsn(Opcodes.ACONST_NULL);
            methodVisitor.visitInsn(Opcodes.ARETURN);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private @Nonnull String @Nullable [] buildExceptions(Method method) {
        Class<?>[] exceptionTypes = method.getExceptionTypes();
        if (JieArray.isEmpty(exceptionTypes)) {
            return null;
        }
        return JieStream.stream(exceptionTypes).map(JieJvm::getInternalName).toArray(String[]::new);
    }
}
