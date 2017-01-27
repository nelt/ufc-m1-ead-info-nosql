package org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter.TweetSampleInjector;

import java.io.IOException;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 26/04/16.
 */
public class CassandraTweetSampleInjector {
    public static void main(String[] args) {
        Cluster cluster = Cluster.builder().addContactPoint(resolver().resolve("cassandra")).build();
        Session client = cluster.connect();
        client.execute("CREATE KEYSPACE IF NOT EXISTS ufcead WITH REPLICATION = " +
                "{ 'class' : 'NetworkTopologyStrategy', 'datacenter1' : 1 }");
        client.execute("CREATE TABLE IF NOT EXISTS ufcead.tweets (" +
                "tweetid text, " +
                "username text, " +
                "text text, " +
                "createdAt timestamp, " +
                "primary key (tweetid)" +
                ")");
        client.execute("CREATE TABLE IF NOT EXISTS ufcead. user_timeline (" +
                "username text, " +
                "createdAt timestamp, " +
                "tweetid text, " +
                "primary key (username, createdAt)" +
                ")");
        client.execute("CREATE TABLE IF NOT EXISTS ufcead. htag_timeline (" +
                "htag text, " +
                "createdAt timestamp, " +
                "tweetid text, " +
                "primary key (htag, createdAt));");

        try {
            new TweetSampleInjector(args, new CassandraTweetInjector(client), 10).run();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            client.close();
            cluster.close();
        }
    }
}
