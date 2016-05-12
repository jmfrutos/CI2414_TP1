package analysis;


import document.Document;

/**
 * Created by CAndres on 5/11/2016.
 */
public abstract class Analyzer {
    public Analyzer() {
    }

    public abstract void analyze(Document doc);
}
