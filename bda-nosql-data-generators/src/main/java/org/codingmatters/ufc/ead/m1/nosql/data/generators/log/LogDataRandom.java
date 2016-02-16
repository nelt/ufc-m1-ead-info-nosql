package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

import org.codingmatters.ufc.ead.m1.nosql.data.generators.util.Randomizer;

import java.time.OffsetDateTime;

/**
 * Created by vagrant on 2/15/16.
 */
public class LogDataRandom {

    private final Randomizer random;
    private final String [] loggers;

    private final OffsetDateTime minAt;
    private final OffsetDateTime maxAt;

    private final LogTemplate [] messages = {};

    public LogDataRandom(Long seed, String[] loggers, OffsetDateTime minAt, OffsetDateTime maxAt) {
        this.loggers = loggers;
        this.minAt = minAt;
        this.maxAt = maxAt;
        this.random = new Randomizer(seed);
    }

    public LogData next() {
        return LogData
                .logger(this.nextLogger())
                .withAt(this.nextAt())
                .withLevel(this.nextLevel())
                .withMessage(this.nextMessage())
                .build();
    }

    private String nextLogger() {
        return this.random.nextFromTable(this.loggers);
    }

    private OffsetDateTime nextAt() {
        return null;
    }

    private Level nextLevel() {
        return null;
    }

    private String nextMessage() {
        return null;
    }

}
