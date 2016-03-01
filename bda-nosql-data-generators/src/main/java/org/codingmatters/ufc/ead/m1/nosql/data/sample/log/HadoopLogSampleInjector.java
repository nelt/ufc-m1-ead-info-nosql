package org.codingmatters.ufc.ead.m1.nosql.data.sample.log;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSClient;
import org.codingmatters.ufc.ead.m1.nosql.data.generators.log.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static org.codingmatters.ufc.ead.m1.nosql.data.utils.HostResolver.resolver;

/**
 * Created by vagrant on 2/22/16.
 */
public class HadoopLogSampleInjector {

    public static void main(String[] args) {
        int months = 1;
        LocalDateTime start = LocalDateTime.now().minusMonths(months);
        LocalDateTime end = LocalDateTime.now();
        new HadoopLogSampleInjector(start, end,
                LogTemplate.logger("youhou").withLevel(Level.INFO).withFormatter(() -> "blablabla").build()
                ).inject();
    }

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final LogTemplate [] templates;
    private final long fileCount;
    private final LogFormatter formatter = new LogFormatter();

    public HadoopLogSampleInjector(LocalDateTime start, LocalDateTime end, LogTemplate ... templates) {
        this.start = start;
        this.end = end;
        this.templates = templates;

        this.fileCount = Duration.between(start, end).toDays() + 1;
    }

    private void inject() {
        ExecutorService pool = Executors.newFixedThreadPool(1);
        AtomicLong injected = new AtomicLong(0);

        System.out.println("starting injection of log data in " + this.fileCount + " files for time range " + this.start + " to " + this.end);

        for(long fileIndex = 0 ; fileIndex < this.fileCount ; fileIndex++) {
            LocalDateTime fStart = this.start.plusDays(fileIndex);
            LocalDateTime fEnd = fStart.plusDays(1);
            pool.submit(() -> this.generateFile(fStart, fEnd, injected));
            break;
        }

        pool.shutdown();
        while(! pool.isTerminated()) {
            try {
                pool.awaitTermination(10, TimeUnit.SECONDS);
                System.out.println("injected " + injected.get() + " log data...");
            } catch (InterruptedException e) {
                throw new RuntimeException("stopped while waiting injection end", e);
            }
        }
        System.out.println("done injecting " + injected.get() + " log data in " + this.fileCount + " files.");
    }

    private void generateFile(LocalDateTime fileStart, LocalDateTime fileEnd, AtomicLong injected) {
        System.out.println("starting injector for range " + fileStart + " to " + fileEnd);
        File log;
        try {
            log = File.createTempFile(fileStart.toString(), ".log");
        } catch (IOException e) {
            throw new RuntimeException("failed creating temporary log file", e);
        }
        LogDataRandom random = new LogDataRandom.Builder()
                .withMinAt(fileStart)
                .withMessages(this.templates)
                .build();

        try(FileOutputStream out = new FileOutputStream(log)) {
            for (LogData data = random.next(); data.getAt().isBefore(fileEnd); data = random.next()) {
                out.write(this.formatter.format(data).getBytes());
                out.write("\n".getBytes());
                injected.incrementAndGet();
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("failed writing logs to temporary file", e);
        }

        try {
            this.upload(log, "/system-" + fileStart.format(DateTimeFormatter.ISO_LOCAL_DATE) + ".log");
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException("error uploading file " + log.getAbsolutePath(), e);
        } finally {
            log.delete();
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
