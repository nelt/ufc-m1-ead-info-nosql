package org.codingmatters.ufc.ead.m1.nosql.twitter.injector.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.InjectionException;
import org.codingmatters.ufc.ead.m1.nosql.twitter.injector.TweetInjector;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;

/**
 * Created by vagrant on 2/24/16.
 */
public class ESTweetInjector implements TweetInjector {
    private final Client client;
    private final ObjectMapper mapper;

    public ESTweetInjector(Client client, ObjectMapper mapper) {
        this.client = client;
        this.mapper = mapper;
    }

    @Override
    public String inject(Tweet tweet) throws InjectionException {
        try {
            IndexResponse response = this.client.prepareIndex("twitter", "tweet").setSource(this.mapper.writeValueAsBytes(tweet)).get();
            return response.getId();
        } catch (JsonProcessingException e) {
            throw new InjectionException("error indexing tweet " + tweet, e);
        }
    }
}
