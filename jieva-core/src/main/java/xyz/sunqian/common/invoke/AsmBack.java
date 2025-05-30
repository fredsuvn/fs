package xyz.sunqian.common.invoke;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.springframework.asm.RecordComponentVisitor;
import xyz.sunqian.common.reflect.BytesClassLoader;
import xyz.sunqian.common.reflect.JieJvm;
import xyz.sunqian.common.reflect.proxy.JieAsm;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.AALOAD;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_VARARGS;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.ATHROW;
import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.INVOKEINTERFACE;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;

public class AsmBack {

    private static final BytesClassLoader loader = new BytesClassLoader();

    public static void test() throws Exception {
        Class<?> cls = loader.loadClass(null, generateInstImpl(String.class.getMethod("subSequence", int.class, int.class)));
        System.out.println(cls);
        Invocable invocable = (Invocable) cls.newInstance();
        System.out.println(invocable.invoke("12345", 1, 2));
    }

    // x/y/z/asm
    private static final String PKG_INTERNAL_NAME;
    private static final String OBJECT_INTERNAL_NAME = "java/lang/Object";
    //"xyz/sunqian/common/invoke/Invocable";
    private static final String INVOCABLE_INTERNAL_NAME = JieJvm.getInternalName(Invocable.class);

    static {
        Package pkg = AsmBack.class.getPackage();
        PKG_INTERNAL_NAME = pkg.getName().replaceAll("\\.", "/") + "/asm";
    }

    private static String generateClassInternalName(Method method) {
        // a/b/c
        String className = JieJvm.getInternalName(method.getDeclaringClass());
        // d
        String methodName = method.getName();
        // a/b/c/a/b/c
        String parametersName = Arrays.stream(method.getParameters())
            .map(p -> JieJvm.getInternalName(p.getType())).collect(Collectors.joining("/"));
        // x/y/z/asm/a/b/c/d/a/b/c/a/b/c/InstCaller
        return PKG_INTERNAL_NAME + "/" + className + "/" + methodName + "/" + parametersName + "/InstCaller";
    }

    private static void generateConstructor(ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        methodVisitor.visitVarInsn(ALOAD, 0);
        methodVisitor.visitMethodInsn(INVOKESPECIAL, OBJECT_INTERNAL_NAME, "<init>", "()V", false);
        methodVisitor.visitInsn(RETURN);
        methodVisitor.visitMaxs(1, 1);
        methodVisitor.visitEnd();
    }

    static byte[] generateInstImpl(Method method) {
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        FieldVisitor fieldVisitor;
        RecordComponentVisitor recordComponentVisitor;
        MethodVisitor methodVisitor;
        AnnotationVisitor annotationVisitor0;

        classWriter.visit(
            V1_8,
            ACC_PUBLIC | ACC_SUPER,
            generateClassInternalName(method),//"xyz/sunqian/common/invoke/asm/InstCaller",
            null,
            OBJECT_INTERNAL_NAME,
            new String[]{INVOCABLE_INTERNAL_NAME}
        );

        generateConstructor(classWriter);

        {
            // invoke method
            methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_VARARGS, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, new String[]{"xyz/sunqian/common/invoke/InvocationException"});
            methodVisitor.visitCode();
            Label label0 = new Label();
            Label label1 = new Label();
            Label label2 = new Label();
            methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Exception");
            methodVisitor.visitLabel(label0);

            Class<?> instClass = method.getDeclaringClass();
            String instInternalName = JieJvm.getInternalName(instClass);

            // String str = (String) inst;
            methodVisitor.visitVarInsn(ALOAD, 1);
            methodVisitor.visitTypeInsn(CHECKCAST, instInternalName);
            methodVisitor.visitVarInsn(ASTORE, 3);

            Parameter[] parameters = method.getParameters();
            int astoreIndex = 4;
            for (int i = 0; i < parameters.length; i++) {
                // Integer i = (Integer) args[0];
                Parameter parameter = parameters[i];
                methodVisitor.visitVarInsn(ALOAD, 2);
                // methodVisitor.visitInsn(ICONST_0);
                JieAsm.visitPushNumber(methodVisitor, i);
                methodVisitor.visitInsn(AALOAD);
                // methodVisitor.visitTypeInsn(CHECKCAST, JieJvm.getInternalName(JieReflect.wrapper(parameter.getType())));
                JieAsm.visitObjectCast(methodVisitor, parameter.getType(), false);
                methodVisitor.visitVarInsn(ISTORE, astoreIndex);
                if (Objects.equals(parameter.getType(), long.class) || Objects.equals(parameter.getType(), double.class)) {
                    astoreIndex += 2;
                } else {
                    astoreIndex++;
                }
            }

            // str.substring(i1, i2);
            methodVisitor.visitVarInsn(ALOAD, 3);
            astoreIndex = 4;
            for (Parameter parameter : parameters) {
                methodVisitor.visitVarInsn(ILOAD, astoreIndex);
                if (Objects.equals(parameter.getType(), long.class) || Objects.equals(parameter.getType(), double.class)) {
                    astoreIndex += 2;
                } else {
                    astoreIndex++;
                }
            }
            if (instClass.isInterface()) {
                methodVisitor.visitMethodInsn(
                    INVOKEINTERFACE,
                    instInternalName,
                    method.getName(),
                    JieJvm.getDescriptor(method),
                    true
                );
            } else {
                methodVisitor.visitMethodInsn(
                    INVOKEVIRTUAL,
                    instInternalName,
                    method.getName(),
                    JieJvm.getDescriptor(method),
                    false
                );
            }
            methodVisitor.visitLabel(label1);
            // methodVisitor.visitInsn(ARETURN);
            JieAsm.returnCastObject(methodVisitor, method.getReturnType());
            methodVisitor.visitLabel(label2);
            methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Exception"});
            methodVisitor.visitVarInsn(ASTORE, 3);
            methodVisitor.visitTypeInsn(NEW, "xyz/sunqian/common/invoke/InvocationException");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitVarInsn(ALOAD, 3);
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "xyz/sunqian/common/invoke/InvocationException", "<init>", "(Ljava/lang/Throwable;)V", false);
            methodVisitor.visitInsn(ATHROW);
            methodVisitor.visitMaxs(0, 0);
            methodVisitor.visitEnd();
        }
        classWriter.visitEnd();
        return classWriter.toByteArray();
    }
}
