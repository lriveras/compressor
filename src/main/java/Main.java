import java.io.IOException;

public class Main {

    /**
     * @param args -action -source -output
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length == 0) return;
        String command = args[0].substring(1);
        if(command.equalsIgnoreCase("h") || command.equalsIgnoreCase("help")) {
            System.out.print("Usage: -action [compress, decompress] -source -output");
            return;
        }
        if(args.length < 3) {
            System.out.print("Usage: -action [compress, decompress] -source -output");
            return;
        }
        String source = args[1].substring(1);
        String target = args[2].substring(1);
        if(command.equalsIgnoreCase("compress")) {
            Encoder e = new Encoder(source, target);
            e.encode();
            return;
        }
        if(command.equalsIgnoreCase("decompress")) {
            Decoder d = new Decoder(source, target);
            d.decode();
            return;
        }
        System.out.print("Usage: -action [compress, decompress] -source -output");
        return;
    }
}


