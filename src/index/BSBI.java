package index;

import analysis.Analyzer;
import document.Document;
import javafx.collections.transformation.SortedList;
import store.Directory;
import store.IOContext;
import store.IndexOutput;
import util.IOUtils;
import util.ObjectSizeFetcher;

import java.io.*;
import java.util.*;

/**
 * Created by CAndres on 5/15/2016.
 */
public class BSBI extends IndexWriter {
    /**
     * {@link IOContext} for all writes; you should pass this
     * to {@link Directory#createOutput(String, IOContext)}.
     *
     * @param directory
     * @param config
     */


    private int postingCouter; //Cuenta los posting en un bloque (Para calcular memoria)
    private int termCounter; // Cuenta los teminos en un bloque (Para calcular memoria)
    private int blockCounter;
    AbstractMap<Integer, ArrayList<Posting>> map; //mapa de postings usado en cada bloque

    private AbstractMap<String,Integer> termMapping; //TermString - TermID
    private AbstractMap<Integer,Integer> termDF;

    public BSBI(Directory directory, Analyzer analyzer, IndexWriterConfig config) {
        super(directory, analyzer, config);

        map = new TreeMap<Integer, ArrayList<Posting>>();

        termMapping = new TreeMap<String, Integer>();
        termDF = new TreeMap<Integer, Integer>();

        try {
            fileName = "indice.txt";

            out = directory.createOutput(fileName, new IOContext(IOContext.Context.DEFAULT));
            //directory.deleteFile(fileName);

            success = true;
            postingCouter = 0;
            blockCounter = 0;
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
                String term = tokenizer.nextToken();
                addDocToPList(term,doc.getDocID());
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }


        System.out.println("Aqui se agrega a la lista posting el documento: " + doc.getLink());

    }


    public boolean addDocToPList(String token, long docId) {

        int usedRAM = getMapRAM();
         System.out.println("doc:" + docId + " termCounter: " + termCounter + " postCount: " + postingCouter + " Ram: "+ usedRAM);
        if (usedRAM >= config.RAM_MEMORY_SIZE) {
            System.out.println("Flush doc:" + docId + " termCounter: " + termCounter + " postCount: " + postingCouter + " Ram: "+ usedRAM);
            flushBlock();
        }

        if(!termMapping.containsKey(token)){ termMapping.put(token,termMapping.size()+1); }


        //System.out.println("if:" + docId + token);
        System.out.println(termMapping.get(token));
        if (map.containsKey(termMapping.get(token))) { //Si el diccionario (del bloque) ya tiene el termino
            //int position = map.get(token).indexOf(docId); //Para ver si el docId ya esta en la lista de postings

            Posting post = null;
            for (Posting i : map.get(termMapping.get(token))) { //Ver si existe el Posting
                if(i.getDocumentId()==docId) post=i;
            }


            if (post == null) { // si el docId no esta
                map.get(termMapping.get(token)).add(new Posting(token, docId, 1));
                postingCouter++;
                return true;
            } else { //en la TP2 aumentamos frecuencia
                post.setOccurence(post.getOccurence()+1);
                return true;
            }
        } else {

            ArrayList<Posting> documentList = new ArrayList<Posting>();
            documentList.add(new Posting(token, docId, 1));
            map.put(termMapping.get(token), documentList);
            termCounter++;
            postingCouter++;
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

    public void ramToFile(String path, AbstractMap<Integer, ArrayList<Posting>> mapa) {
        IndexOutput block = null;
        StringBuilder str = new StringBuilder();
        // para cada token
        Set set = mapa.keySet();
        Iterator iter = set.iterator();

        while(iter.hasNext()) {
            Integer temp = (Integer) iter.next();
            str.append(temp + " ");

            for (Posting i : mapa.get(temp)) {
                str.append(i.toString() + " ");

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

    // En map se guarda los termIDs - docIDs. Cada termID es 32 bits ya que usa Integer
    // Los postings son de 64-bit cada uno ya que usa Long en cada documentoID
    // Retorna la memoria del mapa en Bytes
    public int getMapRAM(){
        int termsSize = 32* termCounter;
        return (Integer)(termsSize + postingCouter*64)/8; //Posting usa Long (64 bits)
    }

    private ArrayList<Posting> mergePostingList(ArrayList<Posting> a, ArrayList<Posting> b) {
        if(a == null) return b;
        else if(b == null) return a;

        for (Posting p1:a) {
            int idx = b.indexOf(p1);
            if (idx == -1)
                b.add(p1);
            else{ // en TP sumar Ocurrencia
                //System.out.println("SUMA"+b.get(idx).getOccurence()+""+p1.getOccurence());
                b.get(idx).setOccurence(b.get(idx).getOccurence()+ p1.getOccurence());
            }

        }
        return b;
    }

    public  AbstractMap<Integer, ArrayList<Posting>> readFromDisk(String path) {
        try {
            TreeMap<Integer, ArrayList<Posting>> newMap = new TreeMap<Integer, ArrayList<Posting>>();

            File inputFile = new File(path);

           // System.out.println("opening " + inputFile.getAbsolutePath());
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
                        String str2[] = st.nextToken().split(":",2);
                        postingList[i++] = Posting.fromString(term, str2[0], Integer.valueOf(str2[1]));
                    }
                }

                newMap.put(Integer.valueOf(term), new ArrayList<Posting>(Arrays.asList((postingList))));
                line = in.readLine();
            }

            in.close();
            return newMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void appendToFile(String path, Integer term, ArrayList<Posting> postings) {
        //System.out.println("Apend:" + term);
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


    //termino por termino (se usa getAllTerms()) une la lista de posting en cada block de dicho termino
    //la lista de posting resultante la agrega a index.txt mediante un append
    //getAllTerms() supone que el diccionario cabe en memoria
    public void mergeAllBlocks(){

        //Obtener primer term
        //Set<String> allTerms = getAllTerms();


        //Para cada term
        for( Map.Entry<String,Integer> entry:entriesSortedByValues(termMapping)) {
            System.out.println(entry);

            ArrayList<Posting> lista = new ArrayList<Posting>(); //lista vacia para cada termino

            //Hacer merge de postings de cada bloque
            for (int i = 0; i <= blockCounter; i++) {
                AbstractMap<Integer, ArrayList<Posting>> mapa_postings = readFromDisk("C:\\indice\\block-"+i+".txt");

                lista = mergePostingList(mapa_postings.get(entry.getValue()), lista);
                //System.out.println("LISTA: "+lista);
            }

            //Agregar la lista de postings en indice
            //System.out.println(lista.toString());
            appendToFile("C:\\indice\\indice.txt", entry.getValue(), lista);

        }

    }

    <K,V extends Comparable<? super V>>
    SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
        SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
                new Comparator<Map.Entry<K,V>>() {
                    @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        int res = e1.getValue().compareTo(e2.getValue());
                        return res != 0 ? res : 1;
                    }
                }
        );
        sortedEntries.addAll(map.entrySet());
        return sortedEntries;
    }
/**
    private Set<String> getAllTerms(){
        Set<String> terms = new TreeSet<String>();
        for(int i=0; i<blockCounter;i++) {
            AbstractMap<String, ArrayList<Posting>> map1 = readFromDisk("C:\\indice\\block-" + i + ".txt");
            for(String term:map1.keySet())
                terms.add(term);
        }
        return terms;
    }
**/



    @Override
    public void close() {

        System.out.println("Flush Ultimo: " + " termCounter: " + termCounter + " postCount: " + postingCouter);
        flushBlock();

        mergeAllBlocks();
        /*
        Iterator<Map.Entry<String,Integer>> iter = entriesSortedByValues(termMapping).iterator();
        while(iter.hasNext()) {
            Map.Entry<String,Integer> entry = iter.next();
            System.out.println(entry.getKey()+"-"+entry.getValue());
        }
        */

        try {
            directory.close();
            IOUtils.close(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
