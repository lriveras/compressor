import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;

public class Main {

    public static void main(String[] args) throws IOException {

        long s = System.currentTimeMillis();
        Encoder e = new Encoder("/Users/luke/IdeaProjects/compressor/src/main/resources/test-5mb.txt",
                "/Users/luke/IdeaProjects/compressor/src/main/resources/encode.txt");
        e.encode();
        Decoder d = new Decoder("/Users/luke/IdeaProjects/compressor/src/main/resources/encode.txt",
                "/Users/luke/IdeaProjects/compressor/src/main/resources/decode.txt");
        d.decode();
        long end = System.currentTimeMillis();
        System.out.println((end-s)/1000);
//        BlockWriter w = new BlockWriter("/Users/luke/IdeaProjects/compressor/src/main/resources/encode.txt");
//        int mult = 'e';
//        mult = mult << 8 | 'e';
//        mult = mult << 8 | 'e';
//        w.writeMultipleBlock(mult);
//        w.writeSingleByteBlock('e');
//        w.writeSingleByteBlock('e');
//        w.writeMultipleBlock(mult);
//        w.writeSingleByteBlock('e');

//        BlockReader r = new BlockReader("/Users/luke/IdeaProjects/compressor/src/main/resources/encode.txt");
//        System.out.println(r.next());
//        System.out.println(r.next());
//        System.out.println(r.next());
//        System.out.println(r.next());

//        EncoderDictionary dic = new EncoderDictionary();
//        dic.addToIndex('c');
//        System.out.println(dic.getPastBytes());
//        dic.addToIndex('a');
//        System.out.println(dic.getPastBytes());
//        dic.addToIndex('r');
//        System.out.println(dic.getPastBytes());
//        dic.addToIndex('c');
//        System.out.println(dic.getPastBytes());
//        dic.addToIndex('a');
//        System.out.println(dic.getPastBytes());
//        dic.addToIndex('s');
//        System.out.println(dic.getPastBytes());
//        dic.addToIndex('a');
//        System.out.println(dic.getPastBytes());
//        dic.addToIndex('r');
//
//        System.out.println(dic.contains("car"));
//        System.out.println(dic.contains("cara"));
//        System.out.println(dic.contains("carc"));
//        System.out.println(dic.contains("a"));
//        System.out.println(dic.contains("carcasar"));


//        byte b = 0xF;
//        char c = (char) b;
//        byte bc = (byte) c;
//        System.out.println("Hello");
//        System.out.println(b<<1);
//        System.out.println(b);
//        System.out.println(b);
//        System.out.println(c);
//        System.out.println(bc);

//        try{
//            FileInputStream fin=new FileInputStream("/Users/luke/IdeaProjects/compressor/src/main/resources/test.txt");
//            byte i= (byte) fin.read();
//            System.out.print((char)i);
//
//            fin.close();
//        }catch(Exception e){System.out.println(e);}
//    }

//        FileOutputStream fos = null;
//        FileInputStream fis = null;
//        byte b = 66;
//        int i = 0;
//        char c;

//        try {
//
//            // create new file output stream
//            fos = new FileOutputStream("/Users/luke/IdeaProjects/compressor/src/main/resources/encode.txt");
//
//            // writes byte to the output stream
//            fos.write(b);
//
//            // flushes the content to the underlying stream
//            fos.flush();
//        } catch(Exception ex) {
//
//            // if an error occurs
//            ex.printStackTrace();
//        } finally {
//
//            // closes and releases system resources from stream
//            if(fos!=null)
//                fos.close();
//            if(fis!=null)
//                fis.close();
//        }

}
}
