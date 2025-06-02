// package test;
//
// import org.testng.annotations.Test;
//
// import java.lang.annotation.Annotation;
// import java.lang.reflect.AnnotatedType;
// import java.util.Arrays;
// import java.util.function.Function;
//
// public class OtherTest {
//
//     public static String[][] array = null;
//
//     public static String string = null;
//
//     @Test
//     public void test() throws Exception {
//         Annotation[] annotations = getClass().getField("array").getAnnotations();
//         System.out.println(Arrays.toString(annotations));
//
//         AnnotatedType ata = getClass().getField("array").getAnnotatedType();
//         System.out.println(ata + ": " + ata.getClass());
//         System.out.println(Arrays.toString(ata.getDeclaredAnnotations()));
//         System.out.println(ata.getType());
//         AnnotatedType ats = getClass().getField("string").getAnnotatedType();
//         System.out.println(ats + ": " + ats.getClass());
//         System.out.println(Arrays.toString(ats.getAnnotations()));
//     }
//
//     @Test
//     public void test2() throws Exception {
//         tx("", Object::toString);
//     }
//
//     public void tx(String a, Function<Object, ? extends String> mapper) {
//         String x = mapper.apply("");
//         System.out.println(x.length());
//     }
// }
