package index;

import document.Document;
import store.Directory;
import index.IndexWriterConfig;
import store.IndexOutput;
import store.IOContext;
import util.IOUtils;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * Created by CAndres on 5/11/2016.
 */
public class IndexWriter {

    Directory directory;
    IndexWriterConfig config;

    String fileName;
    IndexOutput out;
    boolean success;

    private StringTokenizer tokenizer;

    /** {@link IOContext} for all writes; you should pass this
     *  to {@link Directory#createOutput(String,IOContext)}. */
    //public final IOContext context = new IOContext(DEFAULT);

    public IndexWriter(Directory directory, IndexWriterConfig config) {
        this.directory = directory;
        this.config = config;
        this.success = false;

        try {
            fileName = "indice.txt";

            out = directory.createOutput(fileName, new IOContext(IOContext.Context.DEFAULT));
            //directory.deleteFile(fileName);

            success = true;

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addDocument(Document doc){
        config.getAnalyzer().analyze(doc);
        try {


            try {

                // Aqui hay que hacer el diccionario con los terminos (no tokens).
                out.writeString("docID: " + doc.docID + "tokens: ");

                tokenizer = new StringTokenizer(doc.getBody_normalized());
                try {
                    while(tokenizer.hasMoreTokens()){
                        String str=tokenizer.nextToken();
                        out.writeString(str);
                    }
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }



            } finally {
                if (!success) {
                    IOUtils.closeWhileHandlingException(out);
                    IOUtils.deleteFilesIgnoringExceptions(directory, fileName);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Aqui se agrega al Diccionario documento: " + doc.getLink());
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
