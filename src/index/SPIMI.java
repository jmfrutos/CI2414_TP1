package index;

import document.Document;
import store.Directory;
import store.IOContext;
import util.IOUtils;

import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.AbstractMap;

/**
 * Created by CAndres on 5/15/2016.
 */
public class SPIMI extends IndexWriter {
    /**
     * {@link IOContext} for all writes; you should pass this
     * to {@link Directory#createOutput(String, IOContext)}.
     *
     * @param directory
     * @param config
     */


    private static int MEMORY_SIZE;
    private static Integer TotalBlockCounter = 0;
    private int currentBlockNumber;
    private int currentSize = 0;

    public SPIMI(Directory directory, IndexWriterConfig config) {
        super(directory, config);

        super.map = new TreeMap<String, ArrayList<Posting>>();
        //MEMORY_SIZE = config.getInt("RAM_MEMORY_SIZE");

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

    @Override
    public void addDocument(Document doc) {
        super.numDocs++;
        doc.setDocID(super.numDocs);
        System.out.println(numDocs);
        config.getAnalyzer().analyze(doc);


        tokenizer = new StringTokenizer(doc.getBody_normalized());
        try {
            while(tokenizer.hasMoreTokens()){
                addDocToPList(tokenizer.nextToken(),doc.getDocID());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if(doc.getDocID() == 3) { System.out.println("Flush");flushBlock();} //TEMPORAL mientras se habilita revision de memoria

        System.out.println("Aqui se agrega al Diccionario documento: " + doc.getLink());

    }


    public boolean addDocToPList(String token, long docId) {
        currentSize = -1;
        if (currentSize >= MEMORY_SIZE) { //hay que habilitar esto de la memoria
            System.out.println("Flush al llegar al doc:" + docId);
            flushBlock();
        }

        if (map.containsKey(token)) {
            map.get(token).add(new Posting(token, docId,1));
            return true;
        }
        else {

            ArrayList<Posting> documentList = new ArrayList<Posting>();
            documentList.add(new Posting(token, docId,1));
            map.put(token, documentList);
            return true;
        }
    }


    public void flushBlock() {
        ramToFile("block-"+String.valueOf(currentBlockNumber) + ".txt");
        map.clear();
        TotalBlockCounter++;
        currentBlockNumber = TotalBlockCounter;
        currentSize = MEMORY_SIZE;
    }
}
