import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class DecoderTest {

    @Test
    public void decodeTest() throws IOException {
        Decoder d = new Decoder(TestUtils.ENCODED_BENCHMARK,
                TestUtils.DECODED_OUTPUT);
        d.decode();


        File output = new File(TestUtils.DECODED_OUTPUT);
        File benchmark = new File(TestUtils.DECODED_BENCHMARK);
        boolean isTwoEqual = FileUtils.contentEquals(output, benchmark);
        Assert.assertTrue(isTwoEqual);

    }
}
