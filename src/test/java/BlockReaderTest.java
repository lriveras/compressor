import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BlockReaderTest {

    int SINGLE_BLOCK = 0xC6;
    int ENCODED_BLOCK = 0x656365;

    @Test
    public void readEncodedBlockTest() throws IOException {
        String path = new File(".").getCanonicalPath();

        BlockReader r = new BlockReader(path + TestUtils.ENCODED_BLOCK_BENCHMARK);
        int encoded = r.next();
        Assert.assertEquals(encoded, SINGLE_BLOCK);
        encoded = r.next();
        Assert.assertEquals(encoded, ENCODED_BLOCK);
    }

}
