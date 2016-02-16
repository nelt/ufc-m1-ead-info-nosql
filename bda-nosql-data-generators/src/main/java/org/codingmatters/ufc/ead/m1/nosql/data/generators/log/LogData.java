package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

import java.time.OffsetDateTime;

/**
 * Created by vagrant on 2/15/16.
 */
public class LogData {

    static Builder logger(String name) {
        return new Builder().withLogger(name);
    }

    private final OffsetDateTime at;
    private final Level level;
    private final String logger;
    private final String message;

    public LogData(OffsetDateTime at, Level level, String logger, String message) {
        this.at = at;
        this.level = level;
        this.logger = logger;
        this.message = message;
    }

    public OffsetDateTime getAt() {
        return at;
    }

    public Level getLevel() {
        return level;
    }

    public String getLogger() {
        return logger;
    }

    public String getMessage() {
        return message;
    }

    static public class Builder {
        private OffsetDateTime at;
        private Level level;
        private String logger;
        private String message;

        private Builder() {
        }

        public Builder withAt(OffsetDateTime at) {
            this.at = at;
            return this;
        }

        public Builder withLevel(Level level) {
            this.level = level;
            return this;
        }

        public Builder withLogger(String logger) {
            this.logger = logger;
            return this;
        }

        public Builder withMessage(String message) {
            this.message = message;
            return this;
        }

        public LogData build() {
            return new LogData(this.at, this.level, this.logger, this.message);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LogData logData = (LogData) o;

        if (at != null ? !at.equals(logData.at) : logData.at != null) return false;
        if (level != logData.level) return false;
        if (logger != null ? !logger.equals(logData.logger) : logData.logger != null) return false;
        return message != null ? message.equals(logData.message) : logData.message == null;

    }

    @Override
    public int hashCode() {
        int result = at != null ? at.hashCode() : 0;
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (logger != null ? logger.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LogData{" +
                "at=" + at +
                ", level=" + level +
                ", logger='" + logger + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
