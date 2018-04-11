import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Encoder uses EncoderDictionary to maintain the past MAX_ADDRESS_LEN bytes and all their
 * combinations with length ranging from MIN_ENCODING_LEN to MAX_ENCODING_LEN
 * while encoding if a combination of bytes is found in the dictionary it is encoded using the 23 bits encoding format
 * 1 bit type - ENCODE_ADDRESS_BIT_LEN for address - ENCODE_AMMOUNT_BIT_LEN for the ammout of bytes encoded
 */
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

    /**
     * Maintains the buffer filled with MAX_ENCODING_LEN of characters to encode
     * @return true if loaded bytes from file, false otherwise
     * @throws IOException
     */
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

    /**
     * @param n ammout of bytes to remove
     */
    public void removeFirstFromBuffer(int n) {
        buffer.delete(0, n);
    }

    /**
     * If no repetitions for at least 3 bytes are found it writes a single byte block, otherwise it writes an encoded block
     * @throws IOException
     */
    protected void encodeFile() throws IOException {
        boolean loaded = false;
        while((loaded = loadBuffer())|| buffer.length() > 0) {
            Pair<Integer, String> encode = getEncodingLocation();
            if(encode.getKey() == -1) {
                encodeSingleByteFromBuffer(buffer.charAt(0));
                dic.addToIndex(buffer.charAt(0));
                removeFirstFromBuffer(1);
            } else {
                String rep = encode.getValue();
                int loc = dic.size() - encode.getKey();
                encodeByteBlockFromBuffer(loc, rep.length());
                dic.addToIndex(buffer.substring(0, encode.getValue().length()).toCharArray());
                removeFirstFromBuffer(encode.getValue().length());
            }
        }
        return;
    }

    /**
     * Determines the ammount of bytes to include in the current block being encoded by looking at repetitions in the dictionary from
     * 66 bytes to 3 bytes if no repetition is found it will return length 1
     * @return ammount of bytes to encode
     */
    protected Pair<Integer, String> getEncodingLocation() {
        int end = Math.min(buffer.length(), CompressorUtils.MAX_ENCODING_LEN);
        return dic.indexOf(buffer.substring(0, end));
    }
}
