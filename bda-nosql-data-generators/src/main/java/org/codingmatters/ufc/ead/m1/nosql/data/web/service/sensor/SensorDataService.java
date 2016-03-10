package org.codingmatters.ufc.ead.m1.nosql.data.web.service.sensor;

import org.codingmatters.ufc.ead.m1.nosql.data.web.service.exception.ServiceException;

/**
 * Created by vagrant on 2/19/16.
 */
public interface SensorDataService {
    SensorDataList weekData(String sensor, int year, int week) throws ServiceException;
}
