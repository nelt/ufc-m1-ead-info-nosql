package org.codingmatters.ufc.ead.m1.nosql.data.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by vagrant on 2/18/16.
 */
public class HostResolver {

    static synchronized public HostResolver resolver() {
        if(singleton == null) {
            try {
                singleton = new HostResolver();
            } catch (IOException e) {
                throw new RuntimeException("failed to load host resolver", e);
            }
        }
        return singleton;
    }

    static private HostResolver singleton;

    private final Properties hosts;

    public HostResolver() throws IOException {
        this.hosts = new Properties();
        try(InputStream resource = Thread.currentThread().getContextClassLoader().getResourceAsStream("hosts.properties")) {
            this.hosts.load(resource);
        }

        File file = new File(System.getProperty("host.resolver.file", "./hosts.properties"));
        if(file.exists()) {
            try(FileInputStream stream = new FileInputStream(file)) {
                this.hosts.load(stream);
            }
        }
    }

    public String resolve(String host) {
        return this.hosts.getProperty(host, host);
    }

}
