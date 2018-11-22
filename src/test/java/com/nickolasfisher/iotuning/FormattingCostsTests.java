package com.nickolasfisher.iotuning;

import org.junit.Test;
import org.openjdk.jmh.annotations.Benchmark;

import java.text.MessageFormat;

// 3
public class FormattingCostsTests {

    public static int COUNT = 25000;
    public static int NUM = 7;

    @Test
    public void launchBenchmark() throws Exception {
        Utils.runBenchmark(this.getClass());
    }

    @Benchmark
    public void printingWithNoFormattingCosts() {
        for (int i = 0; i < COUNT; i++) {
            System.out.print("The square of 7 is 49\n");
        }
    }

    @Benchmark
    public void formatUsingAddition() {
        for (int i = 0; i < COUNT; i++) {
            String s = "The square of " + NUM + " is " + NUM * NUM + "\n";
            System.out.print(s);
        }
    }

    @Benchmark
    public void formatUsingMessageFormatter_preCompiled() {
        MessageFormat formatter = new MessageFormat("The square of {0} is {1}\n");
        Integer[] values = new Integer[2];
        values[0] = NUM;
        values[1] = NUM * NUM;
        for (int i = 0; i < COUNT; i++) {
            String s = formatter.format(values);
            System.out.print(s);
        }
    }

    @Benchmark
    public void formatWithoutPrecompiling() {
        String format = "The square of {0} is {1}\n";
        Integer[] values = new Integer[2];
        values[0] = NUM;
        values[1] = NUM * NUM;
        for (int i = 0; i < COUNT; i++) {
            String s = MessageFormat.format(format, values);
            System.out.print(s);
        }
    }

}
