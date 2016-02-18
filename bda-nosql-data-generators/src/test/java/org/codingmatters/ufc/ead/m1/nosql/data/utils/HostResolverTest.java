package org.codingmatters.ufc.ead.m1.nosql.data.utils;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Properties;

import static org.junit.Assert.assertThat;

/**
 * Created by vagrant on 2/18/16.
 */
public class HostResolverTest {

    private File overrideFile;

    @Test
    public void testDefaults() throws Exception {
        assertThat(new HostResolver().resolve("riak"), Matchers.is("192.168.33.100"));
        assertThat(new HostResolver().resolve("cassandra"), Matchers.is("192.168.33.110"));
    }

    @Test
    public void testOverride() throws Exception {
        Properties override = new Properties();
        override.setProperty("riak", "10.0.2.1");
        try(FileOutputStream out = new FileOutputStream(overrideFile)) {
            override.store(out, "");
            out.flush();
        }

        System.setProperty("host.resolver.file", overrideFile.getAbsolutePath());

        assertThat(new HostResolver().resolve("riak"), Matchers.is("10.0.2.1"));
        assertThat(new HostResolver().resolve("cassandra"), Matchers.is("192.168.33.110"));
    }

    @Test
    public void testCurrentDirectoryOverride() throws Exception {
        File file = new File("./hosts.properties");
        file.createNewFile();
        try {
            Properties override = new Properties();
            override.setProperty("riak", "10.0.2.12");
            try (FileOutputStream out = new FileOutputStream(file)) {
                override.store(out, "");
                out.flush();
            }

            assertThat(new HostResolver().resolve("riak"), Matchers.is("10.0.2.12"));
            assertThat(new HostResolver().resolve("cassandra"), Matchers.is("192.168.33.110"));
        } finally {
            file.delete();
        }
    }

    @Before
    public void setUp() throws Exception {
        System.clearProperty("host.resolver.file");
        this.overrideFile = File.createTempFile("host", ".properties");
    }

    @After
    public void tearDown() throws Exception {
        System.clearProperty("host.resolver.file");
        this.overrideFile.delete();
    }
}