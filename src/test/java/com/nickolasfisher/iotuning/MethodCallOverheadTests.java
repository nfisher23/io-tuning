package com.nickolasfisher.iotuning;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.*;

import static org.junit.Assert.assertEquals;

// 2
public class MethodCallOverheadTests {

    @Test
    public void launchBenchmark() throws Exception {
        Utils.runBenchmark(this.getClass());
    }

    @Benchmark
    public void readEachCharacterUnderTheHood() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(Utils.smallCsvFilePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             DataInputStream dataInputStream = new DataInputStream(bufferedInputStream)) {
            int count = 0;
            while (dataInputStream.readLine() != null) {
                count++;
            }

            assertEquals(Utils.numberOfNewLines_inSmallCsv, count);
        }
    }

    @Benchmark
    public void faster_usingBufferedReader() throws Exception {
        try (FileReader fileReader = new FileReader(Utils.smallCsvFilePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            int count = 0;
            while (bufferedReader.readLine() != null) {
                count++;
            }

            assertEquals(Utils.numberOfNewLines_inSmallCsv, count);
        }
    }
}
