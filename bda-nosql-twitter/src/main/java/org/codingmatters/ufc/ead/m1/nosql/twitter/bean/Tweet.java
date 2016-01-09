package org.codingmatters.ufc.ead.m1.nosql.twitter.bean;

import twitter4j.Status;

import java.util.Date;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ubuntu on 09/01/16.
 */
public class Tweet {

    public static final Pattern MENTIONS_PATTERN = Pattern.compile("@(\\w+)");
    public static final Pattern HTAG_PATTERN = Pattern.compile("#(\\w+)");
    public static final Pattern LINK_PATTERN = Pattern.compile("(https://[^\\s]+)");

    public static Tweet from(Status status) {
        return new Tweet(
                new User(status.getUser().getName(), status.getUser().getFollowersCount()),
                status.getText(),
                status.getLang(),
                status.getCreatedAt(),
                extractReferences(status, MENTIONS_PATTERN),
                extractReferences(status, HTAG_PATTERN),
                extractReferences(status, LINK_PATTERN)
                );
    }

    private static HashSet<String> extractReferences(Status status, Pattern pattern) {
        Matcher parser = pattern.matcher(status.getText());
        HashSet<String> mentions = new HashSet<>();
        while(parser.find()) {
            mentions.add(parser.group(1));
        }
        return mentions;
    }


    private final User user;
    private final String text;
    private final String lang;
    private final Date createdAt;
    private final HashSet<String> mentions;
    private final HashSet<String> htags;
    private final HashSet<String> links;

    public Tweet(User user, String text, String lang, Date createdAt, HashSet<String> mentions, HashSet<String> htags, HashSet<String> links) {
        this.user = user;
        this.text = text;
        this.lang = lang;
        this.createdAt = createdAt;
        this.mentions = mentions;
        this.htags = htags;
        this.links = links;
    }

    public User getUser() {
        return user;
    }

    public String getText() {
        return text;
    }

    public String getLang() {
        return lang;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public HashSet<String> getMentions() {
        return mentions;
    }

    public HashSet<String> getHtags() {
        return htags;
    }

    public HashSet<String> getLinks() {
        return links;
    }
}
