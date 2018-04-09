import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class DecoderTest {

    @Test
    public void decodeTest() throws IOException {
        String path = new File(".").getCanonicalPath();

        Decoder d = new Decoder(path + TestUtils.ENCODED_BENCHMARK, path + TestUtils.DECODED_OUTPUT);
        d.decode();


        File output = new File(path + TestUtils.DECODED_OUTPUT);
        File benchmark = new File(path + TestUtils.DECODED_BENCHMARK);
        boolean isTwoEqual = FileUtils.contentEquals(output, benchmark);
        Assert.assertTrue(isTwoEqual);

    }
}
