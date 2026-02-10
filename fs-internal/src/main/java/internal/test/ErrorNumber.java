package internal.test;

/**
 * A number implementation that always throws {@link UnsupportedOperationException} for all operations.
 */
public class ErrorNumber extends Number {

    @Override
    public int intValue() {
        throw new UnsupportedOperationException("intValue");
    }

    @Override
    public long longValue() {
        throw new UnsupportedOperationException("longValue");
    }

    @Override
    public float floatValue() {
        throw new UnsupportedOperationException("floatValue");
    }

    @Override
    public double doubleValue() {
        throw new UnsupportedOperationException("doubleValue");
    }
}
