package org.codingmatters.ufc.ead.m1.nosql.twitter.bean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created by vagrant on 3/21/16.
 */
public class TweetTest {

    @Test
    public void testJson() throws Exception {
        /*
         {
         "user":
            {"name":"psychedelic princess","followersCount":304},
         "text":"RT @5SOS_SGFam: Fans are already inside the venue for soundcheck #SLFLTokyo",
         "lang":"en",
         "createdAt":1456213158000,
         "mentions":["5SOS_SGFam"],
         "htags":["SLFLTokyo"],
         "links":[]}

         */
        Tweet tweet = new Tweet.Builder()
                .withUser(new User.Builder()
                        .withName("psychedelic princess")
                        .withFollowersCount(304)
                        .build())
                .withText("RT @5SOS_SGFam: Fans are already inside the venue for soundcheck #SLFLTokyo")
                .withLang("en")
                .withCreatedAt(new Date(1456213158000L))
                .withMentions("5SOS_SGFam")
                .withHtags("SLFLTokyo")
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        String json = mapper.writeValueAsString(tweet);
        System.out.println(json);
    }
}