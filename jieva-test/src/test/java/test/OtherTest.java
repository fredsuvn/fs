package test;

import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;

public class OtherTest {

    public static @MyNullable1 String [] array = null;

    @Test
    public void test() throws Exception {
        Annotation[] annotations = getClass().getField("array").getAnnotations();
        System.out.println(Arrays.toString(annotations));
    }
}
