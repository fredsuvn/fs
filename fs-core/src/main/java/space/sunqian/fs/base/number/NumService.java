package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;

public interface NumService {

    @Nonnull
    Number toNumber(@Nonnull CharSequence cs) throws NumException;

    @Nonnull
    Number toNumber(@Nonnull CharSequence cs, int start, int end) throws NumException;
}
