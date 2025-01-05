package xyz.sunqian.common.reflect.proxy;

import org.objectweb.asm.*;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;
import xyz.sunqian.common.base.JieString;
import xyz.sunqian.common.coll.JieColl;
import xyz.sunqian.common.reflect.JieJvm;
import xyz.sunqian.common.reflect.JieReflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <a href="https://asm.ow2.io/">ASM</a> implementation for {@link ProxyBuilder}. The runtime environment must have
 * asm package {@code org.objectweb.asm}.
 * <p>
 * This generator will create all public constructors with the same parameters as those in the proxied superclass (if
 * any), and the codes of these constructors will simply call the corresponding superclass constructors with the same
 * parameters. To ensure that the {@link ProxyClass#newInstance()} can execute correctly, it is recommended that the
 * superclass has a no-parameter constructor. For the proxy class which has no superclass (only interfaces), the
 * generator will auto create a constructor with no parameter.
 * <p>
 * This generator doesn't support custom class loader, and default {@link #isFinal(boolean)} is {@code true}.
 *
 * @author fredsuvn
 */
public class AsmProxyBuilder implements ProxyBuilder, Opcodes {

    private static final AtomicLong counter = new AtomicLong();

    private static final String PROXY_INVOKER_INTERNAL_NAME = JieJvm.getInternalName(ProxyInvoker.class);
    private static final String PROXY_INVOKER_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(ProxyInvoker[].class);
    private static final String METHOD_ARRAY_DESCRIPTOR = JieJvm.getDescriptor(Method[].class);
    private static final String METHOD_ARRAY_INTERNAL = JieJvm.getInternalName(Method[].class);
    private static final String OBJECT_INTERNAL_NAME = JieJvm.getInternalName(Object.class);
    private static final String PROXY_INVOKER_SIMPLE_NAME = ProxyInvoker.class.getSimpleName();
    private static final String CALL_SUPER_METHOD_DESCRIPTOR =
        "(I" + JieJvm.getDescriptor(Object.class) + JieJvm.getDescriptor(Object[].class) + JieJvm.getDescriptor($X.class) + ")"
            + JieJvm.getDescriptor(Object.class);
    private static final String CALL_SUPER_METHOD_NAME = "callSuper";
    private static final String CALL_VIRTUAL_METHOD_NAME = "callVirtual";

    private Class<?> superClass;
    private List<Class<?>> interfaces;
    private MethodProxyHandler handler;
    private boolean isFinal = true;
    private String className;

    @Override
    public ProxyBuilder superClass(Class<?> superClass) {
        this.superClass = superClass;
        return this;
    }

    @Override
    public ProxyBuilder interfaces(List<Class<?>> interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    @Override
    public ProxyBuilder proxyHandler(MethodProxyHandler handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public ProxyBuilder isFinal(boolean isFinal) {
        this.isFinal = isFinal;
        return this;
    }

    @Override
    public ProxyBuilder className(String className) {
        this.className = className;
        return this;
    }

    @Override
    public ProxyBuilder classLoader(ClassLoader classLoader) {
        return this;
    }

    @Override
    public ProxyClass build() throws ProxyException {
        try {
            return build0();
        } catch (ProxyException e) {
            throw e;
        } catch (Exception e) {
            throw new ProxyException(e);
        }
    }

    private ProxyClass build0() {
        if (superClass == null && JieColl.isEmpty(interfaces)) {
            throw new ProxyException("No super class or interface to proxy.");
        }
        if (handler == null) {
            throw new ProxyException("Need method proxy handler.");
        }
        String superInternalName = superClass == null ? OBJECT_INTERNAL_NAME : JieJvm.getInternalName(superClass);
        List<String> interfaceInternalNames = new LinkedList<>();
        if (interfaces == null) {
            interfaces = Collections.emptyList();
        }
        for (Class<?> anInterface : interfaces) {
            if (!anInterface.isInterface()) {
                throw new ProxyException("Not an interface: " + anInterface.getName() + ".");
            }
            interfaceInternalNames.add(JieJvm.getInternalName(anInterface));
        }
        String proxyClassName = className;
        if (JieString.isEmpty(proxyClassName)) {
            long count = counter.getAndIncrement();
            proxyClassName = getClass().getName() + "$Proxy$" + count;
        }
        String proxyInternalName = JieString.replace(proxyClassName, ".", "/");
        String proxyDescriptor = "L" + proxyInternalName + ";";
        // String invokerName = proxyClassName + "$" + PROXY_INVOKER_SIMPLE_NAME;
        String invokerInternalName = proxyInternalName + "$" + PROXY_INVOKER_SIMPLE_NAME;
        Map<String, Method> proxiedMethods = new LinkedHashMap<>();
        List<Class<?>> proxied = new LinkedList<>();
        if (superClass != null) {
            proxied.add(superClass);
        }
        proxied.addAll(interfaces);
        for (Class<?> upper : proxied) {
            Method[] methods = upper.getMethods();
            for (Method method : methods) {
                if (Modifier.isFinal(method.getModifiers()) || Modifier.isStatic(method.getModifiers())) {
                    continue;
                }
                if (!handler.needsProxy(method)) {
                    continue;
                }
                String methodInfo = method.getName() + ":" + JieJvm.getDescriptor(method);
                if (proxiedMethods.containsKey(methodInfo)) {
                    continue;
                }
                proxiedMethods.put(methodInfo, method);
            }
        }
        List<Method> methodList = new ArrayList<>(proxiedMethods.values());
        // Generates proxy class
        Generator proxyGenerator = new Generator(
            proxyInternalName,
            AsmMisc.generateSignature(proxied),
            superInternalName,
            interfaceInternalNames,
            invokerInternalName,
            methodList,
            isFinal
        );
        byte[] proxyBytecode = proxyGenerator.generateBytecode();
        Class<?> proxyClass = JieJvm.loadBytecode(proxyBytecode);
        // Generates invoker class
        InvokerGenerator invokerGenerator = new InvokerGenerator(
            proxyInternalName,
            proxyDescriptor,
            invokerInternalName
        );
        byte[] invokerBytecode = invokerGenerator.generateBytecode();
        Class<?> invokerClass = JieJvm.loadBytecode(invokerBytecode);
        return new AsmProxyClass(proxyClass, invokerClass, handler, methodList.toArray(new Method[0]));
    }

    private static final class AsmProxyClass implements ProxyClass {

        private final Class<?> proxyClass;
        private final Class<?> invokerClass;
        private final MethodProxyHandler handler;
        private final Method[] methods;

        private AsmProxyClass(
            Class<?> proxyClass, Class<?> invokerClass, MethodProxyHandler handler, Method[] methods) {
            this.proxyClass = proxyClass;
            this.invokerClass = invokerClass;
            this.handler = handler;
            this.methods = methods;
        }

        @Override
        public <T> T newInstance() throws ProxyException {
            try {
                Constructor<?> constructor =
                    JieReflect.getConstructor(proxyClass, Jie.array(MethodProxyHandler.class, Method[].class));
                return Jie.as(constructor.newInstance(handler, methods));
            } catch (Exception e) {
                throw new ProxyException(e);
            }
        }

        @Override
        public @Nullable Class<?> getProxyClass() {
            return proxyClass;
        }
    }

    private static final class Generator implements Opcodes {

        private static final String HANDLER_DESCRIPTOR = JieJvm.getDescriptor(MethodProxyHandler.class);
        private static final String HANDLER_INTERNAL = JieJvm.getInternalName(MethodProxyHandler.class);
        private static final String INIT_DESCRIPTOR =
            "(" + HANDLER_DESCRIPTOR + METHOD_ARRAY_DESCRIPTOR + ")V";

        private static final String HANDLER_METHOD_DESCRIPTOR = JieJvm.getDescriptor(JieReflect.getMethod(
            MethodProxyHandler.class, "invoke", Jie.array(Object.class, Method.class, Object[].class, ProxyInvoker.class))
        );

        private final String proxyInternalName;
        private final String proxySignature;
        private final String superInternalName;
        private final List<String> superInternalNames;
        private final String invokerInternalName;
        private final List<Method> methods;
        private final boolean isFinal;

        private Generator(
            String proxyInternalName,
            String proxySignature,
            String superInternalName,
            List<String> superInternalNames,
            String invokerInternalName,
            List<Method> methods,
            boolean isFinal
        ) {
            this.proxyInternalName = proxyInternalName;
            this.proxySignature = proxySignature;
            this.superInternalName = superInternalName;
            this.superInternalNames = superInternalNames;
            this.invokerInternalName = invokerInternalName;
            this.methods = methods;
            this.isFinal = isFinal;
        }

        public byte[] generateBytecode() {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            // Declaring
            {
                int access = ACC_PUBLIC | ACC_SUPER;
                if (isFinal) {
                    access = access | ACC_FINAL;
                }
                cw.visit(V1_8, access, proxyInternalName, proxySignature, superInternalName, superInternalNames.isEmpty() ? null : superInternalNames.toArray(new String[0]));
                cw.visitInnerClass(invokerInternalName, proxyInternalName, PROXY_INVOKER_SIMPLE_NAME, ACC_STATIC | ACC_SYNTHETIC);
            }

            // Generates fields
            {
                // private final MethodProxyHandler handler
                FieldVisitor fv = cw.visitField(ACC_PRIVATE, "handler", HANDLER_DESCRIPTOR, null, null);
                fv.visitEnd();
            }
            {
                // private Method[] methods
                FieldVisitor fv = cw.visitField(ACC_PRIVATE, "methods", METHOD_ARRAY_DESCRIPTOR, null, null);
                fv.visitEnd();
            }
            {
                // private ProxyInvoker[] invokers
                FieldVisitor fv = cw.visitField(ACC_PRIVATE, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR, null, null);
                fv.visitEnd();
            }

            // Generates constructor: (MethodProxyHandler handler, Method[] methods)
            {
                MethodVisitor cv = cw.visitMethod(ACC_PUBLIC, "<init>", INIT_DESCRIPTOR, null, null);
                // super();
                cv.visitVarInsn(ALOAD, 0);
                cv.visitMethodInsn(INVOKESPECIAL, superInternalName, "<init>", "()V", false);
                // this.handler = handler;
                cv.visitVarInsn(ALOAD, 0);
                cv.visitVarInsn(ALOAD, 1);
                cv.visitFieldInsn(PUTFIELD, proxyInternalName, "handler", HANDLER_DESCRIPTOR);
                // this.methods = methods;
                cv.visitVarInsn(ALOAD, 0);
                cv.visitVarInsn(ALOAD, 2);
                cv.visitFieldInsn(PUTFIELD, proxyInternalName, "methods", METHOD_ARRAY_DESCRIPTOR);
                // this.invokers = new ProxyInvoker[length];
                cv.visitVarInsn(ALOAD, 0);
                cv.visitVarInsn(ALOAD, 2);
                cv.visitInsn(ARRAYLENGTH);
                cv.visitTypeInsn(ANEWARRAY, PROXY_INVOKER_INTERNAL_NAME);
                cv.visitFieldInsn(PUTFIELD, proxyInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                // int i = 0
                cv.visitInsn(ICONST_0);
                cv.visitVarInsn(ISTORE, 3);
                // for-i
                Label forLabel = new Label();
                cv.visitLabel(forLabel);
                cv.visitFrame(F_FULL, 4, new Object[]{proxyInternalName, HANDLER_INTERNAL, METHOD_ARRAY_INTERNAL, INTEGER}, 0, new Object[]{});
                cv.visitVarInsn(ILOAD, 3);
                cv.visitVarInsn(ALOAD, 0);
                cv.visitFieldInsn(GETFIELD, proxyInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                cv.visitInsn(ARRAYLENGTH);
                Label breakLabel = new Label();
                // if(i >= invokers.length) break;
                cv.visitJumpInsn(IF_ICMPGE, breakLabel);
                // invokers[i] = new InvokerImpl(i);
                cv.visitVarInsn(ALOAD, 0);
                cv.visitFieldInsn(GETFIELD, proxyInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                cv.visitVarInsn(ILOAD, 3);
                cv.visitTypeInsn(NEW, invokerInternalName);
                cv.visitInsn(DUP);
                cv.visitVarInsn(ALOAD, 0);
                cv.visitVarInsn(ILOAD, 3);
                cv.visitMethodInsn(INVOKESPECIAL, invokerInternalName, "<init>", "(L" + proxyInternalName + ";I)V", false);
                cv.visitInsn(AASTORE);
                cv.visitIincInsn(3, 1);
                cv.visitJumpInsn(GOTO, forLabel);
                cv.visitLabel(breakLabel);
                cv.visitFrame(F_CHOP, 1, null, 0, null);
                cv.visitInsn(RETURN);
                // constructor.visitMaxs(6, 4);
                cv.visitMaxs(0, 0);
                cv.visitEnd();
            }

            // Generates override methods
            {
                int methodCount = 0;
                for (Method method : methods) {
                    MethodVisitor override = cw.visitMethod(ACC_PUBLIC, method.getName(), JieJvm.getDescriptor(method), null, null);
                    Parameter[] parameters = method.getParameters();
                    int extraLocalIndex = AsmMisc.countExtraLocalIndex(parameters);
                    // method = this.methods[j]
                    override.visitVarInsn(ALOAD, 0);
                    override.visitFieldInsn(GETFIELD, proxyInternalName, "methods", METHOD_ARRAY_DESCRIPTOR);
                    AsmMisc.visitPushNumber(override, methodCount);
                    override.visitInsn(AALOAD);
                    override.visitVarInsn(ASTORE, extraLocalIndex);
                    // Object args = new Object[]{};
                    AsmMisc.visitPushNumber(override, parameters.length);
                    override.visitTypeInsn(ANEWARRAY, OBJECT_INTERNAL_NAME);
                    int paramIndex = 1;
                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        override.visitInsn(DUP);
                        AsmMisc.visitPushNumber(override, i);
                        AsmMisc.visitLoadParamAsObject(override, parameter.getType(), paramIndex);
                        override.visitInsn(AASTORE);
                        if (Objects.equals(parameter.getType(), long.class) || Objects.equals(parameter.getType(), double.class)) {
                            paramIndex += 2;
                        } else {
                            paramIndex++;
                        }
                    }
                    override.visitVarInsn(ASTORE, extraLocalIndex + 1);
                    // invoker = this.invokers[i];
                    override.visitVarInsn(ALOAD, 0);
                    override.visitFieldInsn(GETFIELD, proxyInternalName, "invokers", PROXY_INVOKER_ARRAY_DESCRIPTOR);
                    AsmMisc.visitPushNumber(override, methodCount);
                    override.visitInsn(AALOAD);
                    override.visitVarInsn(ASTORE, extraLocalIndex + 2);
                    // handler
                    override.visitVarInsn(ALOAD, 0);
                    override.visitFieldInsn(GETFIELD, proxyInternalName, "handler", HANDLER_DESCRIPTOR);
                    // this
                    override.visitVarInsn(ALOAD, 0);
                    // method
                    override.visitVarInsn(ALOAD, extraLocalIndex);
                    // args
                    override.visitVarInsn(ALOAD, extraLocalIndex + 1);
                    // invoker
                    override.visitVarInsn(ALOAD, extraLocalIndex + 2);
                    // handler.invoke(this, method, args, invoker)
                    override.visitMethodInsn(INVOKEINTERFACE, HANDLER_INTERNAL, "invoke", HANDLER_METHOD_DESCRIPTOR, true);
                    if (Objects.equals(method.getReturnType(), void.class)) {
                        override.visitInsn(RETURN);
                    } else {
                        AsmMisc.visitObjectCast(override, method.getReturnType(), true);
                    }
                    // override.visitMaxs(5, extraLocalIndex + 3);
                    override.visitMaxs(0, 0);
                    override.visitEnd();
                    methodCount++;
                }
            }

            // Generate callSuper(i, inst, args, null)
            {
                Label[] caseLabels = methods.stream().map(it -> new Label()).toArray(Label[]::new);
                MethodVisitor callSuper = cw.visitMethod(ACC_PUBLIC, CALL_SUPER_METHOD_NAME, CALL_SUPER_METHOD_DESCRIPTOR, null, null);
                callSuper.visitVarInsn(ILOAD, 1);
                Label switchLabel = new Label();
                callSuper.visitTableSwitchInsn(0, methods.size() - 1, switchLabel, caseLabels);
                int i = 0;
                for (Method method : methods) {
                    callSuper.visitLabel(caseLabels[i]);
                    callSuper.visitFrame(F_SAME, 0, null, 0, null);
                    callSuper.visitVarInsn(ALOAD, 0);
                    // callSuper.visitTypeInsn(CHECKCAST, JieJvm.getInternalName(method.getDeclaringClass()));
                    // callSuper.visitTypeInsn(Opcodes.CHECKCAST, JieJvm.getInternalName(method.getDeclaringClass()));
                    Class<?>[] params = method.getParameterTypes();
                    for (int j = 0; j < params.length; j++) {
                        callSuper.visitVarInsn(ALOAD, 3);
                        AsmMisc.visitPushNumber(callSuper, j);
                        callSuper.visitInsn(AALOAD);
                        AsmMisc.visitObjectCast(callSuper, params[j], false);
                    }
                    String methodOwnerInternalName = JieJvm.getInternalName(method.getDeclaringClass());
                    boolean hasOwner = Objects.equals(methodOwnerInternalName, superInternalName) || superInternalNames.contains(methodOwnerInternalName);
                    if (hasOwner) {
                        callSuper.visitMethodInsn(INVOKESPECIAL, JieJvm.getInternalName(method.getDeclaringClass()), method.getName(), JieJvm.getDescriptor(method), method.getDeclaringClass().isInterface());
                    } else {
                        callSuper.visitMethodInsn(INVOKESPECIAL, superInternalName, method.getName(), JieJvm.getDescriptor(method), false);
                    }
                    AsmMisc.returnCastObject(callSuper, method.getReturnType());
                    i++;
                }
                callSuper.visitLabel(switchLabel);
                callSuper.visitFrame(F_SAME, 0, null, 0, null);
                callSuper.visitInsn(ACONST_NULL);
                // stackCount.increment();
                callSuper.visitInsn(ARETURN);
                // callSuper.visitMaxs(0, 5);
                callSuper.visitMaxs(0, 0);
                callSuper.visitEnd();
            }

            // Generate callVirtual(i, inst, args, null)
            {
                Label[] caseLabels = methods.stream().map(it -> new Label()).toArray(Label[]::new);
                MethodVisitor callVirtual = cw.visitMethod(ACC_PUBLIC, CALL_VIRTUAL_METHOD_NAME, CALL_SUPER_METHOD_DESCRIPTOR, null, null);
                callVirtual.visitVarInsn(ILOAD, 1);
                Label switchLabel = new Label();
                callVirtual.visitTableSwitchInsn(0, methods.size() - 1, switchLabel, caseLabels);
                int i = 0;
                for (Method method : methods) {
                    callVirtual.visitLabel(caseLabels[i]);
                    callVirtual.visitFrame(F_SAME, 0, null, 0, null);
                    callVirtual.visitVarInsn(ALOAD, 2);
                    callVirtual.visitTypeInsn(CHECKCAST, JieJvm.getInternalName(method.getDeclaringClass()));
                    Class<?>[] params = method.getParameterTypes();
                    for (int j = 0; j < params.length; j++) {
                        callVirtual.visitVarInsn(ALOAD, 3);
                        AsmMisc.visitPushNumber(callVirtual, j);
                        callVirtual.visitInsn(AALOAD);
                        AsmMisc.visitObjectCast(callVirtual, params[j], false);
                    }
                    if (method.getDeclaringClass().isInterface()) {
                        callVirtual.visitMethodInsn(INVOKEINTERFACE, JieJvm.getInternalName(method.getDeclaringClass()), method.getName(), JieJvm.getDescriptor(method), true);
                    } else {
                        callVirtual.visitMethodInsn(INVOKEVIRTUAL, JieJvm.getInternalName(method.getDeclaringClass()), method.getName(), JieJvm.getDescriptor(method), false);
                    }
                    AsmMisc.returnCastObject(callVirtual, method.getReturnType());
                    i++;
                }
                callVirtual.visitLabel(switchLabel);
                callVirtual.visitFrame(F_SAME, 0, null, 0, null);
                callVirtual.visitInsn(ACONST_NULL);
                callVirtual.visitInsn(ARETURN);
                // callVirtual.visitMaxs(2 + 2 * maxParameterCount, 5);
                callVirtual.visitMaxs(0, 0);
                callVirtual.visitEnd();
            }

            return cw.toByteArray();
        }
    }

    private static final class InvokerGenerator implements Opcodes {

        private static final String INVOKE_METHOD_DESCRIPTOR = JieJvm.getDescriptor(
            JieReflect.getMethod(ProxyInvoker.class, "invoke", Jie.array(Object.class, Object[].class))
        );
        private static final String INVOKE_SUPER_METHOD_DESCRIPTOR = JieJvm.getDescriptor(
            JieReflect.getMethod(ProxyInvoker.class, "invokeSuper", Jie.array(Object.class, Object[].class))
        );

        private final String proxyInternalName;
        private final String proxyDescriptor;
        private final String invokerInternalName;

        private InvokerGenerator(
            String proxyInternalName,
            String proxyDescriptor,
            String invokerInternalName
        ) {
            this.proxyInternalName = proxyInternalName;
            this.proxyDescriptor = proxyDescriptor;
            this.invokerInternalName = invokerInternalName;
        }

        public byte[] generateBytecode() {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

            // Declaring: Inner class: implements ProxyInvoker
            {
                cw.visit(V1_8, ACC_PRIVATE | ACC_FINAL, invokerInternalName, null, OBJECT_INTERNAL_NAME, new String[]{PROXY_INVOKER_INTERNAL_NAME});
                cw.visitInnerClass(invokerInternalName, proxyInternalName, PROXY_INVOKER_SIMPLE_NAME, ACC_STATIC | ACC_SYNTHETIC);
            }

            // Generates fields:
            {
                // private final int i;
                FieldVisitor fv = cw.visitField(ACC_PRIVATE | ACC_FINAL, "i", "I", null, null);
                fv.visitEnd();
            }
            {
                // Out.this
                FieldVisitor fv = cw.visitField(ACC_FINAL | ACC_SYNTHETIC, "this$0", proxyDescriptor, null, null);
                fv.visitEnd();
            }

            // Generates constructor: (int i)
            {
                MethodVisitor cv = cw.visitMethod(ACC_SYNTHETIC, "<init>", "(" + proxyDescriptor + "I)V", null, null);
                // this.this$0 = out;
                cv.visitVarInsn(ALOAD, 0);
                cv.visitVarInsn(ALOAD, 1);
                cv.visitFieldInsn(PUTFIELD, invokerInternalName, "this$0", proxyDescriptor);
                // super();
                cv.visitVarInsn(ALOAD, 0);
                cv.visitMethodInsn(INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
                // this.i = i;
                cv.visitVarInsn(ALOAD, 0);
                cv.visitVarInsn(ILOAD, 2);
                cv.visitFieldInsn(PUTFIELD, invokerInternalName, "i", "I");
                cv.visitInsn(RETURN);
                // cmv.visitMaxs(2, 3);
                cv.visitMaxs(0, 0);
                cv.visitEnd();
            }

            // Generates methods:
            {
                // invoke(Object inst, Object[] args): return proxy.callVirtual(i, inst, args, null);
                MethodVisitor invoke = cw.visitMethod(ACC_PUBLIC, "invoke", INVOKE_METHOD_DESCRIPTOR, null, null);
                invoke.visitVarInsn(ALOAD, 0);
                invoke.visitFieldInsn(GETFIELD, invokerInternalName, "this$0", proxyDescriptor);
                invoke.visitVarInsn(ALOAD, 0);
                invoke.visitFieldInsn(GETFIELD, invokerInternalName, "i", "I");
                invoke.visitVarInsn(ALOAD, 1);
                invoke.visitVarInsn(ALOAD, 2);
                invoke.visitInsn(ACONST_NULL);
                invoke.visitMethodInsn(INVOKEVIRTUAL, proxyInternalName, CALL_VIRTUAL_METHOD_NAME, CALL_SUPER_METHOD_DESCRIPTOR, false);
                invoke.visitInsn(ARETURN);
                // invoke.visitMaxs(5, 3);
                invoke.visitMaxs(0, 0);
                invoke.visitEnd();
            }
            {
                // invokeSuper(Object inst, Object[] args): return proxy.callSuper(i, inst, args, null);
                MethodVisitor invokeSuper = cw.visitMethod(ACC_PUBLIC, "invokeSuper", INVOKE_SUPER_METHOD_DESCRIPTOR, null, null);
                invokeSuper.visitVarInsn(ALOAD, 0);
                invokeSuper.visitFieldInsn(GETFIELD, invokerInternalName, "this$0", proxyDescriptor);
                invokeSuper.visitVarInsn(ALOAD, 0);
                invokeSuper.visitFieldInsn(GETFIELD, invokerInternalName, "i", "I");
                invokeSuper.visitVarInsn(ALOAD, 1);
                invokeSuper.visitVarInsn(ALOAD, 2);
                // invokeSuper.visitInsn(DUP);
                // invokeSuper.visitVarInsn(ASTORE, 2);
                // invokeSuper.visitVarInsn(ALOAD, 0);
                // invokeSuper.visitFieldInsn(GETFIELD, invokerInternalName, "i", "I");
                // invokeSuper.visitVarInsn(ALOAD, 2);
                // invokeSuper.visitVarInsn(ALOAD, 1);
                invokeSuper.visitInsn(ACONST_NULL);
                invokeSuper.visitMethodInsn(INVOKEVIRTUAL, proxyInternalName, CALL_SUPER_METHOD_NAME, CALL_SUPER_METHOD_DESCRIPTOR, false);
                invokeSuper.visitInsn(ARETURN);
                // invokeSuper.visitMaxs(5, 3);
                invokeSuper.visitMaxs(0, 0);
                invokeSuper.visitEnd();
            }

            return cw.toByteArray();
        }
    }

    private static final class $X {}
}
