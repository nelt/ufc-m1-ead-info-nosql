package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

/**
 * Created by vagrant on 2/15/16.
 */
public class LogTemplate {
    
    static public Builder logger(String logger) {
        return new Builder().withLogger(logger);
    }
    
    static public class Builder {
        private Level level;
        private String logger;
        private Formatter formatter;
        
        private Builder() {}

        public Builder withLevel(Level level) {
            this.level = level;
            return this;
        }

        public Builder withLogger(String logger) {
            this.logger = logger;
            return this;
        }

        public Builder withFormatter(Formatter formatter) {
            this.formatter = formatter;
            return this;
        }

        public LogTemplate build() {
            return new LogTemplate(this.level, this.logger, this.formatter);
        }
    }
    
    private final Level level;
    private final String logger;
    private final Formatter formatter;

    private LogTemplate(Level level, String logger, Formatter formatter) {
        this.level = level;
        this.logger = logger;
        this.formatter = formatter;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return this.formatter.format();
    }


    public String getLogger() {
        return logger;
    }

    public interface Formatter {
        String format();
    }
}
