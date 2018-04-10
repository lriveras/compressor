import java.util.HashMap;

/**
 * EncoderDictionary maintains MAX_ENCODER_DICTIONARY_LEN bytes in memory and computes all the possible combination
 * of bytes for legths ranging from MIN_ENCODING_LEN to MAX_ENCODING_LEN. Every time a byte is added it will index all combinations
 * so they can be checked later on in constant access time.
 */
public class EncoderDictionary {

    HashMap<String, Integer> lastCharAbsoluteIndexBySequence;
    private StringBuilder pastBytes;
    private int absoluteIndex = 0;
    private int deleteIndex = 0;

    public EncoderDictionary() {
        lastCharAbsoluteIndexBySequence = new HashMap<String, Integer>();
        pastBytes = new StringBuilder();
    }

    /**
     * Adds byte to the dictionary and computes all combinations of characters from MIN_ENCODING_LEN to MAX_ENCODING_LEN
     * if a sequence of character has already been indexed, it will store a reference to the first occurrence index
     * to avoid recalculation and thus enhancing performance
     * @param c byte to add
     */
    public void addToIndex(char c) {
        pastBytes.append(c);
        if(size() >= CompressorUtils.MIN_ENCODING_LEN) {
            int offset = (internalSize() - size());
            int start = internalSize() - CompressorUtils.MIN_ENCODING_LEN;
            while (start >= offset && internalSize() - start <= CompressorUtils.MAX_ENCODING_LEN) {
                String k = pastBytes.substring(start);
                lastCharAbsoluteIndexBySequence.put(k, absoluteIndex);
                start--;
            }
        }
        absoluteIndex++;
        clearIndex();
    }

    /**
     * When memory is a constrain clearIndex will remove past unused sequences in the dictionary to maintain only the ones needed
     * This operation becomes expensive as the dictionary size increases as it will iterate once through every removed character
     */
    protected void clearIndex() {
        int diff = internalSize() - size();
        while(diff >  CompressorUtils.MAX_ENCODER_DICTIONARY_LEN) {
            removeFirstFromIndex();
        }
    }

    public void addToIndex(char[] bytes) {
        for(char b: bytes) {
            addToIndex(b);
        }
    }

    public void removeFirstFromIndex() {
        if(pastBytes.length() == 0) return;
        if(pastBytes.length() >= CompressorUtils.MIN_ENCODING_LEN) {
            int end = CompressorUtils.MAX_ENCODING_LEN;
            while (end >= CompressorUtils.MIN_ENCODING_LEN && end <= size()) {
                String k = pastBytes.substring(0, end);
                lastCharAbsoluteIndexBySequence.remove(k);
                end--;
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
        return lastCharAbsoluteIndexBySequence.containsKey(s) && lastCharAbsoluteIndexBySequence.get(s) >= absoluteIndex - size();
    }

    /**
     * indexOf will caclulate the index of a repetition in the dictionary if there is any.
     * The operation happens in constant access time as it uses cached indices to calculate the repetition index
     * @param s sequence to find the index of
     * @return the index of the sequence if found, -1 otherwise
     */
    public int indexOf(String s) {
        return contains(s) ? pastBytes.lastIndexOf(s) - (internalSize() - size()) : -1;
    }

    public int size() {
        return Math.min(pastBytes.length(), CompressorUtils.MAX_ADDRESS_LEN);
    }

    public int internalSize() {
        return pastBytes.length();
    }

    /**
     * IndexNode is a helper class used in the dictionary to be stored in the mapped value. The class will only contain an index.
     * This is used to be able to change all indices already mapped to a repetition at once
     * instead of iterating through all the repetitions for a single character.
     * This class is useful in the clearIndex method for switching the old reference
     * of an index without iterating through all the combinations
     */
//    protected class IndexNode {
//        protected int index;
//        public IndexNode(int index) {
//            this.index = index;
//        }
//
//        public int getIndex() {
//            return  index;
//        }
//
//        public void setIndex(int index) {
//            this.index = index;
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if(!(o instanceof IndexNode)) return false;
//            return ((IndexNode) o).getIndex() == this.getIndex();
//        }
//    }
}
