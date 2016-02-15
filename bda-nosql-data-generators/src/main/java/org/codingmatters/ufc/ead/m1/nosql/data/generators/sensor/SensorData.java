package org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor;

import java.time.OffsetDateTime;

/**
 * Created by vagrant on 2/14/16.
 */
public class SensorData {

    static Builder forSensor(String name) {
        return new Builder().withName(name);
    }

    private final String name;
    private final OffsetDateTime at;
    private final Double temperature;
    private final Double hygrometry;

    private SensorData(String name, OffsetDateTime at, Double temperature, Double hygrometry) {
        this.name = name;
        this.at = at;
        this.temperature = temperature;
        this.hygrometry = hygrometry;
    }

    public String getName() {
        return name;
    }

    public OffsetDateTime getAt() {
        return at;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getHygrometry() {
        return hygrometry;
    }

    static class Builder {
        private String name;
        private OffsetDateTime at;
        private Double temperature;
        private Double hygrometry;

        private Builder() {}

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withAt(OffsetDateTime at) {
            this.at = at;
            return this;
        }

        public Builder withTemperature(Double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder withHygrometry(Double hygrometry) {
            this.hygrometry = hygrometry;
            return this;
        }

        public SensorData build() {
            return new SensorData(this.name, this.at, this.temperature, this.hygrometry);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SensorData that = (SensorData) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (at != null ? !at.equals(that.at) : that.at != null) return false;
        if (temperature != null ? !temperature.equals(that.temperature) : that.temperature != null) return false;
        return hygrometry != null ? hygrometry.equals(that.hygrometry) : that.hygrometry == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (at != null ? at.hashCode() : 0);
        result = 31 * result + (temperature != null ? temperature.hashCode() : 0);
        result = 31 * result + (hygrometry != null ? hygrometry.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SensorData{" +
                "name='" + name + '\'' +
                ", at=" + at +
                ", temperature=" + temperature +
                ", hygrometry=" + hygrometry +
                '}';
    }
}
