package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

/**
 * Created by vagrant on 2/20/16.
 */
public class LogFormatter {
    public String format(LogData log) {
        return String.format("%s - %s - [%s] %s", log.getAt(), log.getLogger(), log.getLevel(), log.getMessage());
    }
}
