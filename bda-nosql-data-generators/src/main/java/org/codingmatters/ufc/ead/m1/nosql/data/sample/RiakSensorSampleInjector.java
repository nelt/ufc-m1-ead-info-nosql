package org.codingmatters.ufc.ead.m1.nosql.data.sample;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.sensor.RiakInjector;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;

import java.time.LocalDateTime;

/**
 * Created by vagrant on 2/19/16.
 */
public class RiakSensorSampleInjector {

    public static void main(String[] args) {
        RiakCluster cluster = Helpers.createRiakCluster();
        RiakClient client = new RiakClient(cluster);
        ObjectMapper mapper = Helpers.configureForDates(new ObjectMapper());

        try {
            LocalDateTime start = LocalDateTime.now().minusYears(2);
            LocalDateTime end = LocalDateTime.now();

            new SensorSampleInjector(new RiakInjector(client, mapper), start, end).run();
        } finally {
            cluster.shutdown();
        }
    }
}
