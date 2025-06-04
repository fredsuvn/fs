package xyz.sunqian.common.benchmark;

import java.util.Base64;

public interface ForProxy {

    default String doSomething(String input) {
        byte[] bytes = Base64.getEncoder().encode(input.getBytes());
        Base64.getDecoder().decode(bytes);
        return input;
    }

    default String doSomeSimple(String input) {
        return input;
    }

    public static class Impl implements ForProxy {

        // @Override
        // public String doSomething(String input) {
        //     return String.valueOf(input.matches(".+[1234567890].+"));
        // }
    }
}
