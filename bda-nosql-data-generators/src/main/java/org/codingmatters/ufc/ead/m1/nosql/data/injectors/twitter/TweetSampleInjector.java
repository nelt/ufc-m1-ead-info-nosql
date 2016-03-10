package org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vagrant on 2/25/16.
 */
public class TweetSampleInjector {

    static final Logger log = LoggerFactory.getLogger(TweetSampleInjector.class);

    private final File sample;
    private final TweetInjector injector;
    private final ExecutorService pool;
    private final int executorCount;
    private final AtomicLong injected = new AtomicLong(0);

    private final JsonFactory jsonFactory;
    private final JsonParser jsonParser;
    private final ObjectMapper mapper;

    public TweetSampleInjector(String [] args, TweetInjector injector, int executorCount) throws IOException {
        this.injector = injector;
        if(args.length < 1) {
            throw new RuntimeException("usage : <sample file path>");
        }

        this.sample = new File(args[0]);
        if(! this.sample.exists()) {
            throw new RuntimeException("sample file must exist (" + this.sample.getAbsolutePath() + ").");
        }

        this.executorCount = executorCount;
        this.pool = Executors.newFixedThreadPool(executorCount);

        this.jsonFactory = new JsonFactory();
        this.jsonParser = this.jsonFactory.createParser(this.sample);
        this.mapper = new ObjectMapper();
    }

    public void run() throws InterruptedException {
        for(int i = 0 ; i < this.executorCount ; i++) {
            this.pool.submit(this::runInjector);
        }

        this.pool.shutdown();

        long start = System.currentTimeMillis();
        while(! this.pool.isTerminated()) {
            log.info("injected " + this.injected.get() + " tweets in " + (System.currentTimeMillis() - start) + " ms. ...");
            this.pool.awaitTermination(10, TimeUnit.SECONDS);
        }
        log.info("injected " + this.injected.get() + " tweets in " + (System.currentTimeMillis() - start) + " ms.");
    }

    private void runInjector() {
        Tweet tweet = this.next();
        while(tweet != null) {
            try {
                this.injector.inject(tweet);
                this.injected.incrementAndGet();
                tweet = this.next();
            } catch (InjectionException e) {
                log.error("error injecting tweet " + tweet, e);
            }
        }
    }

    private synchronized Tweet next() {
        try {
            if(this.jsonParser.nextToken() == JsonToken.START_OBJECT) {
                return mapper.readValue(this.jsonParser, Tweet.Builder.class).build();
            }
        } catch (IOException e) {
            log.error("error reading json sample", e);
        }
        return null;
    }

}
