package space.sunqian.fs.invoke;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.value.SimpleKey;
import space.sunqian.fs.cache.SimpleCache;

import java.util.function.Function;

final class InvocableBack {

    static @Nullable Object @Nonnull [] toInstanceArgs(
        @Nullable Object inst, @Nullable Object @Nonnull ... args
    ) {
        Object[] instanceArgs = new Object[args.length + 1];
        instanceArgs[0] = inst;
        System.arraycopy(args, 0, instanceArgs, 1, args.length);
        return instanceArgs;
    }

    enum ReturnNull implements Invocable {

        INST;

        @Override
        public Object invokeDirectly(@Nullable Object inst, Object @Nonnull ... args) {
            return null;
        }
    }

    static final class Cache {

        private static final @Nonnull SimpleCache<@Nonnull SimpleKey, @Nonnull Invocable> CACHE = SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(CACHE);
        }

        static @Nonnull Invocable get(
            @Nonnull SimpleKey key,
            @Nonnull Function<@Nonnull SimpleKey, @Nonnull Invocable> function
        ) {
            return CACHE.get(key, function);
        }

        private Cache() {
        }
    }

    private InvocableBack() {
    }
}
