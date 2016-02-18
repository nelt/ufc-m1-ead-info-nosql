package org.codingmatters.ufc.ead.m1.nosql.data.injectors;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.basho.riak.client.core.util.BinaryValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;

import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

/**
 * Created by vagrant on 2/16/16.
 */
public class RiakInjector {

    private final RiakClient client;
    private final ObjectMapper mapper;

    public RiakInjector(RiakClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }


    public void inject(SensorData data) throws InjectorException {
        byte[] json;
        try {
            json = this.mapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new InjectorException("error marshalling as json" + data, e);
        }

        Namespace bucket = new Namespace("sensor_" + data.getName());
        String key = data.getAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        RiakObject value = new RiakObject()
                .setContentType("application/json")
                .setValue(BinaryValue.create(json));

        try {
            this.client.execute(new StoreValue.Builder(value)
                    .withLocation(new Location(bucket, key))
                    .build());
        } catch (ExecutionException | InterruptedException e) {
            throw new InjectorException("error while injecting " + data, e);
        }
    }
}
