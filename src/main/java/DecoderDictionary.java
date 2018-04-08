
public class DecoderDictionary {

    protected StringBuilder pastBytes;

    public DecoderDictionary() {
        pastBytes = new StringBuilder();
    }

    public void addToIndex(char c) {
        pastBytes.append(c);
    }

    public void addAllToIndex(char[] c) {
        pastBytes.append(c);
    }

    public void removeFirstFromIndex(int n) {
        pastBytes.delete(0, n);
    }

    public String substring(int start, int end) {
        return pastBytes.substring(start, end);
    }

    public int size() {
        return pastBytes.length();
    }
}
