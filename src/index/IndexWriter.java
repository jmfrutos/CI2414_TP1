package index;

import document.Document;
import store.Directory;
import index.IndexWriterConfig;
import store.IndexOutput;
import store.IOContext;
import util.IOUtils;

import javax.print.DocFlavor;
import java.io.IOException;

/**
 * Created by CAndres on 5/11/2016.
 */
public class IndexWriter {

    Directory directory;
    IndexWriterConfig config;

    String fileName;
    IndexOutput out;
    boolean success;

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
                out.writeString("docID: " + doc.docID); // En realidad hay que hacer el diccionario aqui con los terminos.
                                                        // ver linea config.getAnalyzer().analyze(doc);
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
