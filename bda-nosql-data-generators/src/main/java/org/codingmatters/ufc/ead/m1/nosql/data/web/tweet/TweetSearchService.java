package org.codingmatters.ufc.ead.m1.nosql.data.web.tweet;

import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.User;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by vagrant on 3/3/16.
 */
public class TweetSearchService {

    static private Logger log = LoggerFactory.getLogger(TweetSearchService.class);

    private final Client client;

    public TweetSearchService(Client client) {
        this.client = client;
    }

    public String process(Request request, Response response) {
        try {
            String search = request.queryParams("search");
            String [] htags = request.queryParamsValues("htags");

            SearchResponse queryResponse = this.client.prepareSearch("twitter")
                    .setTypes("tweet")
                    .setQuery(this.createQueryFromRequest(search, htags))
                    .setSize(100)
                    .addField("text")
                    .addField("user.name")
                    .addField("createdAt")
                    .addField("htags")
                    .addAggregation(
                            AggregationBuilders.terms("htags").field("htags").size(100)
                    )
                    .execute().get(5, TimeUnit.SECONDS);

            TweetResultPage result = new TweetResultPage();
            result.setTotalResults(queryResponse.getHits().getTotalHits());
            result.setTook(queryResponse.getTookInMillis());
            result.setCurrentSearch(search);
            result.setCurrentHtags(htags);

            for (SearchHit hit : queryResponse.getHits()) {
                log.debug("fields : {}", hit.getFields().keySet());
                Tweet tweet = new Tweet.Builder()
                        .withText(hit.getFields().get("text").getValue())
                        .withCreatedAt(Date.from(Instant.ofEpochMilli(hit.getFields().get("createdAt").getValue())))
                        .withUser(new User.Builder().withName(hit.getFields().get("user.name").getValue()).build())
                        .build();
                result.addTweet(tweet);
            }

            Terms htagTerms = queryResponse.getAggregations().get("htags");
            for (Terms.Bucket bucket : htagTerms.getBuckets()) {
                result.addHtagFacet(bucket.getKeyAsString(), bucket.getDocCount());
            }


            return result.render();
        } catch (InterruptedException | ExecutionException e) {
            log.error("error while processing query", e);
            response.status(500);
        } catch (TimeoutException e) {
            log.error("timeout while executing query", e);
            response.status(408);
        }
        return null;
    }

    private QueryBuilder createQueryFromRequest(String search, String[] htags) {
        QueryBuilder query;
        if(search == null || search.trim().isEmpty()) {
            query = QueryBuilders.matchAllQuery();
        } else {
            query = QueryBuilders.termQuery("text", search);
        }
        if(htags != null && htags.length > 0) {
            BoolQueryBuilder filter = QueryBuilders.boolQuery();
            for (String htag : htags) {
                filter.must(QueryBuilders.termQuery("htags", htag));
            }
            return QueryBuilders.boolQuery()
                    .must(query)
                    .filter(filter);
        } else {
            return query;
        }
    }
}
