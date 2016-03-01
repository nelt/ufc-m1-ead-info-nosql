package org.codingmatters.ufc.ead.m1.nosql.twitter.consumers;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.twitter.TwitterStreamConsumer;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by vagrant on 2/23/16.
 */
public class SampleMakerConsumer {

    static private final Logger log = LoggerFactory.getLogger(SampleMakerConsumer.class);

    public static void main(String[] args) {
        int consumeCountTarget = 100;
        File outFile;

        if(args.length > 0) {
            consumeCountTarget = Integer.parseInt(args[0]);
        }
        try {
            if (args.length > 1) {
                outFile = new File(args[1]);
                if (!outFile.exists()) {
                    outFile.createNewFile();
                }
            } else {
                outFile = File.createTempFile("twitter-sample", ".json");
            }
        } catch(IOException e) {
            throw new RuntimeException("error creating output file", e);
        }

        AtomicLong consumed = new AtomicLong(0);
        long startAt = System.currentTimeMillis();
        long elapsed = 0;

        try(
                TwitterStreamConsumer twitterStreamConsumer = new TwitterStreamConsumer.Builder().build();
                FileOutputStream out = new FileOutputStream(outFile)
        ) {
            SampleMakerConsumer sampler = new SampleMakerConsumer(out);
            twitterStreamConsumer.consume(status -> {
                Tweet tweet = Tweet.from(status);
                try {
                    sampler.addToSample(tweet);
                } catch (IOException e) {
                    log.error("error writong tweet " + tweet, e);
                    throw new RuntimeException(e);
                }
                consumed.incrementAndGet();
            });

            while(consumed.get() < consumeCountTarget) {
                Thread.sleep(10 * 1000L);
                elapsed = System.currentTimeMillis() - startAt;
                log.info("alrerady consumed {} tweets in {}ms...", consumed.get(), elapsed);
            }
            sampler.close();

            elapsed = System.currentTimeMillis() - startAt;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("consumed {} tweets in {}ms.", consumed.get(), elapsed);
    }

    private final OutputStream out;

    private final ExecutorService executor;
    private final JsonFactory jsonFactory;
    private final JsonGenerator jsonGenerator;
    private final ObjectMapper mapper;


    public SampleMakerConsumer(OutputStream out) throws IOException {
        this.out = out;
        this.executor = Executors.newSingleThreadExecutor();
        this.jsonFactory = new JsonFactory(); // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory
        this.jsonGenerator = this.jsonFactory.createGenerator(this.out, JsonEncoding.UTF8); // or Stream, Reader
        this.mapper = new ObjectMapper();
    }

    private void addToSample(Tweet tweet) throws IOException {
        this.executor.submit(() -> {
            try {
                this.mapper.writeValue(this.jsonGenerator, tweet);
                this.out.write("\n".getBytes());
                this.out.flush();
            } catch (IOException e) {
                log.error("error writing tweet " + tweet, e);
            }
        });
    }


    private void close() throws InterruptedException {
        this.executor.shutdown();
        while(! this.executor.isTerminated()) {
            this.executor.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
