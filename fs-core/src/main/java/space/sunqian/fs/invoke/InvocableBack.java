package space.sunqian.fs.invoke;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;
import space.sunqian.fs.base.value.SimpleKey2;
import space.sunqian.fs.cache.SimpleCache;

import java.util.function.Function;

final class InvocableBack {

    static @Nonnull Invocable getInvocable(
        @Nonnull SimpleKey2 key,
        @Nonnull Function<@Nonnull SimpleKey2, @Nonnull Invocable> function
    ) {
        return Cache.get(key, function);
    }

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

    private static final class Cache {

        private static final @Nonnull SimpleCache<@Nonnull SimpleKey2, @Nonnull Invocable> CACHE = SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(CACHE);
        }

        private static @Nonnull Invocable get(
            @Nonnull SimpleKey2 key,
            @Nonnull Function<@Nonnull SimpleKey2, @Nonnull Invocable> function
        ) {
            return CACHE.get(key, function);
        }

        private Cache() {
        }
    }

    private InvocableBack() {
    }
}
