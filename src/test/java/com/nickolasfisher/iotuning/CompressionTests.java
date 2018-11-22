package com.nickolasfisher.iotuning;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

// 5
public class CompressionTests {

    private static String compressedLargeCsvFile = Utils.pathToResources + "simple-large-compressed-csv.zip";
    private static String millionLineCsvFilePath = Utils.pathToResources + "million-lines.csv";

    public static int NUMBER_OF_CSV_LINES = 1000000;

    @Test
    public void launchBenchmark() throws Exception {
        Utils.runBenchmark(this.getClass());
    }


    @Benchmark
    public void readAndWriteWithoutCompression() throws Exception {
        writeUncompressedFileToDisk(Utils.getCsv(NUMBER_OF_CSV_LINES), millionLineCsvFilePath);
        String readValues = readUncompressedFileFromDisk(millionLineCsvFilePath);
        assertTrue(readValues.startsWith("0,1,2,3,4,5"));
    }

    private void writeUncompressedFileToDisk(String data, String fileOutPutPath) throws Exception {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOutPutPath)) {
            fileOutputStream.write(data.getBytes());
        }
    }

    private String readUncompressedFileFromDisk(String filePath) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            int length = (int) new File(filePath).length();
            byte[] bytes = new byte[length];
            fileInputStream.read(bytes);
            return new String(bytes);
        }
    }

    @Benchmark
    public void readAndWriteCompressedData() throws Exception {
        compressAndWriteFile(Utils.getCsv(NUMBER_OF_CSV_LINES), compressedLargeCsvFile);
        String dataFromCompressedFile = readCompressedFile(compressedLargeCsvFile);
        assertTrue(dataFromCompressedFile.startsWith("0,1,2,3,4,5"));
    }

    private void compressAndWriteFile(String data, String fileOutputPath) throws Exception {
        try (FileOutputStream fileOutputStream = new FileOutputStream(fileOutputPath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream)) {
            ZipEntry zipEntry = new ZipEntry(fileOutputPath);
            zipOutputStream.putNextEntry(zipEntry);
            zipOutputStream.write(data.getBytes());
        }
    }

    private String readCompressedFile(String path) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(path);
             ZipInputStream zipInputStream = new ZipInputStream(fileInputStream)) {
            zipInputStream.getNextEntry();
            int BUFF_SIZE = (int) new File(path).length();
            byte[] buffered = new byte[BUFF_SIZE];
            zipInputStream.read(buffered);
            return new String(buffered);
        }

    }
}
