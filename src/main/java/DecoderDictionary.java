/**
 * DecoderDictionary maintains bytes in memory for them to be accessed later in time
 */
public class DecoderDictionary {

    protected StringBuilder pastBytes;

    public DecoderDictionary() {
        pastBytes = new StringBuilder();
    }

    public void addToIndex(char c) {
        pastBytes.append(c);
    }

    public void removeFirstFromIndex(int n) {
        pastBytes.delete(0, n);
    }

    public String substring(int start, int end) {
        return pastBytes.substring(start, end);
    }

    public char charAt(int index) {
        return pastBytes.charAt(index);
    }

    public int size() {
        return pastBytes.length();
    }
}
