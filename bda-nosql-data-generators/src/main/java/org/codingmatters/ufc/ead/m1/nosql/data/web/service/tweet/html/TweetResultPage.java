package org.codingmatters.ufc.ead.m1.nosql.data.web.service.tweet.html;

import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by vagrant on 3/3/16.
 */
public class TweetResultPage {

    private long totalResults;
    private final LinkedList<Tweet> tweets = new LinkedList<>();
    private final LinkedList<HtagFacet> htagFacets = new LinkedList<>();
    private String currentSearch = null;
    private Set<String> currentHtags;
    private long took;

    public String render() {
        String page = "<html>" +
                "<head>" +
                "<title>Tweet search</title>" +
                "<link rel=\"stylesheet\" href=\"/tweet.css\">" +
                "</head>" +
                "<body>\n";

        page += this.renderSearchBox();
        page += this.renderCurrentSelectedHTags();
        page += this.renderHtagSelection();
        page += this.renderSearchStatistics();
        page += this.renderResults();

        return page + "\n</body></html>";
    }

    private String renderSearchBox() {
        String fragment = "<p class=\"tweet-search-box\">" +
                "<form action=\"\">";
        for (String htag : this.currentHtags) {
            fragment += "<input type=\"hidden\" name=\"htags\" value=\"" + htag + "\" />";
        }

        fragment +=
                "Search: <input class=\"tweet-search\" type=\"text\" name=\"search\" value=\"" +
                (this.currentSearch != null ? this.currentSearch : "") +
                "\"/>" +
                "</form>" +
                "</p>";
        return fragment;
    }

    private String renderCurrentSelectedHTags() {
        if(! this.currentHtags.isEmpty()) {
            String fragment = "<p class=\"tweet-current-htag-filter\">Current # filter :\n";
            for (String htag : this.currentHtags) {
                fragment += "<a href=\"" + this.getQueryParamsWithoutHtag(htag) + "\" " +
                        "class=\"tweet-htag-facet\">- " + htag +
                        "</a> \n";
            }
            fragment += "</p>";
            return fragment;
        } else {
            return "";
        }
    }

    private String renderHtagSelection() {
        String fragment = "<p class=\"tweet-htag-facets\">Add # filter:\n";
        for (HtagFacet htagFacet : this.htagFacets) {
            if(! this.currentHtags.contains(htagFacet.getHtag())) {
                fragment += "<a href=\"" + this.getQueryParamsWithHtag(htagFacet.getHtag()) + "\" " +
                        "class=\"tweet-htag-facet\">" +
                        "+ " + htagFacet.getHtag() + " (" + htagFacet.getDocCount() + ")" +
                        "</a> \n";
            }
        }
        fragment += "</p>";
        return fragment;
    }

    private String renderSearchStatistics() {
        String fragment = "<p class=\"search-stats\">" + this.totalResults + " tweets are matching. " + this.tweets.size() + " first displayed.</p>\n";
        fragment += "<p class=\"search-took\">Search took " + this.took + "ms.</p>\n";
        return fragment;
    }

    private String renderResults() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd, HH:mm:ss");

        String fragment = "<p class=\"tweet-results\">" +
                "<ul>\n";
        for (Tweet tweet : this.tweets) {
            fragment += "<li>" +
                    "<p class=\"tweet-text\">" + tweet.getText() + "</p>" +
                    "<p class=\"tweet-user\">" + tweet.getUser().getName() + "</p>" +
                    "<p class=\"tweet-date\">" + dateFormat.format(tweet.getCreatedAt()) + "</p>" +
                    "</li>\n";
        }
        fragment += "</ul>" +
                "</p>\n";
        return fragment;
    }

    private String getQueryParamsWithHtag(String htag) {
        String result = "?";
        if(this.currentSearch != null) {
            result += "search=" + (this.currentSearch != null ? this.currentSearch : "");
        }
        for (String currentHtag : this.currentHtags) {
            result += "&htags=" + currentHtag;
        }
        result += "&htags=" + htag;
        return result;
    }

    private String getQueryParamsWithoutHtag(String htag) {
        String result = "?";
        if(this.currentSearch != null) {
            result += "search=" + (this.currentSearch != null ? this.currentSearch : "");
        }
        for (String currentHtag : this.currentHtags) {
            if(! currentHtag.equals(htag)) {
                result += "&htags=" + currentHtag;
            }
        }
        return result;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }

    public void addTweet(Tweet tweet) {
        this.tweets.add(tweet);
    }

    public void addHtagFacet(String htag, long docCount) {
        this.htagFacets.add(new HtagFacet(htag, docCount));
    }

    public void setCurrentSearch(String currentSearch) {
        this.currentSearch = currentSearch;
    }

    public void setCurrentHtags(String[] currentHtags) {
        if(currentHtags != null) {
            this.currentHtags = new HashSet<>(Arrays.asList(currentHtags));
        } else {
            this.currentHtags = Collections.emptySet();
        }
    }

    public void setTook(long took) {
        this.took = took;
    }
}
