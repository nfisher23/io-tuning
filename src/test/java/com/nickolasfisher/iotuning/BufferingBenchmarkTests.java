package com.nickolasfisher.iotuning;

import org.junit.Before;
import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class BufferingBenchmarkTests {

    private static String pathToResources = "src/test/resources";
    private static String csvFilePath = pathToResources + "/simple-csv-file.csv";

    private static final int numberOfNewlines = 10000;

    @Before
    public void setupFile() throws Exception {
        Path csvPathAsPath = Paths.get(csvFilePath);
        // run once to create the sample data we need for testing
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numberOfNewlines; i++) {
            for (int j = 0; j < 10; j++) {
                builder.append(Integer.toString(j)).append(",");
            }
            builder.replace(builder.length() - 1, builder.length(), "\n");
        }

        String csvDataToWrite = builder.toString();
        Files.write(csvPathAsPath, csvDataToWrite.getBytes());
    }

    @Test
    public void launchBenchmark() throws Exception {
        Options options = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.AverageTime)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementIterations(2)
                .measurementTime(TimeValue.seconds(1))
                // since we are doing read input, which is an
                // OS bottleneck, we have to ensure
                // we use one thread at a time
                .threads(1)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(options).run();
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void noBuffering() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(csvFilePath)) {
            int count = countNewLinesUsingStream(fileInputStream);
            assertEquals(numberOfNewlines, count);
        }
    }

    private int countNewLinesUsingStream(InputStream inputStream) throws Exception {
        int count = 0;
        int bytesRead;
        while ((bytesRead = inputStream.read()) != -1) {
            if (bytesRead == '\n') {
                count++;
            }
        }
        return count;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void defaultJavaBuffering() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(csvFilePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            int count = countNewLinesUsingStream(bufferedInputStream);
            assertEquals(numberOfNewlines, count);
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void manualBufferingInCode() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(csvFilePath)) {
            int count = countNewLinesManually(fileInputStream, 8192);
            assertEquals(numberOfNewlines, count);
        }
    }

    private int countNewLinesManually(InputStream inputStream, int customBytesToBuffer) throws Exception {
        byte buff[] = new byte[customBytesToBuffer];
        int count = 0;
        int bytesRead;
        while ((bytesRead = inputStream.read(buff)) != -1) {
            for (int i = 0; i < bytesRead; i++) {
                if (buff[i] == '\n') {
                    count++;
                }
            }
        }
        return count;
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void useFileSizeAsBuffer() throws Exception {
        int lengthOfFile = (int)(new File(csvFilePath).length());
        try (FileInputStream fileInputStream = new FileInputStream(csvFilePath)) {
            int count = countNewLinesManually(fileInputStream,lengthOfFile);
            assertEquals(numberOfNewlines, count);
        }
    }


}
