package com.nickolasfisher.iotuning;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

// 1
public class BufferingBenchmarkTests {


    @Test
    public void launchBenchmark() throws Exception {
        Utils.runBenchmark(this.getClass());
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void noBuffering() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(Utils.smallCsvFilePath)) {
            int count = countNewLinesUsingStream(fileInputStream);
            assertEquals(Utils.numberOfNewLines_inSmallCsv, count);
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
        try (FileInputStream fileInputStream = new FileInputStream(Utils.smallCsvFilePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            int count = countNewLinesUsingStream(bufferedInputStream);
            assertEquals(Utils.numberOfNewLines_inSmallCsv, count);
        }
    }

    @Benchmark
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void manualBufferingInCode() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(Utils.smallCsvFilePath)) {
            int count = countNewLinesManually(fileInputStream, 8192);
            assertEquals(Utils.numberOfNewLines_inSmallCsv, count);
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
        int lengthOfFile = (int)(new File(Utils.smallCsvFilePath).length());
        try (FileInputStream fileInputStream = new FileInputStream(Utils.smallCsvFilePath)) {
            int count = countNewLinesManually(fileInputStream,lengthOfFile);
            assertEquals(Utils.numberOfNewLines_inSmallCsv, count);
        }
    }


}
