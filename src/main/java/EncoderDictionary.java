import java.util.HashMap;

/**
 * EncoderDictionary maintains MAX_ENCODER_DICTIONARY_LEN bytes in memory and computes all the possible combination
 * of bytes for legths ranging from MIN_ENCODING_LEN to MAX_ENCODING_LEN. Every time a byte is added it will index all combinations
 * so they can be checked later on in constant access time.
 */
public class EncoderDictionary {

    HashMap<Integer, String> sequenceByLastCharAbsoluteIndex;
    HashMap<Integer, IndexNode> lastIndexByFirstIndexOfSequence;
    HashMap<String, IndexNode> lastCharAbsoluteIndexBySequence;
    private StringBuilder pastBytes;
    private int absoluteIndex = 0;
    private int deleteIndex = 0;

    public EncoderDictionary() {
        sequenceByLastCharAbsoluteIndex = new HashMap<Integer, String>();
        lastIndexByFirstIndexOfSequence = new HashMap<Integer, IndexNode>();
        lastCharAbsoluteIndexBySequence = new HashMap<String, IndexNode>();
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
            String sequence = pastBytes.substring(internalSize() - Math.min(size(), CompressorUtils.MAX_ENCODING_LEN));
            IndexNode absoluteIndexNode = new IndexNode(absoluteIndex);
            if(!lastCharAbsoluteIndexBySequence.containsKey(sequence)) {
                while (start >= offset && internalSize() - start <= CompressorUtils.MAX_ENCODING_LEN) {
                    String k = pastBytes.substring(start);
                    lastCharAbsoluteIndexBySequence.put(k, absoluteIndexNode);
                    start--;
                }
                sequenceByLastCharAbsoluteIndex.put(absoluteIndexNode.getIndex(), sequence);
            } else {
                lastIndexByFirstIndexOfSequence.put(lastCharAbsoluteIndexBySequence.get(sequence).getIndex(), absoluteIndexNode);
            }
        }
        absoluteIndex++;
//        clearIndex();
    }

    /**
     * When memory is a constrain clearIndex will remove past unused sequences in the dictionary to maintain only the ones needed
     * This operation becomes expensive as the dictionary size increases as it will iterate once through every removed character
     */
    protected void clearIndex() {
        int diff = absoluteIndex - deleteIndex >= 0 ? absoluteIndex - deleteIndex : Integer.MAX_VALUE - deleteIndex + absoluteIndex;
        while(diff >  CompressorUtils.MAX_ENCODER_DICTIONARY_LEN) {
            IndexNode deleteIndexNode = new IndexNode(deleteIndex);
            String sequenceToSwitch = sequenceByLastCharAbsoluteIndex.get(deleteIndexNode.getIndex());
            IndexNode firstIndexOfSeq = lastCharAbsoluteIndexBySequence.get(sequenceToSwitch);
            if(firstIndexOfSeq != null && lastIndexByFirstIndexOfSequence.containsKey(firstIndexOfSeq.getIndex())) {
                int newFirstIndex = lastIndexByFirstIndexOfSequence.get(firstIndexOfSeq.getIndex()).getIndex();
                lastIndexByFirstIndexOfSequence.remove(firstIndexOfSeq.getIndex());
                firstIndexOfSeq.setIndex(newFirstIndex);
            } else {
                lastCharAbsoluteIndexBySequence.remove(sequenceToSwitch);
            }
            pastBytes.deleteCharAt(0);
            sequenceByLastCharAbsoluteIndex.remove(deleteIndexNode.getIndex());
            deleteIndex++;
            diff = absoluteIndex - deleteIndex >= 0 ? absoluteIndex - deleteIndex : Integer.MAX_VALUE - deleteIndex + absoluteIndex;
        }
    }

    public void addToIndex(char[] bytes) {
        for(char b: bytes) {
            addToIndex(b);
        }
    }

    public void removeFirstFromIndex() {
        if(pastBytes.length() == 0) return;
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
        return indexOf(s) >= 0;
    }

    /**
     * indexOf will caclulate the index of a repetition in the dictionary if there is any.
     * The operation happens in constant access time as it uses cached indices to calculate the repetition index
     * @param s sequence to find the index of
     * @return the index of the sequence if found, -1 otherwise
     */
    public int indexOf(String s) {
        if(!lastCharAbsoluteIndexBySequence.containsKey(s)) return -1;
        int lastByteOfFirstRep = lastCharAbsoluteIndexBySequence.get(s).getIndex();
        int firstIndexOf = lastCharAbsoluteIndexBySequence.get(s).getIndex() - s.length() + 1;
        int lastIndexOf = -1;
        if(lastIndexByFirstIndexOfSequence.containsKey(lastByteOfFirstRep)) {
            lastIndexOf = lastIndexByFirstIndexOfSequence.get(lastByteOfFirstRep).getIndex() - s.length() + 1;
        }
        int indexOf = Math.max(firstIndexOf, lastIndexOf) - (internalSize() - size());
        return indexOf >= 0 ? indexOf : -1;
    }

    public int size() {
        return Math.min(pastBytes.length(), CompressorUtils.MAX_ADDRESS_LEN);
    }

    protected int internalSize() {
        return pastBytes.length();
    }

    /**
     * IndexNode is a helper class used in the dictionary to be store in the mapped value. The class will only contain an index.
     * This is used to be able to change all indices already mapped to a repetition at once
     * instead of iterating through all the repetitions for a single character.
     * This class is useful in the clearIndex method for switching the old reference
     * of an index without iterating through all the combinations
     */
    protected class IndexNode {
        protected int index;
        public IndexNode(int index) {
            this.index = index;
        }

        public int getIndex() {
            return  index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof IndexNode)) return false;
            return ((IndexNode) o).getIndex() == this.getIndex();
        }
    }
}
