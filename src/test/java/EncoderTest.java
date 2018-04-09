import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class EncoderTest {

    @Test
    public void encodeTest() throws IOException {
        Encoder d = new Encoder(TestUtils.DECODED_BENCHMARK,
                TestUtils.ENCODED_OUTPUT);
        d.encode();


        File output = new File(TestUtils.ENCODED_OUTPUT);
        File benchmark = new File(TestUtils.ENCODED_BENCHMARK);
        boolean isTwoEqual = FileUtils.contentEquals(output, benchmark);
        Assert.assertTrue(isTwoEqual);

    }
}
