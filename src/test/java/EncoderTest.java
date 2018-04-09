import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class EncoderTest {

    @Test
    public void encodeTest() throws IOException {
        String path = new File(".").getCanonicalPath();

        Encoder d = new Encoder(path + TestUtils.DECODED_BENCHMARK, path + TestUtils.ENCODED_OUTPUT);
        d.encode();

        File output = new File(path + TestUtils.ENCODED_OUTPUT);
        File benchmark = new File(path + TestUtils.ENCODED_BENCHMARK);
        boolean isTwoEqual = FileUtils.contentEquals(output, benchmark);
        Assert.assertTrue(isTwoEqual);

    }
}
