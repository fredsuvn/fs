package space.sunqian.egg;

import space.sunqian.annotation.Nullable;
import space.sunqian.fs.base.exception.FsRuntimeException;

public class EggHatchingException extends FsRuntimeException {

    public EggHatchingException() {
        super();
    }

    public EggHatchingException(@Nullable String message) {
        super(message);
    }

    public EggHatchingException(@Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public EggHatchingException(@Nullable Throwable cause) {
        super(cause);
    }
}