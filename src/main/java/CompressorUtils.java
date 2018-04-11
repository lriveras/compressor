/**
 * Contains all the values regarding the compressor
 */
public class CompressorUtils {

    public static int BYTE_LEN = 8;
    public static int MAX_ENCODING_LEN = 66;
    public static int MAX_ADDRESS_LEN = 65536;
    public static int MIN_ENCODING_LEN = 3;

    public static int SINGLE_BLOCK_TYPE = 0;
    public static int ENCODED_BLOCK_TYPE = 1;

    public static int ENCODE_ADDRESS_BIT_LEN = 16;
    public static int ENCODE_AMMOUNT_BIT_LEN = 6;

    public static int SINGLE_BLOCK_BIT_LEN = 9;
    public static int ENCODED_BLOCK_BIT_LEN = 23;

    public static int FIRST_9_BITS_ON = 0x01FF;
    public static int FIRST_23_BITS_ON = 0x7FFFFF;
    public static int FIRST_16_BITS_ON = 0xFFFF;
    public static int FIRST_6_BITS_ON = 0x3F;



}
