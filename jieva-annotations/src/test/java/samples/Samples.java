package samples;

import xyz.sunqian.annotations.DefaultNonNull;
import xyz.sunqian.annotations.DefaultNullable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

public class Samples {

    // Nullable field
    private @Nullable String nullable;

    // Non-null field
    private final @Nonnull String nonNull = "Nonnull";

    // All fields are nullable by default
    @DefaultNullable
    static class NullableClass {
        private String nullable1;
        private String nullable2;
    }

    // All fields are non-null by default
    @DefaultNonNull
    static class NonNullClass {
        private final String nonNull1 = "nonNull1";
        private final String nonNull2 = "nonNull2";
    }
}
