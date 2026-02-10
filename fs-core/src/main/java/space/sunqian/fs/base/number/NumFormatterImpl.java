package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;

import java.text.NumberFormat;
import java.util.function.Supplier;

final class NumFormatterImpl implements NumFormatter {

    private final @Nonnull Supplier<? extends @Nonnull NumberFormat> format;

    NumFormatterImpl(@Nonnull Supplier<? extends @Nonnull NumberFormat> format) {
        this.format = format;
    }

    @Override
    public @Nonnull String format(@Nonnull Number num) throws NumException {
        try {
            return format.get().format(num);
        } catch (Exception e) {
            throw new NumException(e);
        }
    }

    @Override
    public <T> @Nonnull T parse(@Nonnull CharSequence numStr, @Nonnull Class<T> numType) throws NumException {
        try {
            Number num = format.get().parse(numStr.toString());
            return NumKit.toNumber(num, numType);
        } catch (Exception e) {
            throw new NumException(e);
        }
    }
}
