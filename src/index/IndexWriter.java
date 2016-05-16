package index;

import document.Document;
import store.Directory;
import index.IndexWriterConfig;
import store.IndexOutput;
import store.IOContext;
import util.IOUtils;

import javax.print.DocFlavor;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import analysis.Analyzer;

/**
 * Created by CAndres on 5/11/2016.
 *
 * Can we keep all postings in memory and then do the sort in-memory at the end? No, not for large collections
 *
 * BSBI (i) segments the collection into parts of equal size, (ii) sorts the termID-docID pairs
 * of each part in memory, (iii) stores intermediate sorted results on disk, and (iv) merges all intermediate results into the final index.
 * http://nlp.stanford.edu/IR-book/html/htmledition/blocked-sort-based-indexing-1.html
 */
public abstract class IndexWriter {

    Directory directory;
    IndexWriterConfig config;
    Analyzer analyzer;

    Long numDocs;

    String fileName;
    IndexOutput out;
    boolean success;

    protected StringTokenizer tokenizer;
    protected AbstractMap<String, ArrayList<Posting>> map;

    /** {@link IOContext} for all writes; you should pass this
     *  to {@link Directory#createOutput(String,IOContext)}. */
    //public final IOContext context = new IOContext(DEFAULT);

    public IndexWriter(Directory directory, Analyzer analyzer, IndexWriterConfig config) {
        this.directory = directory;
        this.analyzer = analyzer;
        this.config = config;
        this.success = false;
        this.numDocs= new Long(0);
    }

    public abstract void addDocument(Document doc);

    public void ramToFile(String path, AbstractMap<String, ArrayList<Posting>> mapa) {
        IndexOutput block = null;
        StringBuilder str = new StringBuilder();
        // para cada token
        Set set = mapa.keySet();
        Iterator iter = set.iterator();

        while(iter.hasNext()) {
            String temp = (String)iter.next();
            str.append(temp + " ");

            for (Posting i : mapa.get(temp)) {
                str.append(i.toString() + " ");
                //System.out.println(i.toString());
            }
            str.append("\n");
        }

        try {
            block = directory.createOutput(path, new IOContext(IOContext.Context.DEFAULT));
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        try {
            block.writeString(str.toString());
            IOUtils.close(block);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(block);
                IOUtils.deleteFilesIgnoringExceptions(directory, fileName);
            }
        }

    }

    public void appendToFile(String path, String term, ArrayList<Posting> postings) {

        StringBuilder str = new StringBuilder();
        for (Posting i : postings) {
            str.append(i.toString() + " ");
        }
        str.append("\n");


        File file = new File(path);
        try {
            file.createNewFile();

            FileWriter writer = new FileWriter(file, true);

            writer.write(term+" "+str.toString());
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public abstract void close();
}
