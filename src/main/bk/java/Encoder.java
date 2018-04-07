import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Encoder {

    StringBuilder buffer;//always loaded with 66 B or less if remaining bytes are less than 66
    EncoderDictionary dic;//always loaded with the past 65KB  and all its combinations up to 5MB
    FileInputStream fis;
    BlockWriter fos;

    public Encoder(String source, String target) throws FileNotFoundException {
        buffer = new StringBuilder();
        dic = new EncoderDictionary();
        fis = new FileInputStream(source);
        fos = new BlockWriter(target);
    }

    public boolean loadBuffer() throws IOException {
        int b = 0;
        boolean loaded = false;
        while(buffer.length() <= 66 && (b = fis.read()) != -1) {
            buffer.append((char) b);
            loaded = true;
        }
        return loaded;
    }

    public void encodeSingleByteFromBuffer(int b) throws IOException {
        fos.writeSingleByteBlock(b);
    }

    public void encodeByteBlockFromBuffer(int loc, int ammount) throws IOException {
        int block = 1 << 16;
        block |= loc - 1;//substracting offset
        block = block << 6;
        block |= ammount - 3;//minus offset
        fos.writeMultipleBlock(block);
    }

    public boolean encode() throws IOException {
        boolean encoded = false;
        try {
            encodeFile();
            encoded = true;
        } catch (Exception e) {
            //log exception
        } finally {
            if(fos!=null)
                fos.close();
            if(fis!=null)
                fis.close();
        }
        return encoded;
    }

    private boolean encodeFile() throws IOException {
        //load file
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
//            if(dic.size() > 65536) {
//                dic.removeFirstFromIndex(dic.size() - 65536);
//            }
            removeFirstFromBuffer(encodeEnd);
        }
        return false;//false if failed
    }

    private int getEncodingLength() {
        int encodeEnd = 1;
        for(int i = buffer.length(); i >= 3; i--) {
            String currBytes = buffer.substring(0, i);
            if(dic.contains(currBytes)) {
                encodeEnd = i;
                break;
            }
        }
        return encodeEnd;
    }

    public void removeFirstFromBuffer(int n) {
        buffer.delete(0, n);
    }
}
