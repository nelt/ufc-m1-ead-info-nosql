package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

import org.codingmatters.ufc.ead.m1.nosql.data.generators.util.Randomizer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vagrant on 2/15/16.
 */
public class LogDataRandom {
    
    static public class Builder {
        private Long seed;
        private String[] loggers; 
        private LocalDateTime minAt; 
        private LogTemplate[] messages;

        public Builder withSeed(Long seed) {
            this.seed = seed;
            return this;
        }

        public Builder withLoggers(String ... loggers) {
            this.loggers = loggers;
            return this;
        }

        public Builder withMinAt(LocalDateTime minAt) {
            this.minAt = minAt;
            return this;
        }

        public Builder withMessages(LogTemplate ... messages) {
            this.messages = messages;
            return this;
        }

        public LogDataRandom build() {
            return new LogDataRandom(this.seed != null ? this.seed : System.currentTimeMillis(), this.loggers, this.minAt, this.messages);
        }
    }

    private final Randomizer random;
    private final String [] loggers;

    private final int atMaxIncrement = 5000;
    private LocalDateTime previousAt;

    private final LogTemplate [] templates;

    public LogDataRandom(Long seed, String[] loggers, LocalDateTime minAt, LogTemplate[] templates) {
        this.loggers = loggers;
        this.previousAt = minAt;
        this.templates = templates;
        this.random = new Randomizer(seed);
    }

    public LogData next() {
        LogTemplate template = this.nextTemplate();
        return LogData
                .logger(this.nextLogger())
                .withAt(this.nextAt())
                .withLevel(template.getLevel())
                .withMessage(template.getMessage())
                .build();
    }

    private LogTemplate nextTemplate() {
        return this.random.nextGaussianFromTable(this.templates);
    }

    private String nextLogger() {
        return this.random.nextFromTable(this.loggers);
    }

    private LocalDateTime nextAt() {
        int increment = this.random.nextInt(this.atMaxIncrement);
        LocalDateTime result = this.previousAt.plus(increment, ChronoUnit.MILLIS);
        try {
            return result;
        } finally {
            this.previousAt = result;
        }
    }

}
