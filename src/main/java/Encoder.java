import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Encoder {

    StringBuilder buffer;
    EncoderDictionary dic;
    FileInputStream fis;
    BlockWriter fos;

    public Encoder(String source, String target) throws FileNotFoundException {
        buffer = new StringBuilder();
        dic = new EncoderDictionary();
        fis = new FileInputStream(source);
        fos = new BlockWriter(target);
    }

    private boolean loadBuffer() throws IOException {
        int b = 0;
        boolean loaded = false;
        while(buffer.length() <= CompressorUtils.MAX_ENCODING_LEN && (b = fis.read()) != -1) {
            buffer.append((char) b);
            loaded = true;
        }
        return loaded;
    }

    private void encodeSingleByteFromBuffer(int b) throws IOException {
        fos.writeSingleByteBlock(b);
    }

    private void encodeByteBlockFromBuffer(int loc, int ammount) throws IOException {
        int block = 1 << CompressorUtils.ENCODE_ADDRESS_BIT_LEN;
        block |= loc - 1;//subtracting offset
        block = block << CompressorUtils.ENCODE_AMMOUNT_BIT_LEN;
        block |= ammount - CompressorUtils.MIN_ENCODING_LEN;//subtracting offset
        fos.writeMultipleBlock(block);
    }

    public boolean encode() throws IOException {
        boolean encoded = false;
        try {
            encodeFile();
            encoded = true;
        } catch (IOException e) {
            throw e;
        } finally {
            if(fos!=null)
                fos.close();
            if(fis!=null)
                fis.close();
        }
        return encoded;
    }

    public void removeFirstFromBuffer(int n) {
        buffer.delete(0, n);
    }

    protected void encodeFile() throws IOException {
        boolean loaded = false;
        while((loaded = loadBuffer())|| buffer.length() > 0) {
            int encodeEnd = getEncodingLength();
            if(encodeEnd == 1) {
                encodeSingleByteFromBuffer(buffer.charAt(0));
            } else {
                String rep = buffer.substring(0, encodeEnd);
                int loc = dic.size() - dic.indexOf(rep);
                encodeByteBlockFromBuffer(loc, rep.length());
            }
            dic.addToIndex(buffer.substring(0, encodeEnd).toCharArray());
            removeFirstFromBuffer(encodeEnd);
        }
        return;
    }

    protected int getEncodingLength() {
        int encodeEnd = 1;
        for(int i = buffer.length(); i >= CompressorUtils.MIN_ENCODING_LEN; i--) {
            String currBytes = buffer.substring(0, i);
            if(dic.contains(currBytes)) {
                encodeEnd = i;
                break;
            }
        }
        return encodeEnd;
    }
}
