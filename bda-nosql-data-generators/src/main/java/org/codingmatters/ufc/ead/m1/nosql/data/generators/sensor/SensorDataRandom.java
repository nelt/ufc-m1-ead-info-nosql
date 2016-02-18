package org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor;

import org.codingmatters.ufc.ead.m1.nosql.data.generators.util.Randomizer;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * Created by vagrant on 2/14/16.
 */
public class SensorDataRandom {

    private final Randomizer random;

    private final String [] sensors;
    private final double minTemperature;
    private final double maxTemperature;
    private final double minHygrometry;
    private final double maxHygrometry;

    private final LocalDateTime minAt;
    private final LocalDateTime maxAt;
    private final long atMinuteIncrement = 60;
    private LocalDateTime nextAtValue;

    public long getAtMinuteIncrement() {
        return atMinuteIncrement;
    }

    private SensorDataRandom(Long seed, String[] sensors, double minTemperature, double maxTemperature, double minHygrometry, double maxHygrometry, LocalDateTime minAt, LocalDateTime maxAt) {
        this.minAt = minAt;
        this.maxAt = maxAt;
        this.nextAtValue = this.minAt;

        this.random = new Randomizer(seed);

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
        String [] candidates = this.sensors;
        return this.random.nextFromTable(candidates);
    }

    private double nextTemperature() {
        return this.random.nextDoubleInRange(this.minTemperature, this.maxTemperature);
    }

    private Double nextHygrometry() {
        return this.random.nextDoubleInRange(this.minHygrometry, this.maxHygrometry);
    }

    private LocalDateTime nextAt() {
        try {
            return this.nextAtValue;
        } finally {
            this.nextAtValue = this.nextAtValue.plusMinutes(this.atMinuteIncrement);
            if(this.nextAtValue.isAfter(this.maxAt)) {
                this.nextAtValue = this.minAt;
            }
        }
    }



    static public class Builder {
        private String [] sensors = {"sensor"};
        private double minTemperature = -25;
        private double maxTemperature = 35;
        private double minHygrometry = 0.15;
        private double maxHygrometry = 0.85;
        private LocalDateTime minAt = LocalDateTime.MIN;
        private LocalDateTime maxAt = LocalDateTime.MAX;

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

        public Builder withAtRange(LocalDateTime min, LocalDateTime max) {
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
