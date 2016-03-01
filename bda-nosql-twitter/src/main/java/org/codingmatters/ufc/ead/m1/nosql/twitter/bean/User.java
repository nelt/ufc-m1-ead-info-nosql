package org.codingmatters.ufc.ead.m1.nosql.twitter.bean;

/**
 * Created by ubuntu on 09/01/16.
 */
public class User {
    
    static public class Builder {
        private String name;
        private int followersCount = 0;

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withFollowersCount(int followersCount) {
            this.followersCount = followersCount;
            return this;
        }

        public User build() {
            return new User(this.name, this.followersCount);
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setFollowersCount(int followersCount) {
            this.followersCount = followersCount;
        }
    }

    private final String name;
    private final int followersCount;

    public User(String name, int followersCount) {
        this.name = name;
        this.followersCount = followersCount;
    }

    public String getName() {
        return name;
    }

    public int getFollowersCount() {
        return followersCount;
    }
}
