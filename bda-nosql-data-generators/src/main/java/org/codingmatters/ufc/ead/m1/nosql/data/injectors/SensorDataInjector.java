package org.codingmatters.ufc.ead.m1.nosql.data.injectors;

import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;

/**
 * Created by vagrant on 2/18/16.
 */
public interface SensorDataInjector {
    void inject(SensorData data) throws InjectorException;
}
