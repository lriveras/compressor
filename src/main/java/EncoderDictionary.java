import java.util.HashMap;
import java.util.LinkedList;

public class EncoderDictionary {

    //will contain up to 5MB 65K * 66 B
    HashMap<String, Integer> cache;
    //will contain up to 65KB of previous data (k=word v=bool)
    StringBuilder pastBytes;
    //global position
    int absoluteIndex = 0;
    LinkedList<String> keySets;

    public EncoderDictionary() {
        cache = new HashMap<String, Integer>();
        pastBytes = new StringBuilder();
        keySets = new LinkedList<String>();
    }

    public void addToIndex(char c) {
        pastBytes.append(c);
        if(pastBytes.length() >= 3) {
            int start = size() - 3;
            while (start >= 0 && size() - start <= 66) {
                cache.put(pastBytes.substring(start), absoluteIndex);
                start--;
            }
            absoluteIndex++;
        }
    }

    public void addToIndex(char[] bytes) {
        for(char b: bytes) {
            addToIndex(b);
        }
    }

    public void removeFirstFromIndex() {
        if(pastBytes.length() == 0) return;
        if(pastBytes.length() >= 3) {
            int end = 3;
            while (end <= 66 && end <= size()) {
                cache.remove(pastBytes.substring(0, end));
                end++;
            }
        }
        pastBytes.deleteCharAt(0);
    }

    public void removeFirstFromIndex(int n) {
        int toRemove = n;
        while(toRemove > 0) {
            removeFirstFromIndex();
            toRemove--;
        }
    }

    public boolean contains(String s) {
        return cache.containsKey(s) && cache.get(s) >= absoluteIndex - size() ;
    }

    public int indexOf(String s) {
        return pastBytes.indexOf(s);
    }

    public int size() {
        return pastBytes.length();
    }

    public String getPastBytes() {
        return pastBytes.toString();
    }


}
