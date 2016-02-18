package org.codingmatters.ufc.ead.m1.nosql.data.sample;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorData;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.sensor.SensorDataRandom;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.InjectorException;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.RiakInjector;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Created by vagrant on 2/17/16.
 */
public class SensorSampleToRiak {

    public static void main(String[] args) {
        RiakCluster cluster = Helpers.createRiakCluster();
        RiakClient client = new RiakClient(cluster);
        ObjectMapper mapper = Helpers.configureForDates(new ObjectMapper());

        try {
            LocalDateTime start = LocalDateTime.now().minusYears(2);
            LocalDateTime end = LocalDateTime.now();

            new SensorSampleToRiak(client, mapper, start, end).run();
        } finally {
            cluster.shutdown();
        }
    }

    private final RiakClient client;
    private final ObjectMapper mapper;
    private final RiakInjector injector;

    private final int sensorCount = 20;
    private final LocalDateTime start;
    private final LocalDateTime end;

    public SensorSampleToRiak(RiakClient client, ObjectMapper mapper, LocalDateTime start, LocalDateTime end) {
        this.client = client;
        this.mapper = mapper;
        this.start = start;
        this.end = end;
        this.injector = new RiakInjector(this.client, this.mapper);
    }

    private void run() throws RuntimeException {
        AtomicLong dataCount = new AtomicLong(0);

        ExecutorService pool = Executors.newFixedThreadPool(this.sensorCount);
        for(int i = 0 ; i < this.sensorCount ; i++) {
            String sensor = "sensor-" + i;
            pool.submit(() -> this.injectSensorSample(dataCount, sensor));
        }

        this.waitInjectionTerminated(dataCount, pool);
    }

    private void injectSensorSample(AtomicLong dataCount, String sensor) {
        LocalDateTime effectiveStart = this.start.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime effectiveEnd = this.end.withMinute(0).withSecond(0).withNano(0);

        SensorDataRandom random = new SensorDataRandom.Builder()
                .withSensors(sensor)
                .withAtRange(effectiveStart, effectiveEnd)
                .build();

        long sampleCount = Duration.between(effectiveStart, effectiveEnd).toMinutes() / random.getAtMinuteIncrement();
        for(long sample = 0 ; sample < sampleCount ; sample++) {
            SensorData data = random.next();
            try {
                this.injector.inject(data);
                dataCount.incrementAndGet();
            } catch (InjectorException e) {
                System.err.println("error injecting " + data + ", failing fast.");
                return;
            }
        }
    }

    private void waitInjectionTerminated(AtomicLong dataCount, ExecutorService pool) {
        long start = System.currentTimeMillis();
        pool.shutdown();
        while(! pool.isTerminated()) {
            try {
                boolean terminated = pool.awaitTermination(10, SECONDS);
                long ellapsed = System.currentTimeMillis() - start;
                if(!terminated) {
                    System.out.println(dataCount.get() + " sensor data injected in " + Helpers.formatDuration(ellapsed) + "...");
                } else {
                    System.out.println("done injecting " + dataCount.get() + " sensor data in " + Helpers.formatDuration(ellapsed) + ".");
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("error waiting for poool shutdown", e);
            }
        }
        System.out.println("done.");
    }
}
