package org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.Random;

/**
 * Created by vagrant on 2/14/16.
 */
public class SensorDataRandom {

    private final Random random;
    private final long seed;

    private final String [] sensors;
    private final double minTemperature;
    private final double maxTemperature;
    private final double minHygrometry;
    private final double maxHygrometry;
    private final OffsetDateTime minAt;
    private final OffsetDateTime maxAt;

    private SensorDataRandom(Long seed, String[] sensors, double minTemperature, double maxTemperature, double minHygrometry, double maxHygrometry, OffsetDateTime minAt, OffsetDateTime maxAt) {
        this.minAt = minAt;
        this.maxAt = maxAt;
        this.seed = seed != null ? seed : System.currentTimeMillis();
        this.random = new Random(this.seed);

        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.minHygrometry = minHygrometry;
        this.maxHygrometry = maxHygrometry;
        this.sensors = sensors;
    }

    public SensorData next() {
        return SensorData.forSensor(this.nextSensor())
                .withTemperature(this.nextTemperature())
                .withHygrometry(this.nextHygrometry())
                .withAt(this.nextAt())
                .build();
    }

    private String nextSensor() {
        int index = this.random.nextInt(this.sensors.length);
        return this.sensors[index];
    }

    private double nextTemperature() {
        return this.nextDoubleInRange(this.minTemperature, this.maxTemperature);
    }

    private Double nextHygrometry() {
        return this.nextDoubleInRange(this.minHygrometry, this.maxHygrometry);
    }

    private double nextDoubleInRange(double min, double max) {
        return this.random.nextDouble() * (max - min) + min;
    }

    private OffsetDateTime nextAt() {
        if(this.minAt == null || this.maxAt == null) {
            return OffsetDateTime.now();
        } else {
            long min = this.minAt.toInstant().toEpochMilli();
            long max = this.maxAt.toInstant().toEpochMilli();
            long millis = (long) Math.floor(this.random.nextDouble() * (max - min) + min);
            return OffsetDateTime.ofInstant(Instant.ofEpochMilli(millis), this.minAt.getOffset());
        }
    }

    static public class Builder {
        private String [] sensors = {"dummy"};
        private double minTemperature = -25;
        private double maxTemperature = 35;
        private double minHygrometry = 0.15;
        private double maxHygrometry = 0.85;
        private OffsetDateTime minAt = null;
        private OffsetDateTime maxAt = null;

        public Builder withSensors(String ... sensors) {
            this.sensors = sensors;
            return this;
        }

        public Builder withTemperatureRange(double min, double max) {
            this.minTemperature = min;
            this.maxTemperature = max;
            return this;
        }

        public Builder withHygrometryRange(double min, double max) {
            this.minHygrometry = min;
            this.maxHygrometry = max;
            return this;
        }

        public Builder withAtRange(OffsetDateTime min, OffsetDateTime max) {
            this.minAt = min;
            this.maxAt = max;
            return this;
        }

        public SensorDataRandom build() {
            return this.buildWithSeed(System.currentTimeMillis());
        }
        public SensorDataRandom buildWithSeed(long seed) {
            return new SensorDataRandom(
                    seed,
                    this.sensors,
                    this.minTemperature, this.maxTemperature,
                    this.minHygrometry, this.maxHygrometry,
                    this.minAt, this.maxAt
            );
        }
    }
}
