package com.nickolasfisher.iotuning;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.RandomAccessFile;

import static org.junit.Assert.assertEquals;

// 4
public class RandomAccessTests {

    public static int INTERVAL = 20000;

    @Test
    public void runBenchmark() throws Exception {
        Utils.runBenchmark(this.getClass());
    }

    @Benchmark
    public void scanningThroughWithBufferedInputStream() throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(Utils.largeCsvFilePath);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)) {
            for (int i = 0; i < 10; i++) {
                int readVal = bufferedInputStream.read();
                long totalSkipped = 0;
                totalSkipped = bufferedInputStream.skip(INTERVAL - 1);
                while (totalSkipped != INTERVAL - 1) {
                    totalSkipped += bufferedInputStream.skip(INTERVAL - totalSkipped - 1);
                }

                assertEquals('0', readVal);
            }
        }
    }

    @Benchmark
    public void seekingToPosition() throws Exception {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(Utils.largeCsvFilePath, "r")) {
            for (int i = 0; i < 10; i++) {
                randomAccessFile.seek(INTERVAL);
                int readValue = randomAccessFile.read();
                assertEquals('0', readValue);
            }
        }
    }
}
