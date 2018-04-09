import java.io.FileOutputStream;
import java.io.IOException;

public class Decoder {

    protected final FileOutputStream fos;
    protected BlockReader fis;
    protected DecoderDictionary dic;

    public Decoder(String source, String target) throws IOException {
        fis = new BlockReader(source);
        fos = new FileOutputStream(target);
        dic = new DecoderDictionary();
    }


    public boolean decode() throws IOException {
        int b = 0;
        while((b = fis.next()) != -1) {
            if(isEncodedBlock(b)) {
                int distance = b >>> CompressorUtils.ENCODE_AMMOUNT_BIT_LEN & CompressorUtils.FIRST_16_BITS_ON;
                int amount = (b & CompressorUtils.FIRST_6_BITS_ON) + CompressorUtils.MIN_ENCODING_LEN; //adding offset
                int blockStart = dic.size() - (distance + 1);//adding offset
                int blockEnd = blockStart + amount;
                char[] block = dic.substring(blockStart, blockEnd).toCharArray();
                writeBlock(block);
                dic.addAllToIndex(block);
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

    protected void writeBlock(char[] block) throws IOException {
        for(char c : block) {
            fos.write((byte) c);
        }
    }

    protected boolean isEncodedBlock(int b) {
        return b >>> (CompressorUtils.ENCODED_BLOCK_BIT_LEN - 1) == 1;
    }

}
