import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PerformanceTest {

    @Test
    public void displayPerformanceAnalysis() throws IOException {
        System.out.println("Encode Performance");
        runEncode("100KB Test", TestUtils.PERFORMANCE_100KB, TestUtils.ENCODE_PERFORMANCE_100KB_OUTPUT);
        runEncode("1MB Test", TestUtils.PERFORMANCE_1MB, TestUtils.ENCODE_PERFORMANCE_1MB_OUTPUT);
        runEncode("5MB Test", TestUtils.PERFORMANCE_5MB, TestUtils.ENCODE_PERFORMANCE_5MB_OUTPUT);
        runEncode("10MB Test", TestUtils.PERFORMANCE_10MB, TestUtils.ENCODE_PERFORMANCE_10MB_OUTPUT);
//        runEncode("100MB Test", TestUtils.PERFORMANCE_100MB, TestUtils.ENCODE_PERFORMANCE_100MB_OUTPUT);
        System.out.println("\n\n");
        System.out.println("Decode Performance");
        runDecode("100KB Test", TestUtils.ENCODE_PERFORMANCE_100KB_OUTPUT, TestUtils.DECODE_PERFORMANCE_100KB_OUTPUT);
        runDecode("1MB Test", TestUtils.ENCODE_PERFORMANCE_1MB_OUTPUT, TestUtils.DECODE_PERFORMANCE_1MB_OUTPUT);
        runDecode("5MB Test", TestUtils.ENCODE_PERFORMANCE_5MB_OUTPUT, TestUtils.DECODE_PERFORMANCE_5MB_OUTPUT);
        runDecode("10MB Test", TestUtils.ENCODE_PERFORMANCE_10MB_OUTPUT, TestUtils.DECODE_PERFORMANCE_10MB_OUTPUT);
//        runDecode("100MB Test", TestUtils.ENCODE_PERFORMANCE_100MB_OUTPUT, TestUtils.DECODE_PERFORMANCE_100MB_OUTPUT);
    }

    public void runEncode(String runText, String source, String target) throws IOException {
        System.gc();
        String path = new File(".").getCanonicalPath();
        long startTime = System.currentTimeMillis();
        long startMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        Encoder e = new Encoder(path + source, path + target);
        e.encode();
        long endTime = System.currentTimeMillis();
        long endMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        System.out.print(runText+ "\t");
        System.out.print((endTime - startTime)/1000 + "s\t");
        System.out.print((endMem - startMem)/1000000 + "MB\n");
        System.gc();
    }

    public void runDecode(String runText, String source, String target) throws IOException {
        System.gc();
        String path = new File(".").getCanonicalPath();

        long startTime = System.currentTimeMillis();
        long startMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        Decoder e = new Decoder(path + source, path + target);
        e.decode();
        long endTime = System.currentTimeMillis();
        long endMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        System.out.print(runText+ "\t");
        System.out.print((endTime - startTime)/1000 + "s\t");
        System.out.print((endMem - startMem)/1000000 + "MB\n");
        System.gc();
    }
}
