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


    public static Posting fromString(String term, String input) {
        String[] parts =input.split(":");
        int documentId = Integer.parseInt(parts[0], Character.MAX_RADIX);
        int occurence = Integer.parseInt(parts[1], Character.MAX_RADIX);
        return new Posting(term, documentId, occurence);
    }

    public String toString() {
        return Long.toString(getDocumentId(), Character.MAX_RADIX);
    }

    @Override
    public int compareTo(Posting p) {
        return new Integer((int) (long)(this.getDocumentId())).compareTo((int) (long)p.getDocumentId());
    }

    public String getTerm() {
        return term;
    }
}