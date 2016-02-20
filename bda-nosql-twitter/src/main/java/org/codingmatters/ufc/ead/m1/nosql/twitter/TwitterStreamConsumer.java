package org.codingmatters.ufc.ead.m1.nosql.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.DefaultStreamingEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.util.HashSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nel on 06/01/16.
 */
public class TwitterStreamConsumer {

    static private Logger log = LoggerFactory.getLogger(TwitterStreamConsumer.class);
    private BlockingQueue<String> queue;

    public static void main(String[] args) {
        String consumerKey = "Pn4JDnhqd065Z00oovMo3qYSN";
        String consumerSecret = "prMiQmo7dVTYuqPx91hBItySXBQfdvmqHPQA5KmWGxic0GApsS";
        String token = "409077150-IQvfx5mnxzpDtFW4yOugrL6jewbLP6xXuJRa6x4k";
        String secret = "vai1ICQsGZt9vyBulbXiNnwA3VsAKL1iOQr5CxkmESLbl";

        AtomicLong consumed = new AtomicLong(0);
        long startAt = System.currentTimeMillis();
        long elapsed = 0;

        TwitterStreamConsumer twitterStreamConsumer = new TwitterStreamConsumer(consumerKey, consumerSecret, token, secret);
        try {
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

            while(consumed.get() < 100) {
                Thread.sleep(100L);
            }
            elapsed = System.currentTimeMillis() - startAt;
         } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                twitterStreamConsumer.stop();
            } catch (InterruptedException e) {
                throw new RuntimeException("error stopping consumer", e);
            }
        }

        log.info("consumed {} tweets in {}ms.", consumed.get(), elapsed);
    }


    private final Authentication auth;
    private ExecutorService service;
    private BasicClient client;

    public TwitterStreamConsumer(String consumerKey, String consumerSecret, String token, String secret) {
        this.auth = new OAuth1(consumerKey, consumerSecret, token, secret);
    }


    public void consume(StatusConsumer consumer) throws InterruptedException {
        // Create an appropriately sized blocking queue
        this.queue = new LinkedBlockingQueue<>(10000);

        // Define our endpoint: By default, delimited=length is set (we need this for our processor)
        // and stall warnings are on.
        DefaultStreamingEndpoint endpoint = new StatusesSampleEndpoint();
        endpoint.addQueryParameter("language", "en");


        // Create a new BasicClient. By default gzip is enabled.
        this.client = new ClientBuilder()
                .hosts(Constants.STREAM_HOST)
                .endpoint(endpoint)
                .authentication(auth)
                .processor(new StringDelimitedProcessor(queue))
                .build();

        // Create an executor service which will spawn threads to do the actual work of parsing the incoming messages and
        // calling the listeners on each message
        int numProcessingThreads = 4;
        this.service = Executors.newFixedThreadPool(numProcessingThreads);

        // Wrap our BasicClient with the twitter4j client
        Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(
                client, queue, Lists.newArrayList(new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                consumer.onStatus(status);
            }

        }), service);

        // Establish a connection
        t4jClient.connect();
        for (int threads = 0; threads < numProcessingThreads; threads++) {
            // This must be called once per processing thread
            t4jClient.process();
        }
    }

    private void stop() throws InterruptedException {
        log.info("stopping client...");
        client.stop();
        log.info("client stopped.");

        log.info("queue empty ? {}", this.queue.isEmpty());

        log.info("shutting down executors...");
        service.shutdown();
        service.awaitTermination(10L, TimeUnit.SECONDS);
        if(! service.isTerminated()) {
            log.info("forcing service shutdown...");
            service.shutdownNow();
        }
        log.info("executors shut down.");
    }
}