package index;

import analysis.Analyzer;
import document.Document;
import store.Directory;
import store.IOContext;
import util.IOUtils;
import util.ObjectSizeFetcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

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


    private int postingCouter;
    private int termCounter;
    private int blockCounter;


    public SPIMI(Directory directory, Analyzer analyzer, IndexWriterConfig config) {
        super(directory, analyzer, config);

        super.map = new TreeMap<String, ArrayList<Posting>>();

        try {
            fileName = "indice.txt";

            out = directory.createOutput(fileName, new IOContext(IOContext.Context.DEFAULT));
            //directory.deleteFile(fileName);

            success = true;
            postingCouter = 0;
            termCounter = 0;

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addDocument(Document doc) {
        super.numDocs++;
        doc.setDocID(super.numDocs);

        super.analyzer.analyze(doc);

        tokenizer = new StringTokenizer(doc.getBody_normalized());
        try {
            while(tokenizer.hasMoreTokens()){
                addDocToPList(tokenizer.nextToken(),doc.getDocID());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }


        System.out.println("Aqui se agrega al Diccionario documento: " + doc.getLink());

    }


    public boolean addDocToPList(String token, long docId) {

       // System.out.println();

        int usedRAM = getMapRAM();
       // System.out.println("doc:" + docId + " termCounter: " + termCounter + " postCount: " + postingCouter + " Ram: "+ usedRAM);
        if (usedRAM >= config.RAM_MEMORY_SIZE) {
            System.out.println("Flush doc:" + docId + " termCounter: " + termCounter + " postCount: " + postingCouter + " Ram: "+ usedRAM);
            flushBlock();
        }
        //System.out.println("if:" + docId + token);
        if (map.containsKey(token)) {
            //int position = map.get(token).indexOf(docId); //Para ver si el docId ya esta en la lista de postings
            if (!map.get(token).contains(new Posting(token,docId,1))) { // si el docId no esta
                map.get(token).add(new Posting(token, docId, 1));
                postingCouter++;
                return true;
            }
            else return true; //si no esta entonces no lo agregamos, en la TP aumentamos frecuencia
        }
        else {

            ArrayList<Posting> documentList = new ArrayList<Posting>();
            documentList.add(new Posting(token, docId,1));
            map.put(token, documentList);
            postingCouter++;
            termCounter++;
            return true;
        }
    }


    public void flushBlock() {
        ramToFile("block-"+String.valueOf(blockCounter) + ".txt", map);
        map.clear();
        blockCounter++;
        termCounter = 0;
        postingCouter = 0;
    }

    // Supone que el tamaño promedio es 11 caracteres. Ej: "Hello World" is 11 characters long, I would estimate
    // its size as 2*11+4+4+4=34 bytes on computers with 32-bit pointers, or 2*11+8+4+4=38 bytes on
    // computers with 64-bit pointers.
    // Retorna la memoria del mapa en Bytes
    public int getMapRAM(){
        int termsSize = 2*11+8+4+4+4* termCounter;
        return (Integer)(termsSize + postingCouter*64)/8; //Posting usa Long (64 bits)
    }

    private ArrayList<Posting> mergePostingList(ArrayList<Posting> a, ArrayList<Posting> b) {
        if(a == null) return b;
        else if(b == null) return a;

        for (Posting p1:a) {
            int idx = b.indexOf(p1);
            if (idx == -1)
                b.add(p1);

        }
        return b;
    }

    public static AbstractMap<String, ArrayList<Posting>> readFromDisk(String path) {
        try {
            TreeMap<String, ArrayList<Posting>> newMap = new TreeMap<String, ArrayList<Posting>>();

            File inputFile = new File(path);

            System.out.println("opening " + inputFile.getAbsolutePath());
            FileReader fstream = new FileReader(inputFile);
            BufferedReader in = new BufferedReader(fstream);

            String line = in.readLine();


            String term = null;
            while (line != null) {
                StringTokenizer st = new StringTokenizer(line);
                boolean firstToken = true;
                Posting[] postingList = new Posting[st.countTokens()-1];
                int i=0;
                while (st.hasMoreTokens()) {
                    if (firstToken==true) {
                        firstToken = false;
                        term = st.nextToken();
                    }
                    else {
                        postingList[i++] = Posting.fromString(term, st.nextToken());
                    }
                }

                newMap.put(term, new ArrayList<Posting>(Arrays.asList((postingList))));
                line = in.readLine();
            }

            in.close();
            return newMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //termino por termino (se usa getNextTerm()) une la lista de posting en cada block de dicho termino
    //la lista de posting resultante la agrega a index.txt mediante un append
    //getNextTerm va devolviendo los terminos en orden alfabetico
    public void mergeAllBlocks(){

        //Obtener primer term
        String lastTerm = "a";

        //Obtener siguiente termino
        String nextTerm = getNextTerm(lastTerm);

        //Para cada term
        while (! lastTerm.equals(nextTerm)) {

            ArrayList<Posting> lista = new ArrayList<Posting>(); //lista vacia para cada termino

            //Hacer merge de postings de cada bloque
            for (int i = 0; i < blockCounter-1; i++) {
                AbstractMap<String, ArrayList<Posting>> map1 = readFromDisk("C:\\indice\\block-"+i+".txt");
                int j = i + 1;
                AbstractMap<String, ArrayList<Posting>> map2 = readFromDisk("C:\\indice\\block-"+j+".txt");

                lista = mergePostingList(map1.get(nextTerm), map2.get(nextTerm));
            }

            //Agregar la lista de postings en indice
            appendToFile("C:\\indice\\indice.txt", nextTerm, lista);

            lastTerm = nextTerm;
            nextTerm = getNextTerm(nextTerm);
        }

    }

    private String getNextTerm(String term){

        String nextTerm = "";

        for(int i=0; i<blockCounter;i++){
            AbstractMap<String, ArrayList<Posting>> map1 = readFromDisk("C:\\indice\\block-"+i+".txt");
            Set set = map1.keySet();
            Iterator iter = set.iterator();
            //System.out.print("block: "+i + ": ");
            while(iter.hasNext()) {

                String puntero = (String)iter.next();
                //System.out.print(puntero+"-"+nextTerm);
                int j = puntero.compareTo(nextTerm);
                if(nextTerm.equals("")) {
                       j = puntero.compareTo(term);
                       if(j > 1 ) {
                           //System.out.print("E0");
                           nextTerm = puntero;
                       }
                }
                else if(!puntero.equals(term) && !nextTerm.equals(term)){
                    j = puntero.compareTo(nextTerm);
                    if (j < 1) {
                        int k = puntero.compareTo(term);
                        if (k > 1) {
                           // System.out.print("E3");
                            nextTerm = puntero;
                        }
                    }
                    if (j > 1) {
                       // System.out.print("E1");
                        int k = term.compareTo(puntero);
                        if (k > 1) {
                            //System.out.print("E2");
                            nextTerm = puntero;
                        }
                    }
                }

            }
        }

        if(nextTerm.equals("")) return term;
        else return nextTerm;
    }


    @Override
    public void close() {

        System.out.println("Flush Ultimo: " + " termCounter: " + termCounter + " postCount: " + postingCouter);
        flushBlock();

        //System.out.println("El siguiente es: " + getNextTerm("z"));
        mergeAllBlocks();
        //int j = "and".compareTo("a");
        //System.out.println("COMP:"+j);


        System.out.println(directory.toString());
        try {
            directory.close();
            IOUtils.close(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
