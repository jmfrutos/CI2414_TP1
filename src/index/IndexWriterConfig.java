package index;

import analysis.Analyzer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.TreeMap;


/**
 * Created by CAndres on 5/11/2016.
 */
public class IndexWriterConfig {


    public static final int RAM_MEMORY_SIZE = 262144; //En Bytes: Ej: [1 term (42 bits) + 1 post (64 bits)]/8 = 13 Bytes

    public static final boolean COMPRESSION_NO_NUMBER = false;
    public static final boolean COMPRESSION_CASE_FOLDING = false;
    public static final boolean COMPRESSION_STOP_WORDS = false;
    public static final boolean COMPRESSION_STEMMING = true;


    public IndexWriterConfig() {

    }





}
