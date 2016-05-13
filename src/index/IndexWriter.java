package index;

import document.Document;
import store.Directory;
import index.IndexWriterConfig;
import store.IOContext;

import java.io.IOException;

/**
 * Created by CAndres on 5/11/2016.
 */
public class IndexWriter {

    Directory directory;
    IndexWriterConfig config;

    /** {@link IOContext} for all writes; you should pass this
     *  to {@link Directory#createOutput(String,IOContext)}. */
    //public final IOContext context = new IOContext(DEFAULT);

    public IndexWriter(Directory directory, IndexWriterConfig config) {
        this.directory = directory;
        this.config = config;
    }

    public void addDocument(Document doc){
        config.getAnalyzer().analyze(doc);

        try {
            directory.createOutput("C:\\indice\\prueba.txt", new IOContext(IOContext.Context.DEFAULT));

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Aqui se agrega al Diccionario documento: " + doc.getLink());
    }

    public void close(){

        try {
            directory.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
