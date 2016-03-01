package org.codingmatters.ufc.ead.m1.nosql.data.generators.log;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.codingmatters.ufc.ead.m1.nosql.data.generators.log.Level.CRITICAL;
import static org.codingmatters.ufc.ead.m1.nosql.data.generators.log.Level.ERROR;
import static org.codingmatters.ufc.ead.m1.nosql.data.generators.log.Level.INFO;
import static org.codingmatters.ufc.ead.m1.nosql.data.generators.log.LogTemplate.logger;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by vagrant on 2/15/16.
 */
public class LogDataRandomTest {

    @Test
    public void testNominal() throws Exception {
        LogDataRandom random = new LogDataRandom.Builder()
                .withMinAt(LocalDateTime.now().minusDays(3))
                .withMessages(
                        logger("log1").withLevel(CRITICAL).withFormatter(() -> "message1").build(),
                        logger("log2").withLevel(INFO).withFormatter(() -> "message2").build(),
                        logger("log3").withLevel(ERROR).withFormatter(() -> "message3").build()
                )
                .build();
        LogData data = random.next();

        assertThat(data.getLogger(), is(isOneOf("log1", "log2", "log3")));
        assertThat(data.getLevel(), is(isOneOf(CRITICAL, INFO, ERROR)));
        assertThat(data.getMessage(), is(isOneOf("message1", "message2", "message3")));
        assertThat(data.getAt(), is(notNullValue()));

        System.out.println(new LogFormatter().format(data));
    }

    @Test
    public void testLogs() throws Exception {
        LogDataRandom random = new LogDataRandom.Builder()
                .withMinAt(LocalDateTime.now().minusDays(3))
                .withMessages(
                        logger("log1").withLevel(CRITICAL).withFormatter(() -> "message1").build(),
                        logger("log2").withLevel(INFO).withFormatter(() -> "message2").build(),
                        logger("log3").withLevel(ERROR).withFormatter(() -> "message3").build()
                )
                .build();
        LogFormatter formatter = new LogFormatter();

        for(int i = 0 ; i < 1000 ; i++) {
            System.out.println(formatter.format(random.next()));
        }
    }
}