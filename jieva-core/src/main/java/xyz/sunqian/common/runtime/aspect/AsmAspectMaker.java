package xyz.sunqian.common.runtime.aspect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.system.JvmKit;
import xyz.sunqian.common.base.value.IntVar;
import xyz.sunqian.common.runtime.asm.AsmKit;
import xyz.sunqian.common.runtime.reflect.BytesClassLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

/**
 * The <a href="https://asm.ow2.io/">ASM</a> implementation for {@link AspectMaker}. The runtime environment must have
 * asm package {@code org.objectweb.asm}.
 * <p>
 * This implementation uses inheritance to implement proxy, just like the keywords: {@code extends}. That means the
 * superclass, which is the advised class, cannot be {@code final} and must be inheritable, and must have an empty
 * constructor to ensure that the {@link AspectSpec#newInstance()} can execute correctly. And only public methods can be
 * advised and tested by {@link AspectHandler#shouldApplyAspect(Method)}.
 * <p>
 * Note the generated aspect class is {@code final}.
 *
 * @author sunqian
 */
@ThreadSafe
public class AsmAspectMaker implements AspectMaker {

    private static final @Nonnull String HANDLER_NAME = JvmKit.getInternalName(AspectHandler.class);
    private static final @Nonnull String HANDLER_DESCRIPTOR = JvmKit.getDescriptor(AspectHandler.class);
    private static final @Nonnull String METHODS_DESCRIPTOR = JvmKit.getDescriptor(Method[].class);
    private static final @Nonnull Method BEFORE_METHOD = Jie.uncheck(
        () -> AspectHandler.class.getMethod("beforeInvoking", Method.class, Object[].class, Object.class),
        AsmAspectException::new
    );
    private static final @Nonnull String BEFORE_DESCRIPTOR = JvmKit.getDescriptor(BEFORE_METHOD);
    private static final @Nonnull Method AFTER_METHOD = Jie.uncheck(
        () -> AspectHandler.class.getMethod("afterReturning", Object.class, Method.class, Object[].class, Object.class),
        AsmAspectException::new
    );
    private static final @Nonnull String AFTER_DESCRIPTOR = JvmKit.getDescriptor(AFTER_METHOD);
    private static final @Nonnull Method THROW_METHOD = Jie.uncheck(
        () -> AspectHandler.class.getMethod("afterThrowing", Throwable.class, Method.class, Object[].class, Object.class),
        AsmAspectException::new
    );
    private static final @Nonnull String THROW_DESCRIPTOR = JvmKit.getDescriptor(THROW_METHOD);
    private static final @Nonnull String METHOD_NAME = JvmKit.getInternalName(Method.class);
    private static final @Nonnull String ARGS_NAME = JvmKit.getInternalName(Object[].class);

    private static final @Nonnull AtomicLong classCounter = new AtomicLong();

    @Override
    public @Nonnull AspectSpec make(
        @Nonnull Class<?> advisedClass,
        @Nonnull AspectHandler aspectHandler
    ) throws AsmAspectException {
        try {
            Package pkg = AsmAspectMaker.class.getPackage();
            // aspect class internal name
            String aspectName = pkg.getName().replace('.', '/')
                + "/" + AsmKit.generateClassSimpleName(classCounter.incrementAndGet());
            // aspect class descriptor
            // String aspectDescriptor = "L" + aspectName + ";";
            String advisedName = JvmKit.getInternalName(advisedClass);
            // advised methods
            Map<Method, AspectMethodInfo> advisedMethodMap = new LinkedHashMap<>();
            IntVar methodCount = IntVar.of(0);
            for (Method method : advisedClass.getMethods()) {
                if (!canOverride(method)) {
                    continue;
                }
                if (!aspectHandler.shouldApplyAspect(method)) {
                    continue;
                }
                advisedMethodMap.put(
                    method,
                    buildAspectMethodInfo(
                        method
                        // advisedName,
                        // aspectDescriptor,
                        // methodCount.getAndIncrement()
                    )
                );
            }
            AspectClassInfo acInfo = new AspectClassInfo(
                aspectName,
                // aspectDescriptor,
                advisedName,
                new ArrayList<>(advisedMethodMap.values())
            );
            byte[] aspectClassBytes = generateAspectClass(acInfo);
            // using new class loader to help collect unused classes
            BytesClassLoader loader = new BytesClassLoader();
            Class<?> aspectClass = loader.loadClass(null, aspectClassBytes);
            return new AsmAspectSpec(
                aspectClass,
                advisedClass,
                aspectHandler,
                advisedMethodMap.keySet().toArray(new Method[0])
            );
        } catch (Exception e) {
            throw new AsmAspectException(e);
        }
    }

    private boolean canOverride(@Nonnull Method method) {
        if (method.isBridge()) {
            return false;
        }
        int modifiers = method.getModifiers();
        if (Modifier.isFinal(modifiers)) {
            return false;
        }
        if (Modifier.isStatic(modifiers)) {
            return false;
        }
        if (Modifier.isPublic(modifiers)) {
            return true;
        }
        return false;
    }

    private @Nonnull AspectMethodInfo buildAspectMethodInfo(
        @Nonnull Method method
        //@Nonnull String ownerName,
        //@Nonnull String aspectDescriptor,
        // int methodIndex
    ) {
        String descriptor = JvmKit.getDescriptor(method);
        String signature = JvmKit.getSignature(method);
        String[] exceptions = AsmKit.getExceptions(method);
        return new AspectMethodInfo(
            method,
            // ownerName,
            descriptor,
            signature,
            exceptions
        );
    }

    private byte @Nonnull [] generateAspectClass(@Nonnull AspectClassInfo pcInfo) throws Exception {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classWriter.visit(
            Opcodes.V1_8,
            Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
            pcInfo.aspectName,
            null,
            pcInfo.advisedName,
            null
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
                pcInfo.advisedName,
                AsmKit.CONSTRUCTOR_NAME,
                AsmKit.EMPTY_CONSTRUCTOR_DESCRIPTOR,
                false
            );
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 1);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.aspectName, "handler", HANDLER_DESCRIPTOR);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitVarInsn(Opcodes.ALOAD, 2);
            visitor.visitFieldInsn(Opcodes.PUTFIELD, pcInfo.aspectName, "methods", METHODS_DESCRIPTOR);
            visitor.visitInsn(Opcodes.RETURN);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
        int i = 0;
        for (AspectMethodInfo pmInfo : pcInfo.methods) {
            generateAspectMethod(classWriter, pcInfo, pmInfo, i);
            i++;
        }
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }

    private void generateAspectMethod(
        @Nonnull ClassWriter classWriter,
        @Nonnull AspectClassInfo acInfo,
        @Nonnull AspectMethodInfo amInfo,
        int i
    ) {
        MethodVisitor visitor = classWriter.visitMethod(
            Opcodes.ACC_PUBLIC,
            amInfo.method.getName(),
            amInfo.descriptor,
            amInfo.signature,
            amInfo.exceptions
        );

        boolean noReturn = Objects.equals(amInfo.method.getReturnType(), void.class);
        int localSlots = AsmKit.countParamSlots(amInfo.method);
        int handlerIndex = localSlots + 1;
        int methodIndex = handlerIndex + 1;
        int argsIndex = methodIndex + 1;
        int returnIndex = noReturn ? argsIndex : argsIndex + 1;

        Label labelStart = new Label();
        Label labelEnd = new Label();
        Label labelHandler = new Label();
        visitor.visitTryCatchBlock(labelStart, labelEnd, labelHandler, "java/lang/Throwable");
        {
            // Handler handler = this.handler;
            // Method method = this.methods[0];
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitFieldInsn(Opcodes.GETFIELD, acInfo.aspectName, "handler", HANDLER_DESCRIPTOR);
            visitor.visitVarInsn(Opcodes.ASTORE, handlerIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitFieldInsn(Opcodes.GETFIELD, acInfo.aspectName, "methods", METHODS_DESCRIPTOR);
            AsmKit.visitConst(visitor, i);
            visitor.visitInsn(Opcodes.AALOAD);
            visitor.visitVarInsn(Opcodes.ASTORE, methodIndex);
        }
        {
            // Object[] args = new Object[]{a};
            AsmKit.visitConst(visitor, amInfo.method.getParameterCount());
            visitor.visitTypeInsn(Opcodes.ANEWARRAY, AsmKit.OBJECT_NAME);
            int aIndex = 0;
            int pIndex = 1;
            for (Parameter parameter : amInfo.method.getParameters()) {
                // args[i] = param[i]
                visitor.visitInsn(Opcodes.DUP);
                AsmKit.visitConst(visitor, aIndex++);
                AsmKit.visitLoad(visitor, parameter.getType(), pIndex);
                AsmKit.wrapToObject(visitor, parameter.getType());
                visitor.visitInsn(Opcodes.AASTORE);
                pIndex += AsmKit.varSize(parameter.getType());
            }
            visitor.visitVarInsn(Opcodes.ASTORE, argsIndex);
        }
        visitor.visitLabel(labelStart);
        {
            // aspectHandler.beforeInvoking(methods[0], args, this);
            visitor.visitVarInsn(Opcodes.ALOAD, handlerIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, methodIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, argsIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                HANDLER_NAME,
                BEFORE_METHOD.getName(),
                BEFORE_DESCRIPTOR,
                true
            );
        }
        {
            // String ret = super.s1((String) args[0]);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            int pIndex = 0;
            for (Parameter parameter : amInfo.method.getParameters()) {
                // get args
                visitor.visitVarInsn(Opcodes.ALOAD, argsIndex);
                AsmKit.visitConst(visitor, pIndex++);
                // get args[pIndex]
                visitor.visitInsn(Opcodes.AALOAD);
                AsmKit.convertObjectTo(visitor, parameter.getType());
            }
            visitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                acInfo.advisedName,
                amInfo.method.getName(),
                amInfo.descriptor,
                false
            );
            if (!noReturn) {
                // visitor.visitVarInsn(Opcodes.ASTORE, returnIndex);
                AsmKit.visitStore(visitor, amInfo.method.getReturnType(), returnIndex);
            }
        }
        {
            // return (String) aspectHandler.afterReturning(ret, methods[0], args, this);
            visitor.visitVarInsn(Opcodes.ALOAD, handlerIndex);
            if (noReturn) {
                visitor.visitInsn(Opcodes.ACONST_NULL);
            } else {
                //visitor.visitVarInsn(Opcodes.ALOAD, returnIndex);
                AsmKit.visitLoad(visitor, amInfo.method.getReturnType(), returnIndex);
                // TODO
            }
            visitor.visitVarInsn(Opcodes.ALOAD, methodIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, argsIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                HANDLER_NAME,
                AFTER_METHOD.getName(),
                AFTER_DESCRIPTOR,
                true
            );
            if (noReturn) {
                visitor.visitInsn(Opcodes.POP);
            } else {
                AsmKit.convertObjectTo(visitor, amInfo.method.getReturnType());
            }
        }
        visitor.visitLabel(labelEnd);
        AsmKit.visitReturn(visitor, amInfo.method.getReturnType(), false, false);
        visitor.visitLabel(labelHandler);
        {
            // return (String) aspectHandler.afterThrowing(ex, methods[0], args, this);
            List<Object> localNames = new ArrayList<>(
                amInfo.method.getParameterCount() + 4 //+ (noReturn ? 0 : 1)
            );
            localNames.add(acInfo.aspectName);
            for (Parameter parameter : amInfo.method.getParameters()) {
                localNames.add(toFrameName(parameter.getType()));
            }
            localNames.add(HANDLER_NAME);
            localNames.add(METHOD_NAME);
            localNames.add(ARGS_NAME);
            // if (!noReturn) {
            //     localNames.add(toFrameName(amInfo.method.getReturnType()));
            // }
            visitor.visitFrame(
                Opcodes.F_FULL,
                localNames.size(),
                localNames.toArray(),
                1,
                new Object[]{"java/lang/Throwable"}
            );
            int exIndex = argsIndex + 1;
            visitor.visitVarInsn(Opcodes.ASTORE, exIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, handlerIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, exIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, methodIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, argsIndex);
            visitor.visitVarInsn(Opcodes.ALOAD, 0);
            visitor.visitMethodInsn(
                Opcodes.INVOKEINTERFACE,
                HANDLER_NAME,
                THROW_METHOD.getName(),
                THROW_DESCRIPTOR,
                true
            );
            if (noReturn) {
                visitor.visitInsn(Opcodes.POP);
            } else {
                AsmKit.convertObjectTo(visitor, amInfo.method.getReturnType());
            }
            AsmKit.visitReturn(visitor, amInfo.method.getReturnType(), false, false);
            visitor.visitMaxs(0, 0);
            visitor.visitEnd();
        }
    }

    private @Nonnull Object toFrameName(Class<?> type) {
        if (Objects.equals(type, boolean.class)
            || Objects.equals(type, byte.class)
            || Objects.equals(type, short.class)
            || Objects.equals(type, char.class)
            || Objects.equals(type, int.class)) {
            return Opcodes.INTEGER;
        }
        if (Objects.equals(type, long.class)) {
            return Opcodes.LONG;
        }
        if (Objects.equals(type, float.class)) {
            return Opcodes.FLOAT;
        }
        if (Objects.equals(type, double.class)) {
            return Opcodes.DOUBLE;
        }
        return JvmKit.getInternalName(type);
    }

    private static final class AspectClassInfo {

        private final @Nonnull String aspectName;
        // private final @Nonnull String aspectDescriptor;
        private final @Nonnull String advisedName;
        private final @Nonnull List<AspectMethodInfo> methods;

        private AspectClassInfo(
            @Nonnull String aspectName,
            //@Nonnull String aspectDescriptor,
            @Nonnull String advisedName,
            @Nonnull List<AspectMethodInfo> methods
        ) {
            this.aspectName = aspectName;
            // this.aspectDescriptor = aspectDescriptor;
            this.advisedName = advisedName;
            this.methods = methods;
        }
    }

    private static final class AspectMethodInfo {

        private final @Nonnull Method method;
        // private final @Nonnull String ownerName;
        private final @Nonnull String descriptor;
        private final @Nullable String signature;
        private final @Nonnull String @Nullable [] exceptions;

        private AspectMethodInfo(
            @Nonnull Method method,
            //@Nonnull String ownerName,
            @Nonnull String descriptor,
            @Nullable String signature,
            @Nonnull String @Nullable [] exceptions
        ) {
            this.method = method;
            // this.ownerName = ownerName;
            this.descriptor = descriptor;
            this.signature = signature;
            this.exceptions = exceptions;
        }
    }

    private static final class AsmAspectSpec implements AspectSpec {

        private final @Nonnull Class<?> aspectClass;
        private final @Nonnull Class<?> advisedClass;
        private final @Nonnull AspectHandler aspectHandler;
        private final @Nonnull Method @Nonnull [] methods;

        private AsmAspectSpec(
            @Nonnull Class<?> aspectClass,
            @Nonnull Class<?> advisedClass,
            @Nonnull AspectHandler aspectHandler,
            @Nonnull Method @Nonnull [] methods
        ) {
            this.aspectClass = aspectClass;
            this.advisedClass = advisedClass;
            this.aspectHandler = aspectHandler;
            this.methods = methods;
        }

        @Override
        public <T> @Nonnull T newInstance() throws AsmAspectException {
            return Jie.uncheck(() -> {
                Constructor<?> constructor = aspectClass.getConstructor(AspectHandler.class, Method[].class);
                return Jie.as(constructor.newInstance(aspectHandler, methods));
            }, AsmAspectException::new);
        }

        @Override
        public @Nonnull Class<?> aspectClass() {
            return aspectClass;
        }

        @Override
        public @Nonnull Class<?> advisedClass() {
            return advisedClass;
        }

        @Override
        public @Nonnull AspectHandler aspectHandler() {
            return aspectHandler;
        }
    }

    /**
     * This exception is the sub-exception of {@link AspectException} for <a href="https://asm.ow2.io/">ASM</a> proxy
     * implementation.
     *
     * @author sunqian
     */
    public static class AsmAspectException extends AspectException {
        /**
         * Constructs with the cause.
         *
         * @param cause the cause
         */
        public AsmAspectException(@Nullable Throwable cause) {
            super(cause);
        }
    }
}
