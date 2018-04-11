import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * EncoderDictionary maintains MAX_ENCODER_DICTIONARY_LEN bytes in memory and computes all the possible combination
 * of bytes for legths ranging from MIN_ENCODING_LEN to MAX_ENCODING_LEN. Every time a byte is added it will index all combinations
 * so they can be checked later on in constant access time.
 */
public class EncoderDictionary {

    HashMap<String, ArrayList<Integer>> sequenceIndices;
    private StringBuilder pastBytes;
    private int absoluteIndex = 0;
    private int addOffset = 0;
    private int removeOffset = 0;

    public EncoderDictionary() {
        sequenceIndices = new HashMap<String, ArrayList<Integer>>();
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
            int start = size() - CompressorUtils.MIN_ENCODING_LEN;
            String k = pastBytes.substring(start);
            if(sequenceIndices.containsKey(k)) {
                sequenceIndices.get(k).add(addOffset);
            } else {
                ArrayList<Integer> indices = new ArrayList();
                indices.add(addOffset);
                sequenceIndices.put(k, indices);
            }
            addOffset = ++addOffset % CompressorUtils.MAX_ADDRESS_LEN;
        }
        clearIndex();
    }

    /**
     * When memory is a constrain clearIndex will remove past unused sequences in the dictionary to maintain only the ones needed
     * This operation becomes expensive as the dictionary size increases as it will iterate once through every removed character
     */
    protected void clearIndex() {
        while(size() >  CompressorUtils.MAX_ADDRESS_LEN) {
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
            String k = pastBytes.substring(0, CompressorUtils.MIN_ENCODING_LEN);
            if(sequenceIndices.containsKey(k) && sequenceIndices.get(k).size() > 0) {
                sequenceIndices.get(k).remove(0);
            } else if(sequenceIndices.containsKey(k) && sequenceIndices.get(k).isEmpty()) {
                sequenceIndices.remove(k);
            }
        }
        pastBytes.deleteCharAt(0);
        removeOffset = ++removeOffset % CompressorUtils.MAX_ADDRESS_LEN;
    }

    public boolean contains(String s) {
        return sequenceIndices.containsKey(s);
    }

    /**
     * indexOf will caclulate the index of a repetition in the dictionary if there is any.
     * The operation happens in constant access time as it uses cached indices to calculate the repetition index
     * @param s sequence to find the index of
     * @return the index of the sequence if found, -1 otherwise
     */
    public Pair<Integer, String> indexOf(String s) {
        Pair<Integer, String> indexPair = new Pair(-1, new String());
        if(s.length() < CompressorUtils.MIN_ENCODING_LEN ) return indexPair;
        String k = s.substring(0, CompressorUtils.MIN_ENCODING_LEN);
        if(!sequenceIndices.containsKey(k)) return indexPair;
        ArrayList<Integer> allRepetitions = sequenceIndices.get(k);
        for(int repStart : allRepetitions) {
            Pair<Integer, String> newPair = getIndexOf(repStart, s);
            if(newPair.getValue().length() > indexPair.getValue().length()) {
                indexPair = newPair;
            }
        }
        return indexPair;
    }

    protected Pair getIndexOf(int start, String s) {
        int index = start >= removeOffset ? start - removeOffset : pastBytes.length() - removeOffset;
        Pair<Integer, String> indexPair = new Pair(index, s.substring(0, CompressorUtils.MIN_ENCODING_LEN));
        for(int i = CompressorUtils.MIN_ENCODING_LEN; i < s.length(); i++) {
            if(pastBytes.length() <= (i + index) || s.charAt(i) != pastBytes.charAt(i + index)) {
                return indexPair;
            } else {
                indexPair = new Pair(index, s.substring(0, i + 1));
            }
        }
        return  indexPair;
    }

    public int size() {
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
