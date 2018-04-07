import java.io.FileOutputStream;
import java.io.IOException;

public class Decoder {

    private final FileOutputStream fos;
    private BlockReader fis;
    private DecoderDictionary dic;

    public Decoder(String source, String target) throws IOException {
        fis = new BlockReader(source);
        fos = new FileOutputStream(target);
        dic = new DecoderDictionary();
    }

    int total = 0;

    public boolean decode() throws IOException {
        int b = 0;
        while((b = fis.next()) != -1) {
            if(isEncodedBlock(b)) {
                int distance = b >>> 6 & 0xFFFF;
                int amount = (b & 0x3F) + 3; //adding offset
                int blockStart = dic.size() - (distance + 1);//adding offset
                int blockEnd = blockStart + amount;
                char[] block = dic.substring(blockStart, blockEnd).toCharArray();
                writeBlock(block);
                dic.addAllToIndex(block);
                total+= amount;
            } else {
                fos.write((byte) b);
                int calc = 1 + dic.size();
                dic.addToIndex((char) b);
                total++;
            }
            if(dic.size() > 65536) {
                dic.removeFirstFromIndex(dic.size() - 65536);
            }
        }
        return true;
    }

    public void writeBlock(char[] block) throws IOException {
        for(char c : block) {
            fos.write((byte) c);
        }
    }

    public boolean isEncodedBlock(int b) {
        return b >>> 22 == 1;
    }

}
