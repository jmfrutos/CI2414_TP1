package index;

import document.Document;
import store.Directory;
import index.IndexWriterConfig;

/**
 * Created by CAndres on 5/11/2016.
 */
public class IndexWriter {

    Directory directory;
    IndexWriterConfig config;

    public IndexWriter(Directory directory, IndexWriterConfig config) {
        this.directory = directory;
        this.config = config;
    }

    public void addDocument(Document doc){
        config.getAnalyzer().analyze(doc);

        System.out.println("Aqui se agrega al Diccionario documento: " + doc.getLink());
    }

    public void close(){}
}
