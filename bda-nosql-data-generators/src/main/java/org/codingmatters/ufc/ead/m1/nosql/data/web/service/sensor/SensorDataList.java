package org.codingmatters.ufc.ead.m1.nosql.data.web.service.sensor;

import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;

import java.util.List;

/**
 * Created by vagrant on 2/18/16.
 */
public class SensorDataList {
    private final long timeSpent;
    private final List<SensorData> data;

    public SensorDataList(long timeSpent, List<SensorData> data) {
        this.timeSpent = timeSpent;
        this.data = data;
    }

    public long getTimeSpent() {
        return timeSpent;
    }

    public List<SensorData> getData() {
        return data;
    }
}
