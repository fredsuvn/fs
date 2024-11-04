package test;

import org.testng.annotations.Test;

/**
 * @author fredsuvn
 */
public class AnnotationTest {

    @Test
    public void testAnnotations() {
        Bean bean1 = new Bean();
        bean1.setP1("123");
        bean1.setP1("p1");
        bean1.setP2("p2");
        String p1 = bean1.getP1();
        String p2 = bean1.getP2();
        System.out.println(p1.substring(1));
        System.out.println(p2.substring(1));
    }
}
