package com.nickolasfisher.iotuning;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;

// 6
public class CachingTests {

    @Test
    public void launch() throws Exception {
        Utils.runBenchmark(this.getClass());
    }

    @Benchmark
    public void loadWholeFileThenScan() throws Exception {
        for (int i = 0; i < 10; i++) {
            List<String> linesInFile = readLinesOfFileFromDisk(Utils.smallCsvFilePath);
            assertLinesCorrect(linesInFile);
        }
    }

    @Benchmark
    public void loadCachedFilesThenScan() throws Exception {
        for (int i = 0; i < 10; i++) {
            List<String> linesInFile = getLinesOfFileCached(Utils.smallCsvFilePath);
            assertLinesCorrect(linesInFile);
        }
    }

    private void assertLinesCorrect(List<String> lines) {
        for (String line : lines) {
            assertTrue(line.startsWith("0,1,2,3,4,5"));
        }
    }

    private static List<String> cachedLines;

    private List<String> getLinesOfFileCached(String filePath) throws Exception {
        if (cachedLines == null) {
            cachedLines = readLinesOfFileFromDisk(filePath);
        }
        return cachedLines;
    }

    private List<String> readLinesOfFileFromDisk(String filePath) throws Exception {
        List<String> listofLines = new ArrayList<>();

        try (FileReader fileReader = new FileReader(filePath);
             BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            listofLines.add(bufferedReader.readLine());
        }

        return listofLines;
    }
}
