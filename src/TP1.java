import analysis.Analyzer;
import analysis.StandardAnalyzer;
import document.Document;
import index.IndexWriter;
import index.IndexWriterConfig;
import store.Directory;
import store.RAMDirectory;

/**
 * Created by Jose on 07/05/2016.
 */

public class TP1 {

    public static void main(String[] args){
        //AQUI PRINCIPAL
        //JAgente ag = new JAgente("http://vistaatenasbnb.com/accommodation.html");
        //ag.recorrerWEB();

        Analyzer analyzer = new StandardAnalyzer();

        // Store the index in memory:
        Directory directory = new RAMDirectory();
        // To store an index on disk, use this instead:
        //Directory directory = FSDirectory.open("/tmp/testindex");
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter iwriter = new IndexWriter(directory, config);

        String text = "<img src=\"perro.jpg\" /><p>Este es un texto de ejemplo, perro y gato.</p>";
        Document doc = new Document(1,"<title>Los Animales</title>",text,"http://animales.com");

        iwriter.addDocument(doc);
        iwriter.close();

        /**
        // Now search the index:
        DirectoryReader ireader = DirectoryReader.open(directory);
        IndexSearcher isearcher = new IndexSearcher(ireader);
        // Parse a simple query that searches for "text":
        QueryParser parser = new QueryParser("fieldname", analyzer);
        Query query = parser.parse("text");
        ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
        assertEquals(1, hits.length);
        // Iterate through the results:
        for (int i = 0; i < hits.length; i++) {
            document.Document hitDoc = isearcher.doc(hits[i].doc);
            assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
        }
        ireader.close();
        directory.close();
        **/



    }

}
