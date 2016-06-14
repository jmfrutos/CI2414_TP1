package buscador;

import analysis.Analyzer;
import analysis.StandardAnalyzer;
import document.Document;
import index.Posting;

import java.io.*;
import java.util.*;


/**
 * Created by Jose on 13/06/2016.
 */
public class Consulta {
    private String consulta;
    private String mensaje1;
    private String cabecera;
    private String pie;
    private String terminoBuscado;
    String resultados;

    private Analyzer analyzer = new StandardAnalyzer();
    private AbstractMap<String,Integer> termMapping = new TreeMap<String, Integer>(); //termID - termNombre
    private AbstractMap<Integer, Integer> termTF = new TreeMap<Integer, Integer>(); //termID - Frecuency en la consulta
    private AbstractMap<Integer, Double> termIDF = new TreeMap<Integer, Double>(); //termID - IDF
    private AbstractMap<Integer, Double> termNorm = new TreeMap<Integer, Double>(); //termID - normalizacion de la consulta
    private AbstractMap<Integer, Double> termTFIDF = new TreeMap<Integer, Double>(); //termID - TFIDF de la consulta
    private AbstractMap<Integer, ArrayList<Posting>> map = new TreeMap<Integer, ArrayList<Posting>>();
    private AbstractMap<Integer,Double> docSimilitud = new TreeMap<Integer, Double>(); // docID - conseno similitud (double)

    public Consulta(){
        mensaje1 =
                "     <h1>SIN RESULTADOS, LE RECOMENDAMOS MEJOR USAR -></h1>\n" +
                        "     <a href=\"http://www.google.com\">GOOGLE</a>\n";

        terminoBuscado = "\n";

        cabecera = "<html>\n" +
                "   <head>\n" +
                "     \n" +
                "   </head>\n" +
                "   <body>\n" +
                "     <p style=\"margin-top: 0\">\n" +
                "       \n" +
                "     </p>";

        pie = "   </body>\n" +
                " </html>\n" +
                " ";

        readTermMapping();
    }
    public String buscar(){
        resultados = cabecera + terminoBuscado + mensaje1 + pie;
        //Parsear y normalizar consulta
        Document doc = new Document();
        doc.setBody(consulta);
        analyzer.analyze(doc);
        StringTokenizer tokenizer = new StringTokenizer(doc.getBody_normalized());
        try {
            //Obtener interseccion terminos de consulta y diccionario
            while(tokenizer.hasMoreTokens()){
                String term = tokenizer.nextToken();
                if(termMapping.containsKey(term)){
                    if(!termTF.containsKey(term)){ termTF.put(termMapping.get(term), 1); }
                    else termTF.put(termMapping.get(term), termTF.get(termMapping.get(term)) + 1);
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("termTF"+termTF);
        // Normalización euclidiana del vector tƒ­idƒ de la consulta
        Double consultaNorm = calcularNormEU();

        // calcular Length Normalization de consulta_terms
        calcularLengthNormalization(consultaNorm);

        // calcula la similitud de cosenos entre lso documentos y la consulta
        // guardar en docSimilitud
        calcularSimilitud();

        // Ordenar documentos y crear string con resultados
        ordenar();

        return resultados;
    }

    public String formatoHTML(String titulo, String link) {
        String texto_formato = "";

        texto_formato = "<h2>" + titulo + "</h2>\n" +
                "<a href=\"" + link +"\">" + link + "</a>\n";

        return texto_formato;
    }

    public String getConsulta() {
        return consulta;
    }

    public void setConsulta(String consulta) {
        this.consulta = consulta;

        terminoBuscado = "<h1>\n" +
                "       Resultados para:\n" + this.consulta +
                "     </h1>\n";
    }

    // Leer archivo termMapping.txt y cargar termMapping
    // y leer idf.txt
    private void readTermMapping(){
        try {
            File inputFile = new File("C:\\indice\\termMapping.txt");
            FileReader fstream = new FileReader(inputFile);
            BufferedReader in = new BufferedReader(fstream);

            String line = in.readLine();

            while (line != null) {
                String term[] = line.split(" ",2);
                termMapping.put(term[0],Integer.valueOf(term[1]));
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(termMapping);

        try {
            File inputFile = new File("C:\\indice\\idf.txt");
            FileReader fstream = new FileReader(inputFile);
            BufferedReader in = new BufferedReader(fstream);

            String line = in.readLine();

            while (line != null) {
                String term[] = line.split(" ",2);
                termIDF.put(Integer.valueOf(term[0]),Double.valueOf(term[1]));
                line = in.readLine();
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(termIDF);
    }

    //Retorna Normalización euclidiana del vector tƒ­idƒ de la consulta

    private double calcularNormEU(){
        Double sumatoria = 0.0;
        for(Map.Entry<Integer, Integer> entry : termTF.entrySet()){  // para cada termino en la consulta
            termTFIDF.put(entry.getKey(), (1 + Math.log10(entry.getValue())) * termIDF.get(entry.getKey()));
            sumatoria += Math.exp(termTFIDF.get(entry.getKey()));
        }
        return Math.sqrt(sumatoria);
    }


    // calcular Length Normalization de consulta_terms
    // guardar en termNorm
    private void calcularLengthNormalization(Double consultaNorm){
        for(Map.Entry<Integer, Double> entry :termTFIDF.entrySet()){
            termTFIDF.put(entry.getKey(), termTFIDF.get(entry.getKey())/consultaNorm);
            //entry.setValue(entry.getKey()/consultaNorm);
        }
    }

    // calcula la similitud de cosenos entre lso documentos y la consulta
    // guardar en docSimilitud
    private void calcularSimilitud(){
        map = readFromDisk("C:\\indice\\norm.txt",2);
        System.out.println(map);
        for(Map.Entry<Integer, ArrayList<Posting>> entry : map.entrySet()){
            System.out.println("entry.getKey()" + entry.getKey());
            if(termTFIDF.containsKey(entry.getKey())) {
                for (Posting p : entry.getValue()) {
                    System.out.println("p.getWtf():" + p.getWtf() + "termTFIDF.get(entry.getKey())" + termTFIDF.get(entry.getKey()));
                    if (!docSimilitud.containsKey(p.getDocumentId()))
                        docSimilitud.put((int) p.getDocumentId(), termTFIDF.get(entry.getKey()) * p.getWtf());
                    else
                        docSimilitud.put((int) p.getDocumentId(), docSimilitud.get(p.getDocumentId()) + termTFIDF.get(entry.getKey()) * p.getWtf());

                }
            }
        }
        System.out.println("DOC SIMILITUD");
        System.out.println(docSimilitud);
    }

    // Ordenar docSimilitud y guardarlos en resultados
    private void ordenar(){
        //ArrayList<Integer> resultado_docs = new ArrayList<Integer>();
        List<Integer> resultado_docs = new ArrayList<Integer>();

        for (Map.Entry<Integer, Double> entry : sortByValues(docSimilitud).entrySet()) {
            resultado_docs.add(entry.getKey());
        }

    }

    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator =  new Comparator<K>() {
            public int compare(K k1, K k2) {
                int compare = map.get(k2).compareTo(map.get(k1));
                if (compare == 0) return 1;
                else return compare;
            }
        };
        Map<K, V> sortedByValues = new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

    //OPTION 1= read frecuency/ocurrency (integer)
    //OPTION 2= read wtf (double)
    public  AbstractMap<Integer, ArrayList<Posting>> readFromDisk(String path, int option) {
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
                        if(option==1)
                            postingList[i++] = Posting.fromString(term, str2[0], Integer.valueOf(str2[1]));
                        else postingList[i++] = Posting.fromString(term, str2[0], Double.valueOf(str2[1]));
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

    private void clearFile(String filename){
        try {
            File file = new File(filename);
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write("");
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}