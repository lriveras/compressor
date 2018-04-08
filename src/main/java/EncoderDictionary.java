import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class EncoderDictionary {

    //will contain up to 5MB 65K * 66 B
    HashMap<Integer, Integer> lastIndexOfSequence;

    //will contain up to 5MB 65K * 66 B
    HashMap<String, Integer> cache;
    //will contain up to 65KB of previous data (k=word v=bool)
    StringBuilder pastBytes;
    //global position
    int absoluteIndex = 0;
    ArrayBlockingQueue<String> keySets;

    public EncoderDictionary() {
        lastIndexOfSequence = new HashMap<Integer, Integer>();
        cache = new HashMap<String, Integer>();
        pastBytes = new StringBuilder();
        keySets = new ArrayBlockingQueue<String>(65536 * 2 * 66);
    }

    public void addToIndex(char c) {
        pastBytes.append(c);
        if(size() >= 3) {
            int offset = (internalSize() - size());
            int start = internalSize() - 3;
            String sequence = pastBytes.substring(internalSize() - Math.min(size(), 66));
            if(!cache.containsKey(sequence)) {
                while (start >= offset && internalSize() - start <= 66) {
                    String k = pastBytes.substring(start);
                    cache.put(k, absoluteIndex);
//                keySets.add(k);
                    start--;
                }
            } else {
                lastIndexOfSequence.put(cache.get(sequence), absoluteIndex);
            }
        }
        absoluteIndex++;
//        if(internalSize() > 65536 * 2) { //20MB max we could increase it to make it faster (clears cache)
//            String k = keySets.remove();
//            while(cache.get(k) < (absoluteIndex - size())) {
//                cache.remove(k);
//                k = keySets.remove();
//            }
//            int n = internalSize() - 65536;
//            removeFirstFromIndex(n);
//        }
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
        return indexOf(s) >= 0;// && cache.get(s) >= absoluteIndex - size();
    }

    public int indexOf(String s) {
        if(!cache.containsKey(s)) return -1;
        int lastByteOfFirstRep = cache.get(s);
        int firstIndexOf = cache.get(s) - s.length() + 1;
        int lastIndexOf = -1;
        if(lastIndexOfSequence.containsKey(lastByteOfFirstRep)) {
            lastIndexOf = lastIndexOfSequence.get(lastByteOfFirstRep) - s.length() + 1;
        }
        int indexOf = Math.max(firstIndexOf, lastIndexOf) - (internalSize() - size());
        return indexOf >= 0 ? indexOf : -1;
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
