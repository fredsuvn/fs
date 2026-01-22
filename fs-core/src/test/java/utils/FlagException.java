package utils;

public class FlagException extends RuntimeException {

    public FlagException(int[] c) {
        super();
        c[0]++;
    }
}
