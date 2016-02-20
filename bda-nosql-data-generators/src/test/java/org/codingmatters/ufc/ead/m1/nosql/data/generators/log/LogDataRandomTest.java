package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

import org.hamcrest.Matchers;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by vagrant on 2/15/16.
 */
public class LogDataRandomTest {

    @Test
    public void testNominal() throws Exception {
        LogDataRandom random = new LogDataRandom.Builder()
                .withLoggers("log1", "log2", "log3")
                .withMinAt(LocalDateTime.now().minusDays(3))
                .withMessages(
                        new LogTemplate(Level.CRITICAL, () -> "message1"),
                        new LogTemplate(Level.INFO, () -> "message2"),
                        new LogTemplate(Level.ERROR, () -> "message3")
                )
                .build();
        LogData data = random.next();

        assertThat(data.getLogger(), is(isOneOf("log1", "log2", "log3")));
        assertThat(data.getLevel(), is(isOneOf(Level.CRITICAL, Level.INFO, Level.ERROR)));
        assertThat(data.getMessage(), is(isOneOf("message1", "message2", "message3")));
        assertThat(data.getAt(), is(notNullValue()));

        System.out.println(new LogFormatter().format(data));
    }

    @Test
    public void testLogs() throws Exception {
        LogDataRandom random = new LogDataRandom.Builder()
                .withLoggers("log1", "log2", "log3")
                .withMinAt(LocalDateTime.now().minusDays(3))
                .withMessages(
                        new LogTemplate(Level.CRITICAL, () -> "message1"),
                        new LogTemplate(Level.INFO, () -> "message2"),
                        new LogTemplate(Level.ERROR, () -> "message3")
                )
                .build();
        LogFormatter formatter = new LogFormatter();

        for(int i = 0 ; i < 1000 ; i++) {
            System.out.println(formatter.format(random.next()));
        }
    }
}