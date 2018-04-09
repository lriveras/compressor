import java.io.FileInputStream;
import java.io.IOException;

/**
 * BlockReader allows reading consiguous encoding blocks (9 or 23 bits)
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

    public int next() throws IOException {
        if(data == -1) return -1;
        int type = data >>> len - 1;
        int size = getNextBlockSize();
        readBlock(size);
        int next = -1;
        if(len <= size) {
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

    protected int getNextBlockSize() {
        int type = data >>> len - 1;
        int size = 0;
        if(type == 0) {
            size = CompressorUtils.SINGLE_BLOCK_BIT_LEN;
        } else if(type == 1) {
            size = CompressorUtils.ENCODED_BLOCK_BIT_LEN;
        }
        return size;
    }

    protected void readBlock(int size) throws IOException {
        int b = 0;
        while(len <= size && (b = fis.read()) != -1) {
            data = data << CompressorUtils.BYTE_LEN;
            data |= b;
            len += CompressorUtils.BYTE_LEN;
        }
    }
}
