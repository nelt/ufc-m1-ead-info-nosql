package org.codingmatters.ufc.ead.m1.nosql.data.sample.log;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;
import static org.junit.Assert.*;

/**
 * Created by vagrant on 2/23/16.
 */
@Ignore
public class HadoopLogSampleInjectorTest {

    private File f;

    @Test
    public void testUpload() throws Exception {
        this.upload(this.f, "/user/vagrant/" + this.f.getName());
    }

    @Before
    public void setUp() throws Exception {
        this.f = File.createTempFile("toto", ".txt");
        this.writeSomeData(f);
    }

    @After
    public void tearDown() throws Exception {
        this.f.delete();
    }

    private void writeSomeData(File f) throws Exception {
        try(FileWriter writer = new FileWriter(f)) {
            for(int i = 0 ; i < 10000 ; i++) {
                writer.write("all work and no play makes jack a dull boy\n");
            }
            writer.flush();
        }
    }


    private void upload(File source, String destinationPath) throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", this.getHdfsUrl());
        conf.set("hadoop.job.ugi", "root");

        System.out.println("preparing uri...");
        URI nodeUri = new URI(this.getHdfsUrl());
        System.out.println("uri=" + nodeUri);
        System.out.println("creating client...");
        DFSClient client = new DFSClient(nodeUri, conf);
        System.out.println("client ready");
        try {
            try (
                    InputStream in = new BufferedInputStream(new FileInputStream(source));
                    OutputStream out = new BufferedOutputStream(client.create(destinationPath, true))
            ) {
                System.out.println("starting upload to HDFS of " + source.getAbsolutePath());
                byte[] buffer = new byte[1024];
                for (int len = in.read(buffer); len != -1; len = in.read(buffer)) {
                    out.write(buffer, 0, len);
                }
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            client.close();
        }
    }

    private String getHdfsUrl() {
        return String.format("hdfs://%s:9000/user/vagrant", resolver().resolve("hadoop"));
    }

}