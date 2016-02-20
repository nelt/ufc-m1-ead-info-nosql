package org.codingmatters.ufc.ead.m1.nosql.data.service;

import com.datastax.driver.core.*;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.service.exception.ServiceException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by vagrant on 2/19/16.
 */
public class CassandraSensorDataService implements SensorDataService {

    private final Session session;
    private final PreparedStatement statement;

    public CassandraSensorDataService(Session session) {
        this.session = session;
        this.statement = this.session.prepare("SELECT * FROM ufcead.sensor_data WHERE sensor = ? AND week = ?");
    }

    @Override
    public SensorDataList weekData(String sensor, int year, int week) throws ServiceException {
        long retrievalStart = System.currentTimeMillis();
        List<SensorData> data = new LinkedList<>();

//        LocalDateTime start = LocalDateTime.of(year, 1, 1, 0, 0).plusWeeks(week);
//        start = start.minusDays(start.getDayOfWeek().getValue() + 1);
//        LocalDateTime end = start.plusWeeks(1);

        ResultSet results = this.session.execute(new BoundStatement(this.statement).bind(
            sensor, String.format("%04d-%02d", year, week)
        ));
        for (Row result : results) {
            data.add(new SensorData.Builder()
                    .withName(sensor)
                    .withAt(LocalDateTime.ofInstant(result.get("at", Date.class).toInstant(), ZoneId.systemDefault()))
                    .withTemperature(result.get("temperature", Double.class))
                    .withHygrometry(result.get("hygrometry", Double.class))
                    .build());
        }

        return new SensorDataList(System.currentTimeMillis() - retrievalStart, data);
    }
}
