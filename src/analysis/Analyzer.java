package analysis;


import document.Document;
import index.IndexWriterConfig;

/**
 * Created by CAndres on 5/11/2016.
 */
public abstract class Analyzer {
    IndexWriterConfig config;

    public Analyzer() {
    }

    public abstract void analyze(Document doc);
}
