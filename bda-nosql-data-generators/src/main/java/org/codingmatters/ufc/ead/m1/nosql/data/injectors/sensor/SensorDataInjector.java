package org.codingmatters.ufc.ead.m1.nosql.data.injectors.sensor;

import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.InjectorException;

/**
 * Created by vagrant on 2/18/16.
 */
public interface SensorDataInjector {
    void inject(SensorData data) throws InjectorException;
}
