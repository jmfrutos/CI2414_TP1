package index;

/**
 * Created by CAndres on 5/15/2016.
 */
public class Posting implements Comparable<Posting> {

    private long documentId;
    private int occurence;
    private String term;

    public double getWtf() {
        return wtf;
    }

    public void setWtf(double wtf) {
        this.wtf = wtf;
    }

    private double wtf;

    public Posting(String term, long documentId, int occurence) {
        super();
        this.documentId = documentId;
        this.occurence = occurence;
        this.term = term;
        this.wtf = 0;
    }
    public Posting(String term, long documentId, double wtf) {
        super();
        this.documentId = documentId;
        this.occurence = 0;
        this.term = term;
        this.wtf = wtf;
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


    public static Posting fromString(String term, String docId, int occurence) {
        return new Posting(term, Long.valueOf(docId), occurence);
    }

    public static Posting fromString(String term, String docId, double valor) {
        return new Posting(term, Long.valueOf(docId), valor);
    }

    public String toString() {
        return Long.toUnsignedString(documentId) + ":" + occurence;
    }

    public String toString2() {
        return Long.toUnsignedString(documentId) + ":" + wtf;
    }

    @Override
    public int compareTo(Posting p) {
        return new Integer((int) (long)(this.getDocumentId())).compareTo((int) (long)p.getDocumentId());
    }

    public String getTerm() {
        return term;
    }
}
