package samples;

import space.sunqian.annotations.CachedResult;
import space.sunqian.annotations.DefaultNonNull;
import space.sunqian.annotations.DefaultNullable;
import space.sunqian.annotations.Immutable;
import space.sunqian.annotations.NonExported;
import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;
import space.sunqian.annotations.OutParam;

import java.util.Collections;
import java.util.List;

public class Samples {

    // Nullable field.
    private final @Nullable String nullable = null;

    // Non-null field.
    private final @Nonnull String nonNull = "Nonnull";

    // Non-null field.
    private final @Nonnull
    @Immutable List<@Nonnull String> immutableList = Collections.emptyList();

    // All fields are nullable by default.
    @DefaultNullable
    public static class NullableClass {
        private final String nullable1 = null;
        private final String nullable2 = null;
    }

    // All fields are non-null by default.
    @DefaultNonNull
    public static class NonNullClass {
        private final String nonNull1 = "nonNull1";
        private final String nonNull2 = "nonNull2";
    }

    // Result is cached.
    @CachedResult
    public static @Nonnull String cachedString() {
        return "123";
    }

    // Param is writable.
    public static void withParam(@Nonnull @OutParam List<@Nonnull String> dst) {
        dst.add("hello");
    }

    // Param is used directly.
    public static @Nonnull Object @Nonnull [] withParam(@Nonnull Object @Nonnull ... args) {
        return args;
    }

    // This class is thread-safe.
    @NonExported
    public static class ThreadSafeClass {}

    // This class is public but not exported.
    @NonExported
    public static class NonExportedClass {}
}
