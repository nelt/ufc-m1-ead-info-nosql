package org.codingmatters.ufc.ead.m1.nosql.data.injectors.sensor;

import com.datastax.driver.core.*;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorDataRandom;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.sensor.cassandra.CassandraInjector;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers.dateFromLocalDateTime;
import static org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers.formattedWeek;
import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;
import static org.junit.Assert.assertThat;

/**
 * Created by vagrant on 2/18/16.
 */
@Ignore
public class CassandraInjectorTest {

    private Cluster cluster;
    private Session session;

    @Before
    public void setUp() throws Exception {
        this.cluster = Cluster.builder().addContactPoint(resolver().resolve("cassandra")).build();

        this.session = this.cluster.connect();
        this.session.execute("CREATE KEYSPACE IF NOT EXISTS ufcead WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 1 }");
        this.session.execute("CREATE TABLE IF NOT EXISTS ufcead.sensor_data ( " +
                "sensor text, " +
                "week text, " +
                "at timestamp, " +
                "temperature double, " +
                "hygrometry double, " +
                "PRIMARY KEY ((sensor, week), at)" +
                ")");
    }

    @Test
    public void testInject() throws Exception {
        CassandraInjector injector = new CassandraInjector(this.session);
        SensorDataRandom random = new SensorDataRandom.Builder().withAtRange(LocalDateTime.of(2015, 01, 01, 00, 00), LocalDateTime.of(2015, 01, 31, 00, 00)).build();
        SensorData data = random.next();

        injector.inject(data);

        PreparedStatement statement = this.session.prepare("SELECT * FROM ufcead.sensor_data WHERE sensor = ? AND week = ? AND at = ?");
        ResultSet results = this.session.execute(new BoundStatement(statement).bind(
            data.getName(), formattedWeek(data.getAt()), dateFromLocalDateTime(data.getAt())
        ));
        assertThat(results, Matchers.is(Matchers.iterableWithSize(1)));
        for (Row row : results) {
            System.out.println(row);
        }
    }

    @After
    public void tearDown() throws Exception {
        this.session.close();
        this.cluster.close();
    }
}