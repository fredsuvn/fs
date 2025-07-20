package xyz.sunqian.common.reflect.proxy;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.asm.AsmKit;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.reflect.BytesClassLoader;
import xyz.sunqian.common.reflect.ClassKit;
import xyz.sunqian.common.reflect.JvmKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The <a href="https://asm.ow2.io/">ASM</a> implementation for {@link ProxyClassGenerator}. The runtime environment
 * must have asm package {@code org.objectweb.asm}.
 * <p>
 * This generator uses inheritance to implement proxy class, just like the keywords: {@code extends} and
 * {@code implements}. That means the superclass, which is the proxied class, cannot be {@code final} and must be
 * {@code public}, and must have an empty constructor to ensure that the {@link ProxyClass#newInstance()} can execute
 * correctly.
 * <p>
 * Note the generated proxy class is {@code final}.
 *
 * @author sunqian
 */
@ThreadSafe
public class AsmProxyClassGenerator implements ProxyClassGenerator {

    private static final @Nonnull String INVOKER_NAME = JvmKit.getInternalName(ProxyInvoker.class);
    private static final @Nonnull String INVOKERS_DESCRIPTOR = JvmKit.getDescriptor(ProxyInvoker[].class);
    private static final @Nonnull String HANDLER_NAME = JvmKit.getInternalName(ProxyMethodHandler.class);
    private static final @Nonnull String HANDLER_DESCRIPTOR = JvmKit.getDescriptor(ProxyMethodHandler.class);
    private static final @Nonnull String METHODS_DESCRIPTOR = JvmKit.getDescriptor(Method[].class);
    private static final @Nonnull String INVOKER_SIMPLE_NAME = "AsmInvoker";
    private static final @Nonnull String SUPER_INVOKER_NAME_PREFIX = "access$super$";
    private static final @Nonnull Method HANDLER_INVOKE = Jie.uncheck(
        () -> ProxyMethodHandler.class.getMethod("invoke", Object.class, Method.class, ProxyInvoker.class, Object[].class),
        AsmProxyException::new
    );
    private static final @Nonnull String HANDLER_INVOKE_DESCRIPTOR = JvmKit.getDescriptor(HANDLER_INVOKE);
    private static final @Nonnull Method INVOKER_INVOKE = Jie.uncheck(
        () -> ProxyInvoker.class.getMethod("invoke", Object.class, Object[].class),
        AsmProxyException::new
    );
    private static final @Nonnull String INVOKER_INVOKE_DESCRIPTOR = JvmKit.getDescriptor(INVOKER_INVOKE);
    private static final @Nonnull String @Nullable [] INVOKER_INVOKE_EXCEPTIONS = AsmKit.getExceptions(INVOKER_INVOKE);
    private static final @Nonnull Method INVOKER_INVOKE_SUPER = Jie.uncheck(
        () -> ProxyInvoker.class.getMethod("invokeSuper", Object.class, Object[].class),
        AsmProxyException::new
    );
    private static final @Nonnull String INVOKER_INVOKE_SUPER_DESCRIPTOR = JvmKit.getDescriptor(INVOKER_INVOKE_SUPER);
    private static final @Nonnull String @Nullable [] INVOKER_INVOKE_SUPER_EXCEPTIONS = AsmKit.getExceptions(INVOKER_INVOKE_SUPER);

    private static final @Nonnull AtomicLong classCounter = new AtomicLong();

    @Override
    public @Nonnull ProxyClass generate(
        @Nullable Class<?> proxiedClass,
        @Nonnull List<Class<?>> interfaces,
        @Nonnull ProxyMethodHandler methodHandler
    ) throws AsmProxyException {
        Package pkg = AsmProxyClassGenerator.class.getPackage();
        String outerName = pkg.getName().replace('.', '/')
            + "/" + AsmKit.generateClassSimpleName(classCounter.incrementAndGet());
        String outerDescriptor = "L" + outerName + ";";
        Class<?> outerSuperClass = proxiedClass == null ? Object.class : proxiedClass;
        String outerSuperName = JvmKit.getInternalName(outerSuperClass);
        String[] outerInterfaces = {};
        if (!interfaces.isEmpty()) {
            outerInterfaces = interfaces.stream().map(JvmKit::getInternalName).toArray(String[]::new);
        }
        String innerName = INVOKER_SIMPLE_NAME;
        String fullInnerName = outerName + "$" + innerName;
        Map<Method, ProxyMethodInfo> methodMap = new LinkedHashMap<>();
        IntVar methodCount = IntVar.of(0);
        for (Method method : outerSuperClass.getDeclaredMethods()) {
            if (canBeProxied(method, methodHandler)) {
                methodMap.put(
                    method,
                    buildProxyMethodInfo(method, outerSuperName, outerDescriptor, false, methodCount)
                );
            }
        }
        filterProxiedMethods(
            methodMap,
            outerSuperClass.getMethods(),
            methodHandler,
            outerSuperName,
            outerDescriptor,
            false,
            methodCount
        );
        int ii = 0;
        for (Class<?> anInterface : interfaces) {
            filterProxiedMethods(
                methodMap,
                anInterface.getMethods(),
                methodHandler,
                outerInterfaces[ii++],
                outerDescriptor,
                true,
                methodCount
            );
        }
        ProxyClassInfo pcInfo = new ProxyClassInfo(
            outerName,
            outerDescriptor,
            outerSuperName,
            outerInterfaces,
            innerName,
            fullInnerName,
            new ArrayList<>(methodMap.values())
        );
        return Jie.uncheck(() -> {
            byte[] proxyClassBytes = generateProxyClass(pcInfo);
            byte[] invokerClassBytes = generateInvokerClass(pcInfo);
            // using new class loader to help collect unused classes
            BytesClassLoader loader = new BytesClassLoader();
            Class<?> proxyClass = loader.loadClass(null, proxyClassBytes);
            loader.loadClass(null, invokerClassBytes);
            return new AsmProxyClass(
                proxyClass,
                methodHandler,
                methodMap.keySet().toArray(new Method[0])
            );
        }, AsmProxyException::new);
    }

    private void filterProxiedMethods(
        @Nonnull Map<Method, ProxyMethodInfo> methodMap,
        @Nonnull Method @Nonnull [] methods,
        @Nonnull ProxyMethodHandler methodHandler,
        @Nonnull String ownerName,
        @Nonnull String outerDescriptor,
        boolean isInterface,
        @Nonnull IntVar methodCount
    ) {
        for (Method method : methods) {
            if (methodMap.containsKey(method)) {
                continue;
            }
            if (canBeProxied(method, methodHandler)) {
                methodMap.put(
                    method,
                    buildProxyMethodInfo(method, ownerName, outerDescriptor, isInterface, methodCount)
                );
            }
        }
    }

    private @Nonnull ProxyMethodInfo buildProxyMethodInfo(
        @Nonnull Method method,
        @Nonnull String ownerName,
        @Nonnull String outerDescriptor,
        boolean isInterface,
        @Nonnull IntVar methodCount
    ) {
        String descriptor = JvmKit.getDescriptor(method);
        String signature = JvmKit.getSignature(method);
        String[] exceptions = AsmKit.getExceptions(method);
        String superInvokerName = SUPER_INVOKER_NAME_PREFIX + methodCount.getAndIncrement();// access$001
        String superInvokerDescriptor = descriptor.replace("(", "(" + outerDescriptor);
        String superInvokerSignature =
            signature == null ? null : signature.replace("(", "(" + outerDescriptor);
        return new ProxyMethodInfo(
            method,
            ownerName,
            descriptor,
            signature,
            exceptions,
            superInvokerName,
            superInvokerDescriptor,
            superInvokerSignature,
            isInterface
        );
    }

    private boolean canBeProxied(Method method, ProxyMethodHandler handler) {
        if (method.isSynthetic()) {
            return false;
        }
        int mod = method.getModifiers();
        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
            return false;
        }
        return handler.requiresProxy(method);
    }

    private byte @Nonnull [] generateProxyClass(@Nonnull ProxyClassInfo pcInfo) throws Exception {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER,
            pcInfo.outerName,
            null,
            pcInfo.outerSuperName,
            pcInfo.outerInterfaces
        );
        classWriter.visitInnerClass(
            pcInfo.fullInnerName,
            pcInfo.outerName,
            pcInfo.innerName,
            Opcodes.ACC_PRIVATE
        );
        {
            FieldVisitor visitor = classWriter.visitField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "handler",
                HANDLER_DESCRIPTOR,
                null,
                null
            );
            visitor.visitEnd();
        }
        {
            FieldVisitor visitor = classWriter.visitField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "methods",
                METHODS_DESCRIPTOR,
                null,
                null
            );
            visitor.visitEnd();
        }
        {
            FieldVisitor visitor = classWriter.visitField(
                Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL,
                "invokers",
                INVOKERS_DESCRIPTOR,
                null,
                null
            );
            visitor.visitEnd();
        }
        {
            MethodVisitor visitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC,
                AsmKit.CONSTRUCTOR_NAME,
                "(" + HANDLER_DESCRIPTOR + METHODS_DESCRIPTOR + ")V",
                null,
                null
            );
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                pcInfo.outerSuperName,
                AsmKit.CONSTRUCTOR_NAME,
                AsmKit.EMPTY_CONSTRUCTOR_DESCRIPTOR,
                false
            );
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.outerName, "handler", HANDLER_DESCRIPTOR);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 2);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.outerName, "methods", METHODS_DESCRIPTOR);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 2);
            visitor.visitInsn(Opcodes.ARRAYLENGTH);
            visitor.visitTypeInsn(Opcodes.ANEWARRAY, INVOKER_NAME);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.outerName, "invokers", INVOKERS_DESCRIPTOR);
            visitor.visitInsn(Opcodes.RETURN);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
        int i = 0;
        for (ProxyMethodInfo pmInfo : pcInfo.methods) {
            generateProxyMethod(classWriter, pcInfo, pmInfo, i);
            generateSuperInvoker(classWriter, pmInfo);
            i++;
        }
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private void generateProxyMethod(
        @Nonnull ClassWriter classWriter,
        @Nonnull ProxyClassInfo pcInfo,
        @Nonnull ProxyMethodInfo pmInfo,
        int i
    ) throws Exception {
        MethodVisitor visitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC,
            pmInfo.method.getName(),
            pmInfo.descriptor,
            pmInfo.signature,
            pmInfo.exceptions
        );
        // ProxyInvoker invoker = invokers[i];
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.outerName, "invokers", INVOKERS_DESCRIPTOR);
        AsmKit.loadConst(visitor, i);
        visitor.visitInsn(Opcodes.AALOAD);
        int invokerPos = AsmKit.paramSize(pmInfo.method.getParameters()) + 1;
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
        AsmKit.loadConst(visitor, i);
        visitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            pcInfo.fullInnerName,
            AsmKit.CONSTRUCTOR_NAME,
            "(" + pcInfo.outerDescriptor + "I)V",
            false
        );
        visitor.visitVarInsn(Opcodes.ASTORE, invokerPos);
        // get invokers
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.outerName, "invokers", INVOKERS_DESCRIPTOR);
        // invokers[i] = invoker;
        AsmKit.loadConst(visitor, i);
        visitor.visitVarInsn(Opcodes.ALOAD, invokerPos);
        visitor.visitInsn(Opcodes.AASTORE);
        // endif
        visitor.visitLabel(ifNonnull);
        visitor.visitFrame(Opcodes.F_APPEND, 1, new String[]{INVOKER_NAME}, 0, null);
        // get handler
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.outerName, "handler", HANDLER_DESCRIPTOR);
        // get this
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        // get methods[i]
        visitor.visitVarInsn(Opcodes.ALOAD, 0);
        visitor.visitFieldInsn(Opcodes.GETFIELD, pcInfo.outerName, "methods", METHODS_DESCRIPTOR);
        AsmKit.loadConst(visitor, i);
        visitor.visitInsn(Opcodes.AALOAD);
        // get invoker
        visitor.visitVarInsn(Opcodes.ALOAD, invokerPos);
        // new Object[params.size]
        AsmKit.loadConst(visitor, pmInfo.method.getParameterCount());
        visitor.visitTypeInsn(Opcodes.ANEWARRAY, AsmKit.OBJECT_NAME);
        // set array
        int aIndex = 0;
        int pIndex = 1;
        for (Parameter parameter : pmInfo.method.getParameters()) {
            // array[i] = param[i]
            visitor.visitInsn(Opcodes.DUP);
            AsmKit.loadConst(visitor, aIndex++);
            AsmKit.loadVar(visitor, parameter.getType(), pIndex);
            AsmKit.wrapToObject(visitor, parameter.getType());
            visitor.visitInsn(Opcodes.AASTORE);
            pIndex += AsmKit.varSize(parameter.getType());
        }
        // return handler.invoke(this, methods[0], invoker, args);
        visitor.visitMethodInsn(
            Opcodes.INVOKEINTERFACE,
            HANDLER_NAME,
            HANDLER_INVOKE.getName(),
            HANDLER_INVOKE_DESCRIPTOR,
            true
        );
        // object -> primitive/object
        AsmKit.convertObjectTo(visitor, pmInfo.method.getReturnType());
        AsmKit.visitReturn(visitor, pmInfo.method.getReturnType(), true, false);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private void generateSuperInvoker(
        @Nonnull ClassWriter classWriter,
        @Nonnull ProxyMethodInfo pmInfo
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
            AsmKit.loadVar(visitor, parameter.getType(), pIndex);
            pIndex += AsmKit.varSize(parameter.getType());
        }
        visitor.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            pmInfo.ownerName,
            pmInfo.method.getName(),
            pmInfo.descriptor,
            pmInfo.isInterface
        );
        AsmKit.visitReturn(visitor, pmInfo.method.getReturnType(), false, false);
        visitor.visitMaxs(0, 0);
        visitor.visitEnd();
    }

    private byte @Nonnull [] generateInvokerClass(@Nonnull ProxyClassInfo pcInfo) throws Exception {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_SUPER,
            pcInfo.fullInnerName,
            null,
            AsmKit.OBJECT_NAME,
            new String[]{INVOKER_NAME}
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
                AsmKit.CONSTRUCTOR_NAME,
                "(" + pcInfo.outerDescriptor + "I)V",
                null,
                null
            );
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.fullInnerName, "this$0", pcInfo.outerDescriptor);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, AsmKit.OBJECT_NAME, AsmKit.CONSTRUCTOR_NAME, AsmKit.EMPTY_CONSTRUCTOR_DESCRIPTOR, false);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ILOAD, 2);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.fullInnerName, "index", "I");
            visitor.visitInsn(Opcodes.RETURN);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
        {
            // invoke(Object inst, Object... args);
            MethodVisitor visitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
                INVOKER_INVOKE.getName(),
                INVOKER_INVOKE_DESCRIPTOR,
                null,
                INVOKER_INVOKE_EXCEPTIONS
            );
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
                Method method = pmInfo.method;
                String methodOwnerName = pmInfo.ownerName;
                if (ClassKit.isProtected(method)) {
                    methodOwnerName = pcInfo.outerName;
                }
                visitor.visitLabel(labels[i++]);
                visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                // get outer
                visitor.visitVarInsn(Opcodes.ALOAD, 1);
                visitor.visitTypeInsn(Opcodes.CHECKCAST, methodOwnerName);
                // loads args
                loadParameters(visitor, pmInfo.method);
                // return outer.invoke(args0, args1...);
                AsmKit.invokeVirtual(
                    visitor, methodOwnerName, method.getName(), pmInfo.descriptor, pmInfo.isInterface
                );
                Class<?> returnType = AsmKit.wrapToObject(visitor, method.getReturnType());
                AsmKit.visitReturn(visitor, returnType, false, true);
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
            MethodVisitor visitor = classWriter.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_VARARGS,
                INVOKER_INVOKE_SUPER.getName(),
                INVOKER_INVOKE_SUPER_DESCRIPTOR,
                null,
                INVOKER_INVOKE_SUPER_EXCEPTIONS
            );
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
                visitor.visitLabel(labels[i++]);
                visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
                // get outer
                visitor.visitVarInsn(Opcodes.ALOAD, 1);
                visitor.visitTypeInsn(Opcodes.CHECKCAST, pcInfo.outerName);
                // loads args
                loadParameters(visitor, pmInfo.method);
                // return outer.invoke(inst, args0, args1...);
                visitor.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    pcInfo.outerName,
                    pmInfo.superInvokerName,//"access$001",
                    pmInfo.superInvokerDescriptor,
                    false
                );
                Class<?> returnType = AsmKit.wrapToObject(visitor, pmInfo.method.getReturnType());
                AsmKit.visitReturn(visitor, returnType, false, true);
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

    private void loadParameters(MethodVisitor visitor, Executable executable) {
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

    private static final class ProxyClassInfo {

        private final @Nonnull String outerName;
        private final @Nonnull String outerDescriptor;
        private final @Nonnull String outerSuperName;
        private final @Nonnull String @Nullable [] outerInterfaces;
        private final @Nonnull String innerName;
        private final @Nonnull String fullInnerName;
        private final @Nonnull List<ProxyMethodInfo> methods;

        private ProxyClassInfo(
            @Nonnull String outerName,
            @Nonnull String outerDescriptor,
            @Nonnull String outerSuperName,
            @Nonnull String @Nullable [] outerInterfaces,
            @Nonnull String innerName,
            @Nonnull String fullInnerName,
            @Nonnull List<ProxyMethodInfo> methods
        ) {
            this.outerName = outerName;
            this.outerDescriptor = outerDescriptor;
            this.outerSuperName = outerSuperName;
            this.outerInterfaces = outerInterfaces;
            this.innerName = innerName;
            this.fullInnerName = fullInnerName;
            this.methods = methods;
        }
    }

    private static final class ProxyMethodInfo {

        private final @Nonnull Method method;
        private final @Nonnull String ownerName;
        private final @Nonnull String descriptor;
        private final @Nullable String signature;
        private final @Nonnull String @Nullable [] exceptions;
        private final @Nonnull String superInvokerName;
        private final @Nonnull String superInvokerDescriptor;
        private final @Nullable String superInvokerSignature;
        private final boolean isInterface;

        private ProxyMethodInfo(
            @Nonnull Method method,
            @Nonnull String ownerName,
            @Nonnull String descriptor,
            @Nullable String signature,
            @Nonnull String @Nullable [] exceptions,
            @Nonnull String superInvokerName,
            @Nonnull String superInvokerDescriptor,
            @Nullable String superInvokerSignature,
            boolean isInterface
        ) {
            this.method = method;
            this.ownerName = ownerName;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
            this.superInvokerName = superInvokerName;
            this.superInvokerDescriptor = superInvokerDescriptor;
            this.superInvokerSignature = superInvokerSignature;
            this.isInterface = isInterface;
        }
    }

    private static final class AsmProxyClass implements ProxyClass {

        private final @Nonnull Class<?> proxyClass;
        private final @Nonnull ProxyMethodHandler handler;
        private final @Nonnull Method @Nonnull [] methods;

        private AsmProxyClass(
            @Nonnull Class<?> proxyClass,
            @Nonnull ProxyMethodHandler handler,
            @Nonnull Method @Nonnull [] methods
        ) {
            this.proxyClass = proxyClass;
            this.handler = handler;
            this.methods = methods;
        }

        @Override
        public <T> @Nonnull T newInstance() throws AsmProxyException {
            return Jie.uncheck(() -> {
                Constructor<?> constructor = proxyClass.getConstructor(ProxyMethodHandler.class, Method[].class);
                return Jie.as(constructor.newInstance(handler, methods));
            }, AsmProxyException::new);
        }

        @Override
        public @Nonnull Class<?> getProxyClass() {
            return proxyClass;
        }
    }

    /**
     * This exception is the sub-exception of {@link ProxyException} for <a href="https://asm.ow2.io/">ASM</a> proxy
     * implementation.
     *
     * @author sunqian
     */
    public static class AsmProxyException extends ProxyException {
        /**
         * Constructs with the cause.
         *
         * @param cause the cause
         */
        public AsmProxyException(@Nullable Throwable cause) {
            super(cause);
        }
    }
}
