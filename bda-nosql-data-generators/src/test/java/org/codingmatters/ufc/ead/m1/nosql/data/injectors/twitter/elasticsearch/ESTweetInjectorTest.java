package org.codingmatters.ufc.ead.m1.nosql.data.injectors.twitter.elasticsearch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.twitter.bean.Tweet;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Map;
import java.util.UUID;

import static org.elasticsearch.common.settings.Settings.settingsBuilder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by vagrant on 2/24/16.
 */
public class ESTweetInjectorTest {

    private Node node;
    private Client client;
    private ObjectMapper mapper;
    private File home;

    @Before
    public void setUp() throws Exception {
        String clusterName = UUID.randomUUID().toString();
        this.home = new File(System.getProperty("java.io.tmpdir"), clusterName);
        this.home.mkdir();
        this.node = NodeBuilder.nodeBuilder()
                .local(true)
                .settings(settingsBuilder()
                        .put("cluster.name", clusterName)
                        .put("path.home", this.home.getAbsolutePath())
                )
                .node();
        this.client = node.client();
        this.mapper = new ObjectMapper();

        this.client.admin().indices().create(new CreateIndexRequest("twitter")).actionGet();
    }

    @Test
    public void testInject() throws Exception {
        Tweet tweet = new Tweet.Builder().build();

        String id = new ESTweetInjector(this.client, this.mapper).inject(tweet);
        System.out.println(id);
        Map<String, Object> indexed = this.client.prepareGet("twitter", "tweet", id).get().getSourceAsMap();
        assertThat(indexed.get("text"), is(tweet.getText()));
    }

    @Test
    public void testSearch() throws Exception {
        Tweet tweet = new Tweet.Builder().build();
        String id = new ESTweetInjector(this.client, this.mapper).inject(tweet);

        SearchResponse response = this.client.prepareSearch("twitter").setQuery(
                QueryBuilders.matchAllQuery()
        ).addAggregation(
                AggregationBuilders.terms("htags").field("htags")
        ).execute().actionGet();

        System.out.println(response.toString());
    }

    @After
    public void tearDown() throws Exception {
        this.node.close();
        this.recursiveDelete(this.home);
    }

    private void recursiveDelete(File file) {
        if(file.isDirectory()) {
            for (File child : file.listFiles()) {
                this.recursiveDelete(child);
            }
        }
        file.delete();
    }
}