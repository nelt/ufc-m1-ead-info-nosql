package org.codingmatters.ufc.ead.m1.nosql.data.service.tweet;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vagrant on 3/9/16.
 */
public class MongoTweetSearchService {
    static private final Logger log = LoggerFactory.getLogger(MongoTweetSearchService.class);

    private final MongoDatabase db;

    public MongoTweetSearchService(MongoDatabase db) {
        this.db = db;
    }

    public String process(Request request, Response response) {
        String search = request.queryParams("search");
        String [] htags = request.queryParamsValues("htags");


        MongoCollection<Document> tweets = this.db.getCollection("tweets");
        Document filter = this.createQueryFromRequest(search, htags);

        TweetResultPage result = new TweetResultPage();
        result.setTook(0);
        result.setCurrentSearch(search);
        result.setCurrentHtags(htags);

        FindIterable<Document> queryResults;
        long start = System.currentTimeMillis();
        if(filter == null) {
            result.setTotalResults(tweets.count());
            queryResults = tweets.find().limit(100);
        } else {
            result.setTotalResults(tweets.count(filter));
            queryResults = tweets.find(filter).limit(100);
        }
        result.setTook(System.currentTimeMillis() - start);

        for (Document queryResult : queryResults) {
            Tweet tweet = new Tweet.Builder()
                    .withText(queryResult.getString("text"))
                    .withCreatedAt(queryResult.getDate("createdAt"))
                    .withUser(new User.Builder().withName(queryResult.getString("user.name")).build())
                    .build();
            result.addTweet(tweet);
        }

        for (Document htagDoc : this.db.getCollection("htags").find().sort(new Document("count", -1)).limit(100)) {
            result.addHtagFacet(htagDoc.getString("htag"), htagDoc.getInteger("count"));
        }

        return result.render();
    }

    private Document createQueryFromRequest(String search, String[] htags) {
        if((search == null || search.isEmpty()) && (htags == null || htags.length == 0)) {
            return null;
        }
        Document result = new Document();
        if(search != null && ! search.isEmpty()) {
            result.append("$text", new Document("$search", search));
        }
        if(htags != null && htags.length > 0) {
            if(htags.length == 1) {
                result.append("htags", htags[0]);
            } else {
                List<Document> htagsCriteria = new ArrayList<>(htags.length);
                for (String htag : htags) {
                    htagsCriteria.add(new Document("htags", htag));
                }
                result.append("$and", htagsCriteria);
            }
        }
        return result;
    }
}
