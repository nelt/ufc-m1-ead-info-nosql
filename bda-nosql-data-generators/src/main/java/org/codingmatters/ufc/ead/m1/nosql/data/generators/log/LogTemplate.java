package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

/**
 * Created by vagrant on 2/15/16.
 */
public class LogTemplate {
    private final Level level;
    private final Formatter formatter;

    public LogTemplate(Level level, Formatter formatter) {
        this.level = level;
        this.formatter = formatter;
    }

    public Level getLevel() {
        return level;
    }

    public String getMessage() {
        return this.formatter.format();
    }

    public interface Formatter {
        String format();
    }
}
