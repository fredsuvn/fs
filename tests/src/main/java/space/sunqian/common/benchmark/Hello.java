package space.sunqian.common.benchmark;

public class Hello {

    private final String inst = "inst-";

    public static String staticWorld(String hi, int i, double d) {
        return hi;// + (i * d);
    }

    public String instanceWorld(String hi, int i, double d) {
        return inst + hi;// + (i * d);
    }
}
