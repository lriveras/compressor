import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BlockReader {

    FileInputStream fis;

    int len;

    int data;

    public BlockReader(String file) throws IOException {
        fis = new FileInputStream(file);
        len = 8;
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

    private int getNextBlockSize() {
        int type = data >>> len - 1;
        int size = 0;
        if(type == 0) {
            size = 9;
        } else if(type == 1) {
            size = 23;
        }
        return size;
    }

    private void readBlock(int size) throws IOException {
        int b = 0;
        while(len <= size && (b = fis.read()) != -1) {
            data = data << 8;
            data |= b;
            len += 8;
        }
    }

    public void close() throws IOException {
        fis.close();
    }
}
