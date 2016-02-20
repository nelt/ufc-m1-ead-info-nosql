package org.codingmatters.ufc.ead.m1.nosql.data.service;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 2/19/16.
 */
@Ignore
public class CassandraSensorDataServiceTest {

    private Cluster cluster;
    private Session session;
    private CassandraSensorDataService service;

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

        this.service = new CassandraSensorDataService(this.session);
    }


    @Test
    public void testAWeek() throws Exception {
        SensorDataList weekData = this.service.weekData("sensor-1", 2015, 51);
        for (SensorData sensorData : weekData.getData()) {
            System.out.println(sensorData);
        }
        System.out.println("retrieved " + weekData.getData().size() + " sensor data in " + Helpers.formatDuration(weekData.getTimeSpent()));
    }


    @After
    public void tearDown() throws Exception {
        this.session.close();
        this.cluster.close();
    }
}