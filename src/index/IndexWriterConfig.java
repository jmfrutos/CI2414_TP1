package index;

import analysis.Analyzer;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.TreeMap;


/**
 * Created by CAndres on 5/11/2016.
 */
public class IndexWriterConfig {


    public static final int RAM_MEMORY_SIZE = 120000;//262144; // 120; //En Bytes: Ej: [1 term (42 bits) + 1 post (64 bits)]/8 = 13 Bytes

    public static final boolean COMPRESSION_NO_NUMBER = true;
    public static final boolean COMPRESSION_CASE_FOLDING = true; //NO poner FALSE por el momento
    public static final boolean COMPRESSION_STOP_WORDS = true;
    public static final boolean COMPRESSION_STEMMING = true;
    public static final boolean COMPRESSION_PUNTUATION = true;
    public static final boolean COMPRESSION_ECHARACTERES = true;

    public static final boolean SPIMI = true; //intercambiar SPIMI y BSBI
    public static final boolean BSBI = false;

    public IndexWriterConfig() {

    }





}
