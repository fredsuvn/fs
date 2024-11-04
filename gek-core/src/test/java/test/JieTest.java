// package test;
//
// import xyz.fslabo.annotations.Nullable;
// import xyz.sunqian.common.invoke.Invoker;
// import xyz.sunqian.common.invoke.InvokingException;
//
// import java.lang.reflect.Method;
//
// import static org.testng.Assert.expectThrows;
//
// public class JieTest {
//
//     public static <T extends Throwable> void testThrow(Class<T> exception, Method method, @Nullable Object inst, Object... args) {
//         method.setAccessible(true);
//         Invoker invoker = Invoker.handle(method);
//         expectThrows(exception, () -> {
//             try {
//                 invoker.invoke(inst, args);
//             } catch (InvokingException e) {
//                 throw e.getCause();
//             }
//         });
//     }
//
//     // @Test
//     // public void testThrow() {
//     //     BeanUtils.copyProperties(null, null);
//     //     JieLog.of().info(JieTrace.toString(
//     //         new IllegalArgumentException(new IllegalStateException(new NullPointerException())))
//     //     );
//     //     JieLog.of().info(JieTrace.toString(
//     //         new IllegalArgumentException(new IllegalStateException(new NullPointerException())),
//     //         " : ")
//     //     );
//     // }
//     //
//     // @Test
//     // public void testFindCallerStackTrace() {
//     //     T1.invoke1();
//     // }
//     //
//     // @Test
//     // public void testEqual() {
//     //     Assert.assertTrue(Jie.equals(new int[]{1, 2, 3}, new int[]{1, 2, 3}));
//     //     Assert.assertFalse(Jie.equals(new int[]{1, 2}, new int[]{1, 2, 3}));
//     //     Assert.assertFalse(Jie.equalsWith(new int[]{1, 2, 3}, new int[]{1, 2, 3}, false, false));
//     //     Assert.assertTrue(Jie.equals(
//     //         new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}},
//     //         new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}
//     //     ));
//     //     Assert.assertFalse(Jie.equals(
//     //         new Object[]{new int[]{1, 2, 3}, new int[]{1, 2}},
//     //         new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}
//     //     ));
//     //     Assert.assertFalse(Jie.equalsWith(
//     //         new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}},
//     //         new Object[]{new int[]{1, 2, 3}, new int[]{1, 2, 3}}, false, true
//     //     ));
//     // }
//     //
//     // @Test
//     // public void testRes() throws IOException {
//     //     URL f1 = Jie.findRes("/t2/f1.txt");
//     //     Assert.assertEquals(JieIO.readString(f1.openStream(), JieChars.defaultCharset()), "f1.txt");
//     //     Set<URL> set = Jie.findAllRes("/t2/f2.txt");
//     //     for (URL url : set) {
//     //         Assert.assertEquals(JieIO.readString(url.openStream(), JieChars.defaultCharset()), "f2.txt");
//     //     }
//     // }
//     //
//     // @Test
//     // public void testSystem() {
//     //     JieLog.of().info(JieSystem.getJavaVersion());
//     //     JieLog.of().info(JieSystem.javaMajorVersion());
//     //     JieLog.of().info(JieChars.nativeCharset());
//     //     JieLog.of().info(JieSystem.getOsName());
//     //     JieLog.of().info(JieSystem.isWindows());
//     //     JieLog.of().info(JieSystem.isLinux());
//     //     JieLog.of().info(JieSystem.isBsd());
//     //     JieLog.of().info(JieSystem.isMac());
//     //     JieLog.of().info(JieSystem.isJdk9OrHigher());
//     // }
//     //
//     // @Test
//     // public void testEnum() {
//     //     Assert.assertEquals(Te.A, Jie.findEnum(Te.class, 0));
//     //     Assert.assertEquals(Te.B, Jie.findEnum(Te.class, "B", false));
//     //     Assert.assertEquals(Te.C, Jie.findEnum(Te.class, "c", true));
//     //     Assert.assertNull(Jie.findEnum(Te.class, 10));
//     //     Assert.assertNull(Jie.findEnum(Te.class, "d", false));
//     //     Assert.expectThrows(IllegalArgumentException.class, () -> Jie.findEnum(Te.class, -1));
//     //     Assert.expectThrows(IllegalArgumentException.class, () -> Jie.findEnum(Jie.class, -1));
//     //     Assert.expectThrows(IllegalArgumentException.class, () -> Jie.findEnum(Jie.class, "a", true));
//     // }
//     //
//     // public enum Te {
//     //     A, B, C
//     // }
//     //
//     // private static final class T1 {
//     //     public static void invoke1() {
//     //         T2.invoke2();
//     //     }
//     // }
//     //
//     // private static final class T2 {
//     //     public static void invoke2() {
//     //         T3.invoke3();
//     //     }
//     // }
//     //
//     // private static final class T3 {
//     //     public static void invoke3() {
//     //         StackTraceElement element1 = JieTrace.findCallerTrace(T1.class.getName(), "invoke1");
//     //         Assert.assertEquals(element1.getClassName(), JieTest.class.getName());
//     //         Assert.assertEquals(element1.getMethodName(), "testFindCallerStackTrace");
//     //         StackTraceElement element2 = JieTrace.findCallerTrace(T2.class.getName(), "invoke2");
//     //         Assert.assertEquals(element2.getClassName(), T1.class.getName());
//     //         Assert.assertEquals(element2.getMethodName(), "invoke1");
//     //         StackTraceElement element3 = JieTrace.findCallerTrace(T3.class.getName(), "invoke3");
//     //         Assert.assertEquals(element3.getClassName(), T2.class.getName());
//     //         Assert.assertEquals(element3.getMethodName(), "invoke2");
//     //     }
//     // }
// }
