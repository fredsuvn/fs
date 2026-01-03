package space.sunqian.fs.base.random;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.Checker;
import space.sunqian.fs.base.bytes.BytesKit;
import space.sunqian.fs.collect.StreamKit;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

enum RngServiceImplByJ17 implements RngService {
    INST;

    @Override
    public @Nonnull Rng random(@Nonnull Random random) {
        return new RandomRng(random);
    }

    @Override
    public @Nonnull Rng threadLocalRandom() {
        return ThreadLocalRandomRng.INST;
    }

    private static final class RandomRng extends AbsRngImpl {

        private final @Nonnull Random random;

        private RandomRng(@Nonnull Random random) {
            this.random = random;
        }

        @Override
        protected @Nonnull Random random() {
            return random;
        }

        @Override
        public void reset(long seed) {
            random.setSeed(seed);
        }

        @Override
        public void reset(byte @Nonnull [] seed) {
            if (random instanceof SecureRandom) {
                ((SecureRandom) random).setSeed(seed);
            } else {
                random.setSeed(BytesKit.bytesToLong(seed));
            }
        }

        @Override
        public int nextInt() {
            return random.nextInt();
        }

        @Override
        public int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return startInclusive;
            }
            return random.nextInt(startInclusive, endExclusive);
        }

        @Override
        public long nextLong() {
            return random.nextLong();
        }

        @Override
        public long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return startInclusive;
            }
            return random.nextLong(startInclusive, endExclusive);
        }

        @Override
        public float nextFloat() {
            return random.nextFloat();
        }

        @Override
        public float nextFloat(float startInclusive, float endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return startInclusive;
            }
            return random.nextFloat(startInclusive, endExclusive);
        }

        @Override
        public double nextDouble() {
            return random.nextDouble();
        }

        @Override
        public double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return startInclusive;
            }
            return random.nextDouble(startInclusive, endExclusive);
        }
    }

    private static final class ThreadLocalRandomRng extends AbsRngImpl {

        private static final @Nonnull ThreadLocalRandomRng INST = new ThreadLocalRandomRng();

        @Override
        protected @Nonnull ThreadLocalRandom random() {
            return ThreadLocalRandom.current();
        }

        @Override
        public void reset(long seed) {
        }

        @Override
        public void reset(byte @Nonnull [] seed) {
        }

        @Override
        public int nextInt() {
            return random().nextInt();
        }

        @Override
        public int nextInt(int startInclusive, int endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return startInclusive;
            }
            return random().nextInt(startInclusive, endExclusive);
        }

        @Override
        public long nextLong() {
            return random().nextLong();
        }

        @Override
        public long nextLong(long startInclusive, long endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return startInclusive;
            }
            return random().nextLong(startInclusive, endExclusive);
        }

        @Override
        public float nextFloat() {
            return random().nextFloat();
        }

        @Override
        public float nextFloat(float startInclusive, float endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return startInclusive;
            }
            return random().nextFloat(startInclusive, endExclusive);
        }

        @Override
        public double nextDouble() {
            return random().nextDouble();
        }

        @Override
        public double nextDouble(double startInclusive, double endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return startInclusive;
            }
            return random().nextDouble(startInclusive, endExclusive);
        }
    }

    private static abstract class AbsRngImpl implements Rng {

        protected abstract @Nonnull Random random();

        @Override
        public void nextBytes(byte @Nonnull [] bytes) {
            random().nextBytes(bytes);
        }

        @Override
        public void nextBytes(byte @Nonnull [] bytes, int off, int len) throws IndexOutOfBoundsException {
            if (off == 0 && len == bytes.length) {
                random().nextBytes(bytes);
                return;
            }
            Checker.checkOffLen(off, len, bytes.length);
            int i = off;
            int end = off + len;
            for (int words = len >> 3; words-- > 0; ) {
                long rnd = nextLong();
                for (int n = 8; n-- > 0; rnd >>>= Byte.SIZE) {
                    bytes[i++] = (byte) rnd;
                }
            }
            if (i < end) {
                for (long rnd = nextLong(); i < end; rnd >>>= Byte.SIZE) {
                    bytes[i++] = (byte) rnd;
                }
            }
        }

        @Override
        public @Nonnull IntStream ints() {
            return random().ints();
        }

        @Override
        public @Nonnull IntStream ints(int startInclusive, int endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return IntStream.generate(() -> startInclusive);
            }
            return random().ints(startInclusive, endExclusive);
        }

        @Override
        public @Nonnull IntStream ints(long size) throws IllegalArgumentException {
            return random().ints(size);
        }

        @Override
        public @Nonnull IntStream ints(long size, int startInclusive, int endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return IntStream.generate(() -> startInclusive).limit(size);
            }
            return random().ints(size, startInclusive, endExclusive);
        }

        @Override
        public @Nonnull IntSupplier intSupplier(int startInclusive, int endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return () -> startInclusive;
            }
            return StreamKit.toSupplier(random().ints(startInclusive, endExclusive));
        }

        @Override
        public @Nonnull LongStream longs() {
            return random().longs();
        }

        @Override
        public @Nonnull LongStream longs(long startInclusive, long endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return LongStream.generate(() -> startInclusive);
            }
            return random().longs(startInclusive, endExclusive);
        }

        @Override
        public @Nonnull LongStream longs(long size) throws IllegalArgumentException {
            return random().longs(size);
        }

        @Override
        public @Nonnull LongStream longs(long size, long startInclusive, long endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return LongStream.generate(() -> startInclusive).limit(size);
            }
            return random().longs(size, startInclusive, endExclusive);
        }

        @Override
        public @Nonnull LongSupplier longSupplier(long startInclusive, long endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return () -> startInclusive;
            }
            return StreamKit.toSupplier(random().longs(startInclusive, endExclusive));
        }

        @Override
        public @Nonnull DoubleStream doubles() {
            return random().doubles();
        }

        @Override
        public @Nonnull DoubleStream doubles(double startInclusive, double endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return DoubleStream.generate(() -> startInclusive);
            }
            return random().doubles(startInclusive, endExclusive);
        }

        @Override
        public @Nonnull DoubleStream doubles(long size) throws IllegalArgumentException {
            return random().doubles(size);
        }

        @Override
        public @Nonnull DoubleStream doubles(long size, double startInclusive, double endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return DoubleStream.generate(() -> startInclusive).limit(size);
            }
            return random().doubles(size, startInclusive, endExclusive);
        }

        @Override
        public @Nonnull DoubleSupplier doubleSupplier(double startInclusive, double endExclusive) throws IllegalArgumentException {
            if (startInclusive == endExclusive) {
                return () -> startInclusive;
            }
            return StreamKit.toSupplier(random().doubles(startInclusive, endExclusive));
        }
    }
}
