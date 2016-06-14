package buscador;

import analysis.Analyzer;
import analysis.StandardAnalyzer;
import document.Document;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    private AbstractMap<Integer, Double> termNorm = new TreeMap<Integer, Double>(); //termID - normalizacion de la consulta
    private AbstractMap<Integer,Double> docSimilitud = new TreeMap<Integer, Double>(); // docID - conseno similitud (double)
    private AbstractMap<Double,Integer> docSimilitudDSC = new TreeMap<Double,Integer>(new Comparator<Double>()
    {
        @Override
        public int compare(Double o1, Double o2) {
            return o2.compareTo(o1);
        }
    });
    public ArrayList consulta_terms = new ArrayList(); //tiene los ids de los terminos que estan en el diccionario (termMapping)

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
                    consulta_terms.add(termMapping.get(term)); // agregar los ids de los terminos guardados en termMapping
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }


        // Normalización euclidiana del vector tƒ­idƒ de la consulta
        calcularNormEU();

        // calcular Length Normalization de consulta_terms
        calcularLengthNormalization();

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
    }

    //Retorna Normalización euclidiana del vector tƒ­idƒ de la consulta

    private double calcularNormEU(){

        return 1.1;
    }


    // calcular Length Normalization de consulta_terms
    // guardar en termNorm
    private void calcularLengthNormalization(){

    }

    // calcula la similitud de cosenos entre lso documentos y la consulta
    // guardar en docSimilitud
    private void calcularSimilitud(){

    }

    // Ordenar docSimilitud y guardarlos en resultados
    private void ordenar(){

        for(Map.Entry<Integer, Double> datos : docSimilitud.entrySet()) {
            docSimilitudDSC.put(datos.getValue(), datos.getKey());
        }
    }
}