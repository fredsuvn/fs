package space.sunqian.fs.data.json;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

/**
 * Exception for {@code JSON} parsing errors.
 *
 * @author sunqian
 */
public class JsonParsingDataException extends JsonDataException {

    private static @Nonnull String buildMessage(int index, @Nullable String unexpected, @Nullable String expected) {
        StringBuilder builder = new StringBuilder("JSON parsing error at index " + index + ".");
        if (unexpected != null) {
            builder.append(" Unexpected char sequence :").append(unexpected).append(".");
        }
        if (expected != null) {
            builder.append(" Expected char sequence :").append(expected).append(".");
        }
        return builder.toString();
    }

    private final int index;
    private final @Nullable String unexpected;
    private final @Nullable String expected;

    /**
     * Constructs with the index of the JSON input where the error occurred, unexpected char sequence, and expected char
     * sequence.
     *
     * @param occurIndex the index of the JSON input where the error occurred
     * @param unexpected the unexpected char sequence
     * @param expected   the expected char sequence
     */
    public JsonParsingDataException(int occurIndex, @Nullable String unexpected, @Nullable String expected) {
        super(buildMessage(occurIndex, unexpected, expected));
        this.index = occurIndex;
        this.unexpected = unexpected;
        this.expected = expected;
    }

    /**
     * Returns the index of the JSON input where the error occurred.
     *
     * @return the index of the JSON input where the error occurred
     */
    public int getOccurIndex() {
        return index;
    }

    /**
     * Returns the unexpected char sequence for the current error, or {@code null} if not available.
     *
     * @return the unexpected char sequence for the current error, or {@code null} if not available
     *
     */
    @Nullable
    public String getUnexpectedChars() {
        return unexpected;
    }

    /**
     * Returns the expected char sequence for the current error, or {@code null} if not available.
     *
     * @return the expected char sequence for the current error, or {@code null} if not available
     */
    @Nullable
    public String getExpectedChars() {
        return expected;
    }
}
