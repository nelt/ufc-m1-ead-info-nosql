package org.codingmatters.ufc.ead.m1.nosql.data.web;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.datastax.driver.core.Cluster;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import org.codingmatters.ufc.ead.m1.nosql.data.web.service.sensor.CassandraSensorDataService;
import org.codingmatters.ufc.ead.m1.nosql.data.web.service.sensor.RiakSensorDataService;
import org.codingmatters.ufc.ead.m1.nosql.data.web.service.sensor.SensorDataService;
import org.codingmatters.ufc.ead.m1.nosql.data.web.service.exception.ServiceException;
import org.codingmatters.ufc.ead.m1.nosql.data.web.service.tweet.MongoTweetSearchService;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;
import org.codingmatters.ufc.ead.m1.nosql.data.web.service.tweet.ESTweetSearchService;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

/**
 * Created by vagrant on 2/18/16.
 */
public class Server {

    private static final Logger log = LoggerFactory.getLogger(Server.class);

    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.run();
            while(true) {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    log.info("stopping server...");
                }
            }
        } finally {
            server.stop();
            log.info("stopped.");
        }
    }

    private final ObjectMapper mapper = Helpers.configureForDates(new ObjectMapper());

    private RiakCluster cluster;
    private RiakSensorDataService riakSensorDataService;

    private Cluster cassandraCluster;
    private CassandraSensorDataService cassandraSensorDataService;

    private ESTweetSearchService ESTweetSearchService;

    private MongoClient mongoClient;
    private MongoTweetSearchService mongoTweetSearchService;

    public Server() {
    }


    public void stop() {
        this.cluster.shutdown();
    }

    public void run() {
        if(System.getProperty("dev.mode") != null) {
            log.info("running in dev mode");
            externalStaticFileLocation(new File("./src/main/resources/www").getAbsolutePath());
        } else {
            staticFileLocation("www");
        }
        get("/riak/sensor/weekly/data/:sensor/:year/:week", (request, response) -> this.serveWeeklySensorData(request, response, this.getRiakService()));
        get("/cassandra/sensor/weekly/data/:sensor/:year/:week", (request, response) -> this.serveWeeklySensorData(request, response, this.getCassandraService()));

        get("/elasticsearch/search/tweets", (request, response) -> this.getESTweetSearchService().process(request, response));

        get("/mongo/search/tweets", (request, response) -> this.getTweetMongoService().process(request, response));
    }


    private Object serveWeeklySensorData(Request request, Response response, SensorDataService withService) throws JsonProcessingException, ServiceException {
        String sensor = request.params(":sensor");
        int year = Integer.parseInt(request.params(":year"));
        int week = Integer.parseInt(request.params(":week"));

        log.info("requested for weekly sensor data for " + sensor + " " + year + "/" + week);

        response.type("application/json");
        return this.mapper.writeValueAsString(withService.weekData(sensor, year, week));
    }



    private synchronized SensorDataService getRiakService() {
        if(this.riakSensorDataService == null) {
            this.cluster = Helpers.createRiakCluster();
            this.riakSensorDataService = new RiakSensorDataService(new RiakClient(this.cluster), this.mapper);
        }
        return this.riakSensorDataService;
    }

    private synchronized SensorDataService getCassandraService() {
        if(this.cassandraSensorDataService == null) {
            this.cassandraCluster = Cluster.builder().addContactPoint(resolver().resolve("cassandra")).build();
            this.cassandraSensorDataService = new CassandraSensorDataService(this.cassandraCluster.connect());
        }
        return this.cassandraSensorDataService;
    }

    private synchronized ESTweetSearchService getESTweetSearchService() {
        if(this.ESTweetSearchService == null) {
            Client client = null;
            try {
                InetAddress host = InetAddress.getByName(resolver().resolve("elastic"));
                client = TransportClient.builder().build()
                        .addTransportAddress(new InetSocketTransportAddress(host, 9300));

                if(! client.admin().indices().exists(new IndicesExistsRequest("twitter")).actionGet().isExists()) {
                    client.admin().indices().create(new CreateIndexRequest("twitter")).actionGet();
                }
            } catch (UnknownHostException e) {
                throw new RuntimeException("error creating elastic search client", e);
            }
            this.ESTweetSearchService = new ESTweetSearchService(client);
        }
        return this.ESTweetSearchService;
    }



    private synchronized MongoTweetSearchService getTweetMongoService() {
        if(this.mongoTweetSearchService == null) {
            this.mongoClient = new MongoClient(resolver().resolve("mongo"), 27017);
            this.mongoTweetSearchService = new MongoTweetSearchService(mongoClient.getDatabase("twitter"));
        }
        return this.mongoTweetSearchService;
    }
}
