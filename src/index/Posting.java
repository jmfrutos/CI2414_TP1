package index;

/**
 * Created by CAndres on 5/15/2016.
 */
public class Posting implements Comparable<Posting> {

    private long documentId;
    private int occurence;
    private String term;

    public Posting(String term, long documentId, int occurence) {
        super();
        this.documentId = documentId;
        this.occurence = occurence;
        this.term = term;

    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(long documentId) {
        this.documentId = documentId;
    }

    public int getOccurence() {
        return occurence;
    }

    public void setOccurence(int occurence) {
        this.occurence = occurence;
    }

    public void add(int add) {
        occurence += add;
    }

    @Override
    public int hashCode() {
        return (int) (long) getDocumentId();
    }
    @Override
    public boolean equals(Object o) {
        if ((o instanceof Posting) == false)
            return false;
        Posting otherPosting = (Posting) o;
        if (otherPosting.getDocumentId() == getDocumentId())
            return true;
        else
            return false;
    }


    public static Posting fromString(String term, String docId) {
        return new Posting(term, Long.valueOf(docId), 1);
    }

    public String toString() {
        return Long.toUnsignedString(documentId);
    }

    @Override
    public int compareTo(Posting p) {
        return new Integer((int) (long)(this.getDocumentId())).compareTo((int) (long)p.getDocumentId());
    }

    public String getTerm() {
        return term;
    }
}
