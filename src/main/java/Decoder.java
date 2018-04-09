import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Decoder will use a DecoderDictionary to maintain the past MAX_ADDRESS_LEN bytes in memory
 * and will use the BlockReader to get 9 or 23 bits blocks from file and decompress the file
 */
public class Decoder {

    protected final FileOutputStream fos;
    protected BlockReader fis;
    protected DecoderDictionary dic;

    public Decoder(String source, String target) throws IOException {
        fis = new BlockReader(source);
        fos = new FileOutputStream(target);
        dic = new DecoderDictionary();
    }


    /**
     * Reads encoded blocks and decodes them. If a block is single it writes the byte, if
     * the block is encoded it gets the byte block form the dictionary and writes them to file
     * @return
     * @throws IOException
     */
    public boolean decode() throws IOException {
        int b = 0;
        while((b = fis.next()) != -1) {
            if(isEncodedBlock(b)) {
                int distance = b >>> CompressorUtils.ENCODE_AMMOUNT_BIT_LEN & CompressorUtils.FIRST_16_BITS_ON;
                int amount = (b & CompressorUtils.FIRST_6_BITS_ON) + CompressorUtils.MIN_ENCODING_LEN; //adding offset
                int blockStart = dic.size() - (distance + 1);//adding offset
                int blockEnd = blockStart + amount;
                char[] block = dic.substring(blockStart, blockEnd).toCharArray();
                for (int i = blockStart; i < blockEnd; i++) {
                    char data = dic.charAt(i);
                    writeByte((byte) data);
                    dic.addToIndex(data);
                }
            } else {
                fos.write((byte) b);
                dic.addToIndex((char) b);
            }
            if(dic.size() > CompressorUtils.MAX_ADDRESS_LEN) {
                dic.removeFirstFromIndex(dic.size() - CompressorUtils.MAX_ADDRESS_LEN);
            }
        }
        return true;
    }

    protected void writeByte(byte b) throws IOException {
        fos.write(b);
    }

    protected boolean isEncodedBlock(int b) {
        return b >>> (CompressorUtils.ENCODED_BLOCK_BIT_LEN - 1) == 1;
    }

}
