package org.codingmatters.ufc.ead.m1.nosql.data.sample.sensor;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.sensor.CassandraInjector;

import java.time.LocalDateTime;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 2/19/16.
 */
public class CassandraSensorSampleInjector {

    public static void main(String[] args) {
        Cluster cluster = Cluster.builder().addContactPoint(resolver().resolve("cassandra")).build();
        Session client = cluster.connect();
        client.execute("CREATE KEYSPACE IF NOT EXISTS ufcead WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 1 }");
        client.execute("CREATE TABLE IF NOT EXISTS ufcead.sensor_data ( " +
                "sensor text, " +
                "week text, " +
                "at timestamp, " +
                "temperature double, " +
                "hygrometry double, " +
                "PRIMARY KEY ((sensor, week), at)" +
                ")");
        try {
            LocalDateTime start = LocalDateTime.now().minusYears(2);
            LocalDateTime end = LocalDateTime.now();

            new SensorSampleInjector(new CassandraInjector(client), start, end).run();
        } finally {
            client.close();
            cluster.close();
        }
    }
}
