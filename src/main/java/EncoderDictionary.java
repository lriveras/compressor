import java.util.ArrayList;
import java.util.HashMap;

/**
 * EncoderDictionary maintains MAX_ENCODER_DICTIONARY_LEN bytes in memory and hashes combination
 * of bytes for length MIN_ENCODING_LEN. Every time a byte is added it will index its minimum repetition
 * so they can be checked later on.
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
     * Adds byte to the dictionary with length MIN_ENCODING_LEN
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
     * Removes last bytes until size is lest than MAX_ADDRESS_LEN in the dictionary to maintain only the ones needed
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


    /**
     * Removes past unused sequences in the dictionary to maintain only the ones needed
     */
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
     * @param s sequence to find the index of
     * @return a pair with index of the largest repeated sequence as key and of the sequence if found, a pair with -1 and null otherwise
     */
    public int indexOf(String s) {
        if(s.length() < CompressorUtils.MIN_ENCODING_LEN ) return -1;
        String k = s.substring(0, CompressorUtils.MIN_ENCODING_LEN);
        if(!sequenceIndices.containsKey(k)) return -1;
        ArrayList<Integer> allRepetitions = sequenceIndices.get(k);
        for(int repStart : allRepetitions) {
            int index = getIndexOf(repStart, s);
            if(index >= 0) {
                return index;
            }
        }
        return -1;
    }

    /**
     * For a given sequence of bytes it will find the index of the repetition if it exists
     * @param start the index of the repetition with its offset
     * @param s the bytes to find
     * @return the index of the repetition
     */
    protected int getIndexOf(int start, String s) {
        int index = start >= removeOffset ? start - removeOffset : pastBytes.length() - removeOffset;
        for(int i = 0; i < s.length(); i++) {
            if(pastBytes.length() == (i + index) || s.charAt(i) != pastBytes.charAt(i + index)) {
                return -1;
            }
        }
        return  index;
    }

    public int size() {
        return pastBytes.length();
    }
}
