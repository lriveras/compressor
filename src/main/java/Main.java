import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

public class Main {

    public static void main(String[] args) throws IOException {

        long s = System.currentTimeMillis();
        long beforeUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        Encoder e = new Encoder("/Users/luke/IdeaProjects/compressor/src/main/resources/mytest.txt",
                "/Users/luke/IdeaProjects/compressor/src/main/resources/encode.txt");
        e.encode();
        Decoder d = new Decoder("/Users/luke/IdeaProjects/compressor/src/main/resources/encode.txt",
                "/Users/luke/IdeaProjects/compressor/src/main/resources/output.txt");
        d.decode();
        long end = System.currentTimeMillis();
        long afterUsedMem=Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory();

        System.out.println((end-s)/1000);
        System.out.println((afterUsedMem - beforeUsedMem)/1000000 + "MB");

    }
}


