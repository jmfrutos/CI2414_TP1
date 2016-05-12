package index;

import analysis.Analyzer;

/**
 * Created by CAndres on 5/11/2016.
 */
public class IndexWriterConfig {

    public final Analyzer analyzer;



    public IndexWriterConfig(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /** Returns the default analyzer to use for indexing documents. */
    public Analyzer getAnalyzer() {
        return analyzer;
    }


}
