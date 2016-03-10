package org.codingmatters.ufc.ead.m1.nosql.data.web.service;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.web.service.sensor.RiakSensorDataService;
import org.codingmatters.ufc.ead.m1.nosql.data.web.service.sensor.SensorDataList;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 2/17/16.
 */
@Ignore
public class RiakSensorDataServiceTest {

    private RiakCluster cluster;
    private RiakClient client;
    private ObjectMapper mapper;
    private RiakSensorDataService service;


    @Before
    public void setUp() throws Exception {
        RiakNode node = new RiakNode.Builder()
                .withRemoteAddress(resolver().resolve("riak"))
                .withRemotePort(8087)
                .build();
        this.cluster = new RiakCluster.Builder(node)
                .build();
        this.cluster.start();
        this.client = new RiakClient(this.cluster);
        this.mapper = Helpers.configureForDates(new ObjectMapper());

        this.service = new RiakSensorDataService(this.client, this.mapper);
    }

    @After
    public void tearDown() throws Exception {
        this.cluster.shutdown();
    }

    @Test
    public void testAWeek() throws Exception {
        SensorDataList weekData = this.service.weekData("sensor-1", 2015, 51);
        for (SensorData sensorData : weekData.getData()) {
            System.out.println(sensorData);
        }
        System.out.println("retrieved " + weekData.getData().size() + " sensor data in " + Helpers.formatDuration(weekData.getTimeSpent()));
    }
}