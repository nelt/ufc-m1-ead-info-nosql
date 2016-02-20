package org.codingmatters.ufc.ead.m1.nosql.data.injectors;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;

import java.time.LocalDateTime;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers.dateFromLocalDateTime;

/**
 * Created by vagrant on 2/18/16.
 */
public class CassandraInjector implements SensorDataInjector {

    private final Session session;
    private PreparedStatement stmt;

    public CassandraInjector(Session session) {
        this.session = session;
        this.stmt = this.session.prepare("" +
                "INSERT INTO ufcead.sensor_data(sensor, week, at, temperature, hygrometry) VALUES (?, ?, ?, ?, ?)");
    }

    @Override
    public void inject(SensorData data) throws InjectorException {
        String week = Helpers.formattedWeek(data.getAt());
        this.session.execute(new BoundStatement(this.stmt).bind(
            data.getName(), week, dateFromLocalDateTime(data.getAt()), data.getTemperature(), data.getHygrometry()
        ));
    }

}
