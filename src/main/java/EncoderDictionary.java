import java.util.HashMap;

public class EncoderDictionary {

    //will contain up to 5MB 65K * 66 B
    HashMap<Integer, String> sequenceByAbsoluteIndex;

    //will contain up to 5MB 65K * 66 B
    HashMap<Integer, IndexNode> lastIndexOfSequence;

    //will contain up to 5MB 65K * 66 B
    HashMap<String, IndexNode> cache;
    //will contain up to 65KB of previous data (k=word v=bool)
    StringBuilder pastBytes;
    //global position
    int absoluteIndex = 0;
    int deleteIndex = 0;

    public EncoderDictionary() {
        sequenceByAbsoluteIndex = new HashMap<Integer, String>();
        lastIndexOfSequence = new HashMap<Integer, IndexNode>();
        cache = new HashMap<String, IndexNode>();
        pastBytes = new StringBuilder();
    }

    public void addToIndex(char c) {
        pastBytes.append(c);
        if(size() >= 3) {
            int offset = (internalSize() - size());
            int start = internalSize() - 3;
            String sequence = pastBytes.substring(internalSize() - Math.min(size(), 66));
            IndexNode absoluteIndexNode = new IndexNode(absoluteIndex);
            if(!cache.containsKey(sequence)) {
                while (start >= offset && internalSize() - start <= 66) {
                    String k = pastBytes.substring(start);
                    cache.put(k, absoluteIndexNode);
                    start--;
                }
                sequenceByAbsoluteIndex.put(absoluteIndexNode.getIndex(), sequence);
            } else {
                lastIndexOfSequence.put(cache.get(sequence).getIndex(), absoluteIndexNode);
            }
        }
        absoluteIndex++;
        clearIndex();
    }

    protected void clearIndex() {
        int deleted = 0;
        while(absoluteIndex - deleteIndex >  65536 * 2) {//65536 * 1000000000) {
            IndexNode deleteIndexNode = new IndexNode(deleteIndex);
            String sequenceToSwitch = sequenceByAbsoluteIndex.get(deleteIndexNode);
            IndexNode firstIndexOfSeq = cache.get(sequenceToSwitch);
            if(lastIndexOfSequence.containsKey(firstIndexOfSeq)) {
                int newFirstIndex = lastIndexOfSequence.get(firstIndexOfSeq).getIndex();
                lastIndexOfSequence.remove(firstIndexOfSeq);
                firstIndexOfSeq.setIndex(newFirstIndex);
            } else {
                cache.remove(sequenceToSwitch);
            }
            pastBytes.deleteCharAt(0);
            sequenceByAbsoluteIndex.remove(deleteIndexNode);
            deleteIndex++;
            deleted++;
        }
        if(pastBytes.length() == Integer.MAX_VALUE - 1) {
            reduceAbsoluteIndex(deleteIndex);
        }
    }

    protected void reduceAbsoluteIndex(int n) {
        for(String k : cache.keySet()) {
            int reducedIndex = cache.get(k).getIndex() - n;
            cache.get(k).setIndex(reducedIndex);
        }

        HashMap<Integer, IndexNode> reducedLastIndexOfSequence = new HashMap();
        for(int k : lastIndexOfSequence.keySet()) {
            int reducedKeyIndex = k - n;
            int reducedValueIndex = lastIndexOfSequence.get(k).getIndex() - n;
            reducedLastIndexOfSequence.put(reducedKeyIndex, new IndexNode(reducedValueIndex));
        }
        lastIndexOfSequence = reducedLastIndexOfSequence;

        HashMap<Integer, String> reducedSequenceByAbsoluteIndex = new HashMap();
        for(int k : sequenceByAbsoluteIndex.keySet()) {
            int reducedKeyIndex = k - n;
            reducedSequenceByAbsoluteIndex.put(reducedKeyIndex, sequenceByAbsoluteIndex.get(k));
        }
        sequenceByAbsoluteIndex = reducedSequenceByAbsoluteIndex;

        absoluteIndex -= n;
        deleteIndex -= n;
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
        int lastByteOfFirstRep = cache.get(s).getIndex();
        int firstIndexOf = cache.get(s).getIndex() - s.length() + 1;
        int lastIndexOf = -1;
        if(lastIndexOfSequence.containsKey(lastByteOfFirstRep)) {
            lastIndexOf = lastIndexOfSequence.get(lastByteOfFirstRep).getIndex() - s.length() + 1;
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
