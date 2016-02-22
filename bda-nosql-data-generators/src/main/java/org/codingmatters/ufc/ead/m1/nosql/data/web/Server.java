package org.codingmatters.ufc.ead.m1.nosql.data.web;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.core.RiakCluster;
import com.datastax.driver.core.Cluster;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.codingmatters.ufc.ead.m1.nosql.data.service.sensor.CassandraSensorDataService;
import org.codingmatters.ufc.ead.m1.nosql.data.service.sensor.RiakSensorDataService;
import org.codingmatters.ufc.ead.m1.nosql.data.service.sensor.SensorDataService;
import org.codingmatters.ufc.ead.m1.nosql.data.service.exception.ServiceException;
import org.codingmatters.ufc.ead.m1.nosql.data.utils.Helpers;
import spark.Request;
import spark.Response;

import java.io.File;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.staticFileLocation;

/**
 * Created by vagrant on 2/18/16.
 */
public class Server {



    public static void main(String[] args) {
        Server server = new Server();
        try {
            server.run();
            while(true) {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    System.out.println("stopping server...");
                }
            }
        } finally {
            server.stop();
            System.out.println("stopped.");
        }
    }

    private final ObjectMapper mapper = Helpers.configureForDates(new ObjectMapper());

    private RiakCluster cluster;
    private RiakSensorDataService riakSensorDataService;

    private Cluster cassandraCluster;
    private CassandraSensorDataService cassandraSensorDataService;

    public Server() {
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

    public void stop() {
        this.cluster.shutdown();
    }

    public void run() {
        if(System.getProperty("dev.mode") != null) {
            System.out.println("running in dev mode");
            externalStaticFileLocation(new File("./src/main/resources/www").getAbsolutePath());
        } else {
            staticFileLocation("www");
        }
        get("/riak/sensor/weekly/data/:sensor/:year/:week", (request, response) -> this.serveWeeklySensorData(request, response, this.getRiakService()));
        get("/cassandra/sensor/weekly/data/:sensor/:year/:week", (request, response) -> this.serveWeeklySensorData(request, response, this.getCassandraService()));
    }


    private Object serveWeeklySensorData(Request request, Response response, SensorDataService withService) throws JsonProcessingException, ServiceException {
        String sensor = request.params(":sensor");
        int year = Integer.parseInt(request.params(":year"));
        int week = Integer.parseInt(request.params(":week"));

        System.out.println("requested for weekly sensor data for " + sensor + " " + year + "/" + week);

        response.type("application/json");
        return this.mapper.writeValueAsString(withService.weekData(sensor, year, week));
    }
}
