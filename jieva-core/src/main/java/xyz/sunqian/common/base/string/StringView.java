package xyz.sunqian.common.base.string;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;

import java.util.List;

/**
 * {@code StringRef} is a view over one or more {@link CharSequence} instances, it provides a unified interface to
 * access character sequences that may be composed of multiple underlying sources.
 * <p>
 * Note that changes in the underlying {@link CharSequence} will affect this view.
 *
 * @author sunqian
 */
public interface StringView extends CharSequence {

    /**
     * Returns a {@code StringView} that is composed of the specified {@link CharSequence} instances.
     *
     * @param strings the specified {@link CharSequence} instances to compose
     * @return a {@code StringView} that is composed of the specified {@link CharSequence} instances
     */
    static @Nonnull StringView of(@Nonnull CharSequence @Nonnull @RetainedParam ... strings) {
        return StringViewBack.of(strings);
    }

    /**
     * Returns a {@code StringView} that is composed of the specified list of {@link CharSequence}.
     *
     * @param strings the specified list of {@link CharSequence} to compose
     * @return a {@code StringView} that is composed of the specified list of {@link CharSequence}
     */
    static @Nonnull StringView of(@Nonnull List<? extends @Nonnull CharSequence> strings) {
        return of(strings.toArray(new CharSequence[0]));
    }
}
