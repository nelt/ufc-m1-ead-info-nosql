package org.codingmatters.ufc.ead.m1.nosql.data.generators.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Random;
import java.util.concurrent.atomic.DoubleAccumulator;
import java.util.regex.Matcher;

/**
 * Created by vagrant on 2/15/16.
 */
public class Randomizer {

    private final Random random;
    private final long seed;

    public Randomizer(Long seed) {
        this.seed = seed != null ? seed : System.currentTimeMillis();
        this.random = new Random(this.seed);
    }

    public long getSeed() {
        return seed;
    }

    public int nextInt(int length) {
        return this.random.nextInt(length);
    }

    public double nextDouble() {
        return this.random.nextDouble();
    }

    public OffsetDateTime nextOffsetDateTime(OffsetDateTime min, OffsetDateTime max) {
        long minMillis = min.toInstant().toEpochMilli();
        long maxMillis = max.toInstant().toEpochMilli();
        long millis = (long) Math.floor(nextDouble() * (maxMillis - minMillis) + minMillis);
        return OffsetDateTime.ofInstant(Instant.ofEpochMilli(millis), min.getOffset());
    }

    public double nextDoubleInRange(double min, double max) {
        return nextDouble() * (max - min) + min;
    }

    public String nextFromTable(String[] candidates) {
        int index = nextInt(candidates.length);
        return candidates[index];
    }

    public <T> T  nextGaussianFromTable(T[] candidates) {
        int i = 2;
        double v = Math.max(0, Math.min(i + this.random.nextGaussian(), 2 * i)) / (2 * i);
        double r = v * (candidates.length - 1);
        Double index = Math.floor(r);
        return candidates[index.intValue()];
    }
}
