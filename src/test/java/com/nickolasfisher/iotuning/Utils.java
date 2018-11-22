package com.nickolasfisher.iotuning;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static final int numberOfNewLines_inSmallCsv = 10000;
    public static final int numberOfNewLines_inLargeCsv = 100000;

    public static String pathToResources = "src/test/resources/";
    public static String smallCsvFilePath = pathToResources + "simple-csv-file.csv";
    public static String largeCsvFilePath = pathToResources + "simple-large-csv-file.csv";

    @BeforeClass
    public static void setupSmallCsv() throws Exception {
        Path csvPathAsPath = Paths.get(Utils.smallCsvFilePath);
        writeCsvFile(Utils.numberOfNewLines_inSmallCsv, csvPathAsPath);
    }

    @BeforeClass
    public static void setupLargeCsv() throws Exception {
        Path pathToLargeCsv = Paths.get(Utils.largeCsvFilePath);
        writeCsvFile(Utils.numberOfNewLines_inLargeCsv, pathToLargeCsv);
    }

    private static void writeCsvFile(int numOfLinesToWrite, Path filePath) throws IOException {
        // run once to create the sample data we need for testing
        String csvDataToWrite = getCsv(numberOfNewLines_inSmallCsv);
        Files.write(filePath, csvDataToWrite.getBytes());
    }

    public static String getCsv(int numberOfLines) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < numberOfLines; i++) {
            for (int j = 0; j < 10; j++) {
                builder.append(Integer.toString(j)).append(",");
            }
            builder.replace(builder.length() - 1, builder.length(), "\n");
        }

        return builder.toString();
    }

    public static void runBenchmark(Class clazz) throws Exception {
        Options options = new OptionsBuilder()
                .include(clazz.getName() + ".*")
                .mode(Mode.AverageTime)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementIterations(2)
                .timeUnit(TimeUnit.MILLISECONDS)
                .measurementTime(TimeValue.seconds(1))
                // OS bottleneck, so we use should one
                // thread at a time for accurate results
                .threads(1)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(options).run();
    }

    @Test
    public void zilch() {}
}
