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
            int start = internalSize() - 3;
            while (start >= 0 && internalSize() - start <= 66) {
                cache.put(pastBytes.substring(start), absoluteIndex);
                start--;
            }
            absoluteIndex++;
        }
        if(internalSize() > 65536 * 4) { //20MB max we could increase it to make it faster (clears cache)
            String k = keySets.remove(0);
            while(cache.get(k) < (absoluteIndex - internalSize())) {
                cache.remove(k);
                k = keySets.remove(0);
            }
            int n = internalSize() - 65536;
            removeFirstFromIndex(n);
        }
    }

    public void addToIndex(char[] bytes) {
        for(char b: bytes) {
            addToIndex(b);
        }
    }

    public void removeFirstFromIndex() {
        if(pastBytes.length() == 0) return;
//        if(pastBytes.length() >= 3) {
//            int end = 3;
//            while (end <= 66 && end <= size()) {
//                cache.remove(pastBytes.substring(0, end));
//                end++;
//            }
//        }
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
        return cache.containsKey(s) && cache.get(s) >= absoluteIndex - internalSize() ;
    }

    public int indexOf(String s) {
        return contains(s) ? pastBytes.indexOf(s) : -1;
    }

    public int size() {
        return Math.min(pastBytes.length(), 65536);
    }

    public int internalSize() {
        return pastBytes.length();
    }

    public String getPastBytes() {
        return pastBytes.toString();
    }


}
