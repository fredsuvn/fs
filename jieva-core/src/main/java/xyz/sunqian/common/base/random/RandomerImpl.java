package xyz.sunqian.common.base.random;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.base.math.MathKit;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

final class RandomerImpl implements Randomer {

    private @Nonnull ThreadLocalRandom random() {
        return ThreadLocalRandom.current();
    }

    @Override
    public boolean nextBoolean() {
        return random().nextBoolean();
    }

    @Override
    public boolean @Nonnull [] fill(boolean @Nonnull [] array) {
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextBoolean();
        }
        return array;
    }

    @Override
    public byte nextByte() {
        return (byte) random().nextInt();
    }

    @Override
    public byte nextByte(byte startInclusive, byte endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return (byte) random().nextInt(startInclusive, endExclusive);
    }

    @Override
    public byte @Nonnull [] fill(byte @Nonnull [] array) {
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) random.nextInt();
        }
        return array;
    }

    @Override
    public byte @Nonnull [] fill(
        byte @Nonnull [] array, byte startInclusive, byte endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = (byte) random.nextInt(startInclusive, endExclusive);
        }
        return array;
    }

    @Override
    public short nextShort() {
        return (short) random().nextInt();
    }

    @Override
    public short nextShort(short startInclusive, short endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return (short) random().nextInt(startInclusive, endExclusive);
    }

    @Override
    public short @Nonnull [] fill(short @Nonnull [] array) {
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = (short) random.nextInt();
        }
        return array;
    }

    @Override
    public short @Nonnull [] fill(
        short @Nonnull [] array, short startInclusive, short endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = (short) random.nextInt(startInclusive, endExclusive);
        }
        return array;
    }

    @Override
    public char nextChar() {
        return (char) random().nextInt();
    }

    @Override
    public char nextChar(char startInclusive, char endExclusive) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            return startInclusive;
        }
        return (char) random().nextInt(startInclusive, endExclusive);
    }

    @Override
    public char @Nonnull [] fill(char @Nonnull [] array) {
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = (char) random.nextInt();
        }
        return array;
    }

    @Override
    public char @Nonnull [] fill(
        char @Nonnull [] array, char startInclusive, char endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = (char) random.nextInt(startInclusive, endExclusive);
        }
        return array;
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
    public int @Nonnull [] fill(int @Nonnull [] array) {
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt();
        }
        return array;
    }

    @Override
    public int @Nonnull [] fill(
        int @Nonnull [] array, int startInclusive, int endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(startInclusive, endExclusive);
        }
        return array;
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
    public long @Nonnull [] fill(long @Nonnull [] array) {
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextLong();
        }
        return array;
    }

    @Override
    public long @Nonnull [] fill(
        long @Nonnull [] array, long startInclusive, long endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextLong(startInclusive, endExclusive);
        }
        return array;
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
        if (startInclusive > endExclusive) {
            throw new IllegalArgumentException("startInclusive > endExclusive.");
        }
        return nextFloat(random(), startInclusive, endExclusive);
    }

    @Override
    public float @Nonnull [] fill(float @Nonnull [] array) {
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextFloat();
        }
        return array;
    }

    @Override
    public float @Nonnull [] fill(
        float @Nonnull [] array, float startInclusive, float endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        if (startInclusive > endExclusive) {
            throw new IllegalArgumentException("startInclusive > endExclusive.");
        }
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = nextFloat(random, startInclusive, endExclusive);
        }
        return array;
    }

    private float nextFloat(ThreadLocalRandom random, float startInclusive, float endExclusive) {
        float r = random.nextFloat();
        r = r * (endExclusive - startInclusive) + startInclusive;
        return MathKit.makeIn(r, startInclusive, endExclusive);
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

    @Override
    public double @Nonnull [] fill(double @Nonnull [] array) {
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextDouble();
        }
        return array;
    }

    @Override
    public double @Nonnull [] fill(
        double @Nonnull [] array, double startInclusive, double endExclusive
    ) throws IllegalArgumentException {
        if (startInclusive == endExclusive) {
            Arrays.fill(array, startInclusive);
            return array;
        }
        ThreadLocalRandom random = random();
        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextDouble(startInclusive, endExclusive);
        }
        return array;
    }
}
