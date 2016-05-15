package index;

/**
 * Created by CAndres on 5/15/2016.
 */
public class Posting {
    private long docId;
    private int frecuency;
    private String term;

    public Posting(String term, long documentId, int occurence) {
        this.docId = documentId;
        this.frecuency = occurence;
        this.term = term;
    }

    public long getDocId() {
        return docId;
    }

    public void setDocId(long documentId) {
        this.docId = documentId;
    }

    public int getFrecuency() {
        return frecuency;
    }

    public void setFrecuency(int frecuency) {
        this.frecuency = frecuency;
    }

    public String getTerm() {
        return term;
    }

    public String toString() {
        return Long.toString(getDocId(), Character.MAX_RADIX);
    }
}
