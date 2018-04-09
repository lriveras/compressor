import java.io.FileInputStream;
import java.io.IOException;

/**
 * BlockReader allows reading contiguous encoding blocks (9 or 23 bits)
 * from a file until there are no more bits to read.
 */
public class BlockReader {

    protected FileInputStream fis;

    protected int len;

    protected int data;

    public BlockReader(String file) throws IOException {
        fis = new FileInputStream(file);
        len = CompressorUtils.BYTE_LEN;
        data = fis.read();
    }

    /**
     * The next will return the next encoded block a 23 bits block for encoded bytes and 9 bits block for single byte
     * @return the next encoded block on file, -1 if nothing else is available
     * @throws IOException
     */
    public int next() throws IOException {
        if(data == -1) return -1;
        int type = data >>> len - 1;
        int size = getNextBlockSize();
        readBlock(size);
        int next = -1;
        if(len <= size) {
            //reached to the end of the file
            if(len == size) {
                next = data;
            }
            data = -1;
            len = 0;
        } else {
            next = data >>> len - size;
            data ^= (next << len - size);
            len -= size;
        }
        return next;
    }

    public void close() throws IOException {
        fis.close();
    }

    /**
     * Calculates the next block size based on the first bit of the block
     * @return
     */
    protected int getNextBlockSize() {
        int type = data >>> len - 1;
        int size = 0;
        if(type == CompressorUtils.SINGLE_BLOCK_TYPE) {
            size = CompressorUtils.SINGLE_BLOCK_BIT_LEN;
        } else if(type == CompressorUtils.ENCODED_BLOCK_TYPE) {
            size = CompressorUtils.ENCODED_BLOCK_BIT_LEN;
        }
        return size;
    }

    /**
     * Reads from file until the buffer has one bit more than size
     * @param size of block to read
     * @throws IOException
     */
    protected void readBlock(int size) throws IOException {
        int b = 0;
        while(len <= size && (b = fis.read()) != -1) {
            data = data << CompressorUtils.BYTE_LEN;
            data |= b;
            len += CompressorUtils.BYTE_LEN;
        }
    }
}
