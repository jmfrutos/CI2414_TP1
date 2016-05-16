package index;

import analysis.Analyzer;
import store.Directory;

/**
 * Created by CAndres on 5/15/2016.
 */
public abstract class BSBI extends IndexWriter {
    /**
     *
     * @param directory
     * @param config
     */
    public BSBI(Directory directory, Analyzer analyzer, IndexWriterConfig config) {
        super(directory, analyzer, config);
    }
}
