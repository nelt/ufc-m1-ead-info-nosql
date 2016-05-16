package org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter.InjectionException;
import org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter.TweetInjector;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers.dateFromLocalDateTime;

/**
 * Created by vagrant on 26/04/16.
 */
public class CassandraTweetInjector implements TweetInjector {

    private final Session session;
    private final PreparedStatement tweetStmt;
    private final PreparedStatement userlineStmt;
    private final PreparedStatement htaglineStmt;

    public CassandraTweetInjector(Session session) {
        this.session = session;
        this.tweetStmt = this.session.prepare("INSERT INTO ufcead.tweets (tweetid, username, text, createdAt) VALUES (?,?,?,?)");
        this.userlineStmt = this.session.prepare("INSERT INTO ufcead.user_timeline (username, tweetid, createdAt) VALUES (?,?,?)");
        this.htaglineStmt = this.session.prepare("INSERT INTO ufcead. htag_timeline (htag, tweetid, createdAt) VALUES (?,?,?)");
    }

    @Override
    public String inject(Tweet tweet) throws InjectionException {
        String tweetId = tweet.getUser().getName() + "-" + tweet.getCreatedAt().getTime();

        this.session.execute(new BoundStatement(this.tweetStmt).bind(
            tweetId, tweet.getUser().getName(), tweet.getText(), tweet.getCreatedAt()
        ));
        this.session.execute(new BoundStatement(this.userlineStmt).bind(
            tweet.getUser().getName(), tweetId, tweet.getCreatedAt()
        ));
        for (String htag : tweet.getHtags()) {
            this.session.execute(new BoundStatement(this.htaglineStmt).bind(
                    htag, tweetId, tweet.getCreatedAt()
            ));
        }

        return tweetId;
    }
}
