package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;

import java.math.BigDecimal;
import java.math.BigInteger;

public enum NumServiceImplByJ9 implements NumService {
    INST;

    @Override
    public @Nonnull Number toNumber(@Nonnull CharSequence cs) throws NumException {
        try {
            return toNumber0(cs, 0, cs.length());
        } catch (Exception e) {
            throw new NumException(e);
        }
    }

    @Override
    public @Nonnull Number toNumber(@Nonnull CharSequence cs, int start, int end) throws NumException {
        try {
            return toNumber0(cs, start, end);
        } catch (Exception e) {
            throw new NumException(e);
        }
    }

    private @Nonnull Number toNumber0(@Nonnull CharSequence cs, int start, int end) throws NumberFormatException {
        for (int i = start; i < end; i++) {
            char c = cs.charAt(i);
            if (c == '.' || c == 'e' || c == 'E') {
                return new BigDecimal(cs.subSequence(start, end).toString());
            }
        }
        int len = cs.length();
        char first = cs.charAt(0);
        if (first == '-' || first == '+') {
            len--;
        }
        if (len <= 9) {
            return Integer.parseInt(cs, start, end, 10);
        }
        if (len <= 18) {
            return Long.parseLong(cs, start, end, 10);
        }
        return new BigInteger(cs.subSequence(start, end).toString());
    }
}
