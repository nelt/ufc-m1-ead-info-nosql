package org.codingmatters.ufc.ead.m1.nosql.twitter.injector.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.TweetSampleInjector;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 2/25/16.
 */
public class ESTweetSampleInjector {
    public static void main(String[] args) {
        Client client = null;
        try {
            InetAddress host = InetAddress.getByName(resolver().resolve("elastic"));
            client = TransportClient.builder().build()
                    .addTransportAddress(new InetSocketTransportAddress(host, 9300));

            if(! client.admin().indices().exists(new IndicesExistsRequest("twitter")).actionGet().isExists()) {
                client.admin().indices().create(new CreateIndexRequest("twitter")).actionGet();
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException("error creating elastic search client", e);
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            new TweetSampleInjector(args, new ESTweetInjector(client, mapper), 10).run();
        } catch (InterruptedException | IOException e) {
            throw  new RuntimeException("error running sample injector", e);
        } finally {
            client.close();
        }
    }
}
