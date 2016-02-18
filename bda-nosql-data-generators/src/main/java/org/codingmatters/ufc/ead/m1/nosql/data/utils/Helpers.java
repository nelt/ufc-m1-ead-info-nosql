package org.codingmatters.ufc.ead.m1.nosql.data.utils;

import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.UnknownHostException;
import java.time.Duration;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 2/17/16.
 */
public class Helpers {
    static public String formatDuration(long elapsed) {
        return Duration.ofMillis(elapsed).toString();
    }

    static public ObjectMapper configureForDates(ObjectMapper mapper) {
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
        return mapper;
    }

    public static RiakCluster createRiakCluster() {
        RiakCluster cluster;
        try {
            RiakNode node = new RiakNode.Builder()
                    .withRemoteAddress(resolver().resolve("riak"))
                    .withRemotePort(8087)
                    .build();
            cluster = new RiakCluster.Builder(node)
                    .build();
            cluster.start();
        } catch (UnknownHostException e) {
            throw new RuntimeException("error creating riak client", e);
        }
        return cluster;
    }
}
