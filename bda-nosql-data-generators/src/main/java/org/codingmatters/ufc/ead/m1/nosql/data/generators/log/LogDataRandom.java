package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

import org.codingmatters.ufc.ead.m1.nosql.data.utils.Randomizer;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Created by vagrant on 2/15/16.
 */
public class LogDataRandom {
    
    static public class Builder {
        private Long seed;
        private LocalDateTime minAt;
        private LogTemplate[] messages;

        public Builder withSeed(Long seed) {
            this.seed = seed;
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
            return new LogDataRandom(this.seed != null ? this.seed : System.currentTimeMillis(), this.minAt, this.messages);
        }
    }

    private final Randomizer random;

    private final int atMaxIncrement = 5000;
    private LocalDateTime previousAt;

    private final LogTemplate [] templates;

    public LogDataRandom(Long seed, LocalDateTime minAt, LogTemplate[] templates) {
        this.previousAt = minAt;
        this.templates = templates;
        this.random = new Randomizer(seed);
    }

    public LogData next() {
        LogTemplate template = this.nextTemplate();
        return LogData
                .logger(template.getLogger())
                .withAt(this.nextAt())
                .withLevel(template.getLevel())
                .withMessage(template.getMessage())
                .build();
    }

    private LogTemplate nextTemplate() {
        return this.random.nextGaussianFromTable(this.templates);
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

    public LocalDateTime getPreviousAt() {
        return previousAt;
    }
}
