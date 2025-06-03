// package xyz.sunqian.common.invoke;
//
// import org.objectweb.asm.ClassWriter;
// import org.objectweb.asm.Opcodes;
// import xyz.sunqian.annotations.Nullable;
//
// final class AsmInvocable implements Invocable {
//     @Override
//     public @Nullable Object invoke(@Nullable Object inst, @Nullable Object... args) throws InvocationException {
//         try {
//             return ((String) inst).replace('a', 'b');
//         } catch (Throwable e) {
//             throw new InvocationException(e);
//         }
//     }
//
//     private void gen() {
//         ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
//
//     classWriter.visit(
//         Opcodes.V1_8,
//         Opcodes.ACC_FINAL | Opcodes.ACC_SUPER,
//         "xyz/sunqian/common/invoke/AsmInvocable",
//         null,
//         "java/lang/Object",
//         new String[]{"xyz/sunqian/common/invoke/Invocable"}
//     );
//
//         {
//             methodVisitor = classWriter.visitMethod(0, "<init>", "()V", null, null);
//             methodVisitor.visitCode();
//             methodVisitor.visitVarInsn(ALOAD, 0);
//             methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
//             methodVisitor.visitInsn(RETURN);
//             methodVisitor.visitMaxs(1, 1);
//             methodVisitor.visitEnd();
//         }
//         {
//             methodVisitor = classWriter.visitMethod(ACC_PUBLIC | ACC_VARARGS, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;", null, new String[]{"xyz/sunqian/common/invoke/InvocationException"});
//             {
//                 annotationVisitor0 = methodVisitor.visitAnnotation("Lxyz/sunqian/annotations/Nullable;", true);
//                 annotationVisitor0.visitEnd();
//             }
//             {
//                 annotationVisitor0 = methodVisitor.visitTypeAnnotation(335544320, null, "Lxyz/sunqian/annotations/Nullable;", true);
//                 annotationVisitor0.visitEnd();
//             }
//             {
//                 annotationVisitor0 = methodVisitor.visitTypeAnnotation(369098752, null, "Lxyz/sunqian/annotations/Nullable;", true);
//                 annotationVisitor0.visitEnd();
//             }
//             {
//                 annotationVisitor0 = methodVisitor.visitTypeAnnotation(369164288, TypePath.fromString("["), "Lxyz/sunqian/annotations/Nullable;", true);
//                 annotationVisitor0.visitEnd();
//             }
//             methodVisitor.visitAnnotableParameterCount(2, true);
//             {
//                 annotationVisitor0 = methodVisitor.visitParameterAnnotation(0, "Lxyz/sunqian/annotations/Nullable;", true);
//                 annotationVisitor0.visitEnd();
//             }
//             {
//                 annotationVisitor0 = methodVisitor.visitParameterAnnotation(1, "Lxyz/sunqian/annotations/Nullable;", true);
//                 annotationVisitor0.visitEnd();
//             }
//             methodVisitor.visitCode();
//             Label label0 = new Label();
//             Label label1 = new Label();
//             Label label2 = new Label();
//             methodVisitor.visitTryCatchBlock(label0, label1, label2, "java/lang/Throwable");
//             methodVisitor.visitLabel(label0);
//             methodVisitor.visitVarInsn(ALOAD, 1);
//             methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
//             methodVisitor.visitIntInsn(BIPUSH, 97);
//             methodVisitor.visitIntInsn(BIPUSH, 98);
//             methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "replace", "(CC)Ljava/lang/String;", false);
//             methodVisitor.visitLabel(label1);
//             methodVisitor.visitInsn(ARETURN);
//             methodVisitor.visitLabel(label2);
//             methodVisitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
//             methodVisitor.visitVarInsn(ASTORE, 3);
//             methodVisitor.visitTypeInsn(NEW, "xyz/sunqian/common/invoke/InvocationException");
//             methodVisitor.visitInsn(DUP);
//             methodVisitor.visitVarInsn(ALOAD, 3);
//             methodVisitor.visitMethodInsn(INVOKESPECIAL, "xyz/sunqian/common/invoke/InvocationException", "<init>", "(Ljava/lang/Throwable;)V", false);
//             methodVisitor.visitInsn(ATHROW);
//             methodVisitor.visitMaxs(3, 4);
//             methodVisitor.visitEnd();
//         }
//         classWriter.visitEnd();
//     }
// }
