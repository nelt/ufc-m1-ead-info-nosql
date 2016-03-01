package org.codingmatters.ufc.ead.m1.nosql.twitter.consumers;

import org.codingmatters.ufc.ead.m1.nosql.twitter.TwitterStreamConsumer;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vagrant on 2/23/16.
 */
public class LogTweetConsumer {

    static private final Logger log = LoggerFactory.getLogger(LogTweetConsumer.class);

    public static void main(String[] args) {
        int consumeCountTarget = 100;
        if(args.length > 0) {
            consumeCountTarget = Integer.parseInt(args[0]);
        }

        AtomicLong consumed = new AtomicLong(0);
        long startAt = System.currentTimeMillis();
        long elapsed = 0;


        try(TwitterStreamConsumer twitterStreamConsumer = new TwitterStreamConsumer.Builder().build()) {
            twitterStreamConsumer.consume(status -> {

                Tweet tweet = Tweet.from(status);
                log.info("**********************" +
                                "\n\tuser      : {} ({} followers)" +
                                "\n\ttext      : {}" +
                                "\n\tlanguage  : {}" +
                                "\n\tcreated at: {}" +
                                "\n\tmentions  : {}" +
                                "\n\thtags     : {}" +
                                "\n\tmentions  : {}",
                        tweet.getUser().getName(), tweet.getUser().getFollowersCount(),
                        tweet.getText(),
                        tweet.getLang(),
                        tweet.getCreatedAt(),
                        tweet.getMentions(),
                        tweet.getHtags(),
                        tweet.getLinks()
                );

                consumed.incrementAndGet();
            });

            while(consumed.get() < consumeCountTarget) {
                Thread.sleep(100L);
            }
            elapsed = System.currentTimeMillis() - startAt;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("consumed {} tweets in {}ms.", consumed.get(), elapsed);
    }

}
