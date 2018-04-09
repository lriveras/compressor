import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * BlockWriter allows writing blocks of 9 and 23 blocks to a file by maintaining
 * the bits in memory until there are enough bits to be written in byte chunks.
 */
public class BlockWriter {

    protected FileOutputStream fos;
    protected int data;
    protected int len;

    public BlockWriter(String file) throws FileNotFoundException {
        fos = new FileOutputStream(file);
        len = 0;
        data = 0;
    }

    public void writeSingleByteBlock(int b) throws IOException {
        data = data << CompressorUtils.SINGLE_BLOCK_BIT_LEN;
        data |= b & CompressorUtils.FIRST_9_BITS_ON;
        len += CompressorUtils.SINGLE_BLOCK_BIT_LEN;
        while(len >= CompressorUtils.BYTE_LEN) {
            writeByte();
        }
    }

    public void writeMultipleBlock(int b) throws IOException {
        data = data << CompressorUtils.ENCODED_BLOCK_BIT_LEN;
        data |= b & CompressorUtils.FIRST_23_BITS_ON;
        len += CompressorUtils.ENCODED_BLOCK_BIT_LEN;
        while(len >= CompressorUtils.BYTE_LEN) {
            writeByte();
        }
    }

    public void close() throws IOException {
        if(len > 0) {
            writeByte();
        }
        fos.flush();
        fos.close();
    }

    protected void writeByte() throws IOException {
        if(len >= CompressorUtils.BYTE_LEN) {
            int block = data >>> len - CompressorUtils.BYTE_LEN;
            fos.write((byte) block);
            data = data ^ (block << len - CompressorUtils.BYTE_LEN);
            len -= CompressorUtils.BYTE_LEN;
        } else if (len > 0) {
            fos.write((byte) data << CompressorUtils.BYTE_LEN - len);
            len = 0;
            data = 0;
        }
    }

}
