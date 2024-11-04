package xyz.sunqian.common.base;

/**
 * Flag enum, indicating a state, result, instruction, special value, etc.
 *
 * @author fredsuvn
 */
public enum Flag {

    /**
     * Special value: {@code null}.
     */
    NULL,

    /**
     * Instruction {@code continue}, to continue a loop.
     */
    CONTINUE,

    /**
     * Instruction {@code break}, to break from a loop.
     */
    BREAK,

    /**
     * Instruction {@code return}, to return from a process.
     */
    RETURN,

    /**
     * Instruction {@code goto}, to go to a point of process.
     */
    GOTO,

    /**
     * Flag value: unsupported.
     */
    UNSUPPORTED,
    ;

    @Override
    public String toString() {
        return "[" + name() + "]";
    }
}
