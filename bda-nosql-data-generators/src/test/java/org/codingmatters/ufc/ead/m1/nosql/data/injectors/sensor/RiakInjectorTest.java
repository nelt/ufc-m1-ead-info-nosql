package org.codingmatters.ufc.ead.m1.nosql.data.injectors.sensor;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorDataRandom;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.sensor.riak.RiakInjector;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

/**
 * Created by vagrant on 2/16/16.
 */
@Ignore
public class RiakInjectorTest {

    private RiakCluster cluster;
    private RiakClient client;
    private ObjectMapper mapper;


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
    }

    @After
    public void tearDown() throws Exception {
        this.cluster.shutdown();
    }

    @Test
    public void testInjectOne() throws Exception {
        RiakInjector injector = new RiakInjector(this.client, this.mapper);
        SensorDataRandom random = new SensorDataRandom.Builder().withAtRange(LocalDateTime.of(2015, 01, 01, 00, 00), LocalDateTime.of(2015, 01, 31, 00, 00)).build();
        SensorData data = random.next();
        injector.inject(data);

        Namespace bucket = new Namespace("sensor_" + data.getName());
        String key = data.getAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        String value = this.client.execute(new FetchValue.Builder(new Location(bucket, key)).build()).getValue(String.class);

        System.out.println("looked up " + bucket + " / " + key);

        assertThat(value, is(not(nullValue())));
        assertThat(value, is(this.mapper.writeValueAsString(data)));

        assertThat(this.mapper.readValue(value, SensorData.Builder.class).build(), is(data));
    }
}