import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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
        data = data << 9;
        data |= b & 0x01FF;
        len += 9;
        while(len >= 8) {
            writeByte();
        }
    }

    public void writeMultipleBlock(int b) throws IOException {
        data = data << 23;
        data |= b & 0x7FFFFF;
        len += 23;
        while(len >= 8) {
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
        if(len >= 8) {
            int block = data >>> len - 8;
            fos.write((byte) block);
            data = data ^ (block << len - 8);
            len -= 8;
        } else if (len > 0) {
            fos.write((byte) data << 8 - len);
            len = 0;
            data = 0;
        }
    }

}
