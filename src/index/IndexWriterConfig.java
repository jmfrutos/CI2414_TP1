package index;

import analysis.Analyzer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.TreeMap;


/**
 * Created by CAndres on 5/11/2016.
 */
public class IndexWriterConfig {

    public final Analyzer analyzer;

    public static final int RAM_MEMORY_SIZE = 3000; //En Bytes: Ej: [1 term (42 bits) + 1 post (64 bits)]/8 = 13 Bytes


    public IndexWriterConfig(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /** Returns the default analyzer to use for indexing documents. */
    public Analyzer getAnalyzer() {
        return analyzer;
    }




}
