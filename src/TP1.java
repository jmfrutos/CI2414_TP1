
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;


import analysis.Analyzer;
import analysis.StandardAnalyzer;
import com.jaunt.JauntException;
import document.Document;
import index.IndexWriter;
import index.IndexWriterConfig;
import index.SPIMI;
import index.BSBI;
import store.Directory;
import store.FSDirectory;
import store.RAMDirectory;

/**
 * Created by Jose on 07/05/2016.
 */

public class TP1 {


    /** Index all text files under a directory. */
    public static void main(String[] args){

        String usage = "java org.apache.lucene.demo.IndexFiles"
                + " [-index INDEX_PATH] [-docs DOCS_PATH] [-update]\n\n"
                + "Indexa los documentos en DOCS_PATH, creando un Indice"
                + "en INDEX_PATH en el cual luego se puede buscar utilizando SearchFiles";
        String indexPath = "index";
        String docsPath = null;
        boolean create = true; //Si crear el archivo del Indice o actualizar

        //El programa se puede ejecutar desde la consola con java -jar
        //Ej: java -jar nombreJAR [-index INDEX_PATH] [-docs DOCS_PATH] [-update]
        //A continuacion se leen los valores de los parametros

        for(int i=0;i<args.length;i++) {
            if ("-index".equals(args[i])) {
                indexPath = args[i+1];
                i++;
            } else if ("-docs".equals(args[i])) {
                docsPath = args[i+1];
                i++;
            } else if ("-update".equals(args[i])) {
                create = false;
            }
        }

        //TEMPORAL PARA DESARROLLAR

        indexPath = "C:\\indice";  //Aqui se pondra el(los) archivo(s) del Indice
        docsPath = "C:\\original"; //Aqui estan todos los archivos originales que se desean indexar (Generados por Agente)

        //FIN DE TEMPORAL

        if (docsPath == null) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }

        final Path docDir = Paths.get(docsPath);
        if (!Files.isReadable(docDir)) {
            System.out.println("Document directory '" +docDir.toAbsolutePath()+ "' does not exist or is not readable, please check the path");
            System.exit(1);
        }

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");

            Directory dir = FSDirectory.open(Paths.get(indexPath));

            IndexWriterConfig iwc = new IndexWriterConfig();
            Analyzer analyzer = new StandardAnalyzer(iwc);

            if (create) {
                // Create a new index in the directory, removing any
                // previously indexed documents:
                //iwc.setOpenMode(OpenMode.CREATE);
            } else {
                // Add new documents to an existing index:
                //iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
            }

            // Optional: for better indexing performance, if you
            // are indexing many documents, increase the RAM
            // buffer.  But if you do this, increase the max heap
            // size to the JVM (eg add -Xmx512m or -Xmx1g):
            //
            // iwc.setRAMBufferSizeMB(256.0);

            IndexWriter writer = null;

            if(iwc.SPIMI) writer = new SPIMI(dir, analyzer, iwc);
            if(iwc.BSBI)  writer = new BSBI(dir, analyzer, iwc);
            indexDocs(writer,docDir);

            // NOTE: if you want to maximize search performance,
            // you can optionally call forceMerge here.  This can be
            // a terribly costly operation, so generally it's only
            // worth it when your index is relatively static (ie
            // you're done adding documents to it):
            //
            // writer.forceMerge(1);


            writer.close();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass() +
                    "\n with message: " + e.getMessage());
        }
    }

    /**
     * Indexes the given file using the given writer, or if a directory is given,
     * recurses over files and directories found under the given directory.
     *
     * NOTE: This method indexes one document per input file.  This is slow.  For good
     * throughput, put multiple documents into your input file(s).  An example of this is
     * in the benchmark module, which can create "line doc" files, one document per line,
     * using the
     * <a href="../../../../../contrib-benchmark/org/apache/lucene/benchmark/byTask/tasks/WriteLineDocTask.html"
     * >WriteLineDocTask</a>.
     *
     * @param writer Writer to the index where the given file/dir info will be stored
     * @param path The file to index, or the directory to recurse into to find files to index
     * @throws IOException If there is a low-level I/O error
     */
    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    try {
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    } catch (IOException ignore) {
                        // don't index files that can't be read.
                    }
                    return FileVisitResult.CONTINUE;

                }
            });
        } else {
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }

    /** Indexes a single document */
    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            // make a new, empty document
            Document doc = new Document();

            // Add the path of the file as a field named "path".  Use a
            // field that is indexed (i.e. searchable), but don't tokenize
            // the field into separate words and don't index term frequency
            // or positional information:

            //Field pathField = new StringField("path", file.toString(), Field.Store.YES);
            //doc.add(pathField);
            doc.setLink(file.toString());

            // Add the last modified date of the file a field named "modified".
            // Use a LongPoint that is indexed (i.e. efficiently filterable with
            // PointRangeQuery).  This indexes to milli-second resolution, which
            // is often too fine.  You could instead create a number based on
            // year/month/day/hour/minutes/seconds, down the resolution you require.
            // For example the long value 2011021714 would mean
            // February 17, 2011, 2-3 PM.
            //doc.add(new LongPoint("modified", lastModified));
            doc.setModified(lastModified);

            // Add the contents of the file to a field named "contents".  Specify a Reader,
            // so that the text of the file is tokenized and indexed, but not stored.
            // Note that FileReader expects the file to be in UTF-8 encoding.
            // If that's not the case searching for special characters will fail.
            //doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));

            doc.setFileName(file.toString());
            doc.setBody(""); //Por el momento no interesa ya que luego JAUNT se encarga de abrir el doc
                            //aunque puede interesar para otra libreria

            //lo agrega al diccionario
            writer.addDocument(doc);
/*
            if (writer.getConfig().getOpenMode() == OpenMode.CREATE) {
                // New index, so we just add the document (no old document can be there):
                System.out.println("adding " + file);
                writer.addDocument(doc);
            } else {
                // Existing index (an old copy of this document may have been indexed) so
                // we use updateDocument instead to replace the old one matching the exact
                // path, if present:
                System.out.println("updating " + file);
                writer.updateDocument(new Term("path", file.toString()), doc);
            }
            */
        }



    }

}
