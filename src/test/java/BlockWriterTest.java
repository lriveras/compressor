import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class BlockWriterTest {

    @Test
    public void writeEncodedSequenceTest() throws IOException {
        String path = new File(".").getCanonicalPath();

        BlockWriter w = new BlockWriter(path + TestUtils.ENCODED_BLOCK_OUTPUT);
        w.writeSingleByteBlock(0xC6);
        w.writeMultipleBlock(0x656365);
        w.close();

        File output = new File(path + TestUtils.ENCODED_BLOCK_OUTPUT);
        File benchmark = new File(path +TestUtils.ENCODED_BLOCK_OUTPUT);
        boolean isTwoEqual = FileUtils.contentEquals(output, benchmark);
        Assert.assertTrue(isTwoEqual);
    }
}
