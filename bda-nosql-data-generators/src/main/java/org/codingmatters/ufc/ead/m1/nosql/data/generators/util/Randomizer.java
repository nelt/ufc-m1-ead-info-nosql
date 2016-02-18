package org.codingmatters.ufc.ead.m1.nosql.data.generators.util;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Random;

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
}
