// package test.test;
//
// import org.testng.annotations.Test;
//
// import java.lang.reflect.Constructor;
//
// public class InnerClassTest {
//
//     @Test
//     public void testConstructor() throws Exception {
//         class X {
//
//             X() {
//             }
//
//             X(String a) {
//             }
//         }
//
//         Constructor<?> c1 = X.class.getDeclaredConstructor();
//         Constructor<?> c2 = X.class.getDeclaredConstructor(String.class);
//
//         Constructor<?> c11 = X.class.getDeclaredConstructor(InnerClassTest.class);
//         Constructor<?> c22 = X.class.getDeclaredConstructor(InnerClassTest.class, String.class);
//     }
// }
