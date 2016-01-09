package org.codingmatters.ufc.ead.m1.nosql.twitter.bean;

/**
 * Created by ubuntu on 09/01/16.
 */
public class User {

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
