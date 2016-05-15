package index;

import document.Document;
import store.Directory;
import index.IndexWriterConfig;
import store.IndexOutput;
import store.IOContext;
import util.IOUtils;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.*;

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

    Long numDocs;

    String fileName;
    IndexOutput out;
    boolean success;

    protected StringTokenizer tokenizer;
    protected AbstractMap<String, ArrayList<Posting>> map;

    /** {@link IOContext} for all writes; you should pass this
     *  to {@link Directory#createOutput(String,IOContext)}. */
    //public final IOContext context = new IOContext(DEFAULT);

    public IndexWriter(Directory directory, IndexWriterConfig config) {
        this.directory = directory;
        this.config = config;
        this.success = false;
        this.numDocs= new Long(0);
    }

    public abstract void addDocument(Document doc);

    public void ramToFile(String path) {

        StringBuilder str = new StringBuilder();
        // para cada token
        Set set = map.keySet();
        Iterator iter = set.iterator();

        while(iter.hasNext()) {
            String temp = (String)iter.next();
            str.append(temp + " ");

            for (Posting i : map.get(temp)) {
                str.append(i.toString() + " ");
                System.out.println("j"+i.toString());
            }
            str.append("\n");
        }


        try {
            out.writeString(str.toString());
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (!success) {
                IOUtils.closeWhileHandlingException(out);
                IOUtils.deleteFilesIgnoringExceptions(directory, fileName);
            }
        }

    }

    public void close(){
        System.out.println(directory.toString());
        try {
            directory.close();
            IOUtils.close(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
