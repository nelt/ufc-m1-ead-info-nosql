package org.codingmatters.ufc.ead.m1.nosql.data.service;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.basho.riak.client.core.query.RiakObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.service.exception.ServiceException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by vagrant on 2/17/16.
 */
public class RiakSensorDataService implements SensorDataService {

    private final RiakClient client;
    private final ObjectMapper mapper;

    public RiakSensorDataService(RiakClient client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public SensorDataList weekData(String sensor, int year, int week) throws ServiceException {
        long retrievalStart = System.currentTimeMillis();
        List<SensorData> data = new LinkedList<>();

        Namespace bucket = new Namespace("sensor_" + sensor);

        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0).plusWeeks(week);
        start = start.minusDays(start.getDayOfWeek().getValue() + 1);
        LocalDateTime end = start.plusWeeks(1);

        for(LocalDateTime date = start ; date.isBefore(end) ; date = date.plusMinutes(60)) {
            String key = date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Location location = new Location(bucket, key);
            try {
                FetchValue.Response response = this.client.execute(new FetchValue.Builder(location).build());
                if(! response.isNotFound()) {
                    byte[] json = response.getValue(RiakObject.class).getValue().getValue();
                    data.add(this.mapper.readValue(json, SensorData.Builder.class).build());
                }
            } catch (ExecutionException | InterruptedException | IOException e) {
                throw new ServiceException("error fetching data", e);
            }
        }

        return new SensorDataList(System.currentTimeMillis() - retrievalStart, data);
    }
}
