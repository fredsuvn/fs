package test;

import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.util.Arrays;

public class OtherTest {

    public static @MyNullable1 String @MyNullable3 [] @MyNullable2 [] array = null;

    public static @MyNullable1 String string = null;

    @Test
    public void test() throws Exception {
        Annotation[] annotations = getClass().getField("array").getAnnotations();
        System.out.println(Arrays.toString(annotations));

        AnnotatedType ata = getClass().getField("array").getAnnotatedType();
        System.out.println(ata + ": " + ata.getClass());
        System.out.println(Arrays.toString(ata.getDeclaredAnnotations()));
        System.out.println(ata.getType());
        AnnotatedType ats = getClass().getField("string").getAnnotatedType();
        System.out.println(ats + ": " + ats.getClass());
        System.out.println(Arrays.toString(ats.getAnnotations()));
    }
}
