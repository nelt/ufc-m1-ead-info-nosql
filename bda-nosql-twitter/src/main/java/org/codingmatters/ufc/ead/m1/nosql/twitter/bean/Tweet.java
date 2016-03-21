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
    
    static public class Builder {
        private User user;
        private String text;
        private String lang;
        private Date createdAt;
        private HashSet<String> mentions = new HashSet<>();
        private HashSet<String> htags = new HashSet<>();
        private HashSet<String> links = new HashSet<>();

        public Builder withUser(User user) {
            this.user = user;
            return this;
        }

        public Builder withText(String text) {
            this.text = text;
            return this;
        }

        public Builder withLang(String lang) {
            this.lang = lang;
            return this;
        }

        public Builder withCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder withMentions(String ... mentions) {
            this.mentions.clear();
            if(mentions != null) {
                for (String mention : mentions) {
                    this.mentions.add(mention);
                }
            }
            return this;
        }

        public Builder withHtags(String ... htags) {
            this.htags.clear();
            if(htags != null) {
                for (String htag : htags) {
                    this.htags.add(htag);
                }
            }
            return this;
        }

        public Builder withLinks(String ... links) {
            this.links.clear();
            if(links != null) {
                for (String link : links) {
                    this.htags.add(link);
                }
            }
            return this;
        }

        public Tweet build() {
            return new Tweet(this.user, this.text, this.lang, this.createdAt, this.mentions, this.htags, this.links);
        }

        public void setUser(User.Builder user) {
            this.user = user.build();
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }

        public void setMentions(HashSet<String> mentions) {
            this.mentions = mentions;
        }

        public void setHtags(HashSet<String> htags) {
            this.htags = htags;
        }

        public void setLinks(HashSet<String> links) {
            this.links = links;
        }
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

    @Override
    public String toString() {
        return "Tweet{" +
                "user=" + user +
                ", text='" + text + '\'' +
                ", lang='" + lang + '\'' +
                ", createdAt=" + createdAt +
                ", mentions=" + mentions +
                ", htags=" + htags +
                ", links=" + links +
                '}';
    }
}
