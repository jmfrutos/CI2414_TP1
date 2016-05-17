package analysis;

import com.jaunt.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import document.Document;
import analysis.PorterStemmer;
import index.IndexWriterConfig;

/**
 * Created by CAndres on 5/11/2016.
 */
public class StandardAnalyzer extends Analyzer  {
    private UserAgent agente;

    private String rutaFuentes = "/Original/";
    private String rutaDominio;
    private String rutaConfig = "/Config/";
    private String rutaIndice = "/Indice/";

    public StandardAnalyzer(){}

    public StandardAnalyzer(IndexWriterConfig config) {
        super.config = config;
    }

    public void analyze(Document doc){

        // ----   REMOVE HTML ------
        agente = new UserAgent();
        try {
            agente.open(new File(doc.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }

        if(config.COMPRESSION_CASE_FOLDING)
            doc.setBody_normalized(agente.doc.innerText().toLowerCase());

        // ----   PUNTUATION ------
        if(config.COMPRESSION_PUNTUATION)
            doc.setBody_normalized(limpiarPuntuacion(doc.getBody_normalized()));

        // --- ESPECIAL CHARACTERES --
        if(config.COMPRESSION_ECHARACTERES)
            doc.setBody_normalized(limpiarTextoCaracteresEspeciales(doc.getBody_normalized()));

        // ----   STEMMING ------
        if(config.COMPRESSION_STEMMING)
            doc.setBody_normalized(aplicarStemming(doc.getBody_normalized()));

        // ----   STOPING WORD ------
        if(config.COMPRESSION_STOP_WORDS)
            doc.setBody_normalized(removerStopWords(doc.getBody_normalized()));

        // ----   NO NUMBERS ------
        if(config.COMPRESSION_NO_NUMBER)
            doc.setBody_normalized(removerStopWords(doc.getBody_normalized()));

        // System.out.println(doc.getBody_normalized());
    }
    private void stem(Document doc){
        //Lo siguiente es un EJEMPLO
        PorterStemmer ps = new PorterStemmer();

        String texto_con_steam = "";
        StringTokenizer tokenizer = new StringTokenizer(doc.getBody_normalized());

        try {
            while(tokenizer.hasMoreTokens()){
                String word = tokenizer.nextToken();

                //Usar PorterStemmer
                //texto_con_steam += ps.stem(word);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        doc.setBody_normalized(texto_con_steam);
    }

    /**
     * Función que normaliza de dos maneras
     * una cadena de texto.
     * @param input
     * @param alta
     * @return cadena de texto limpia normalizada.
     */
    public String Normalizar(String input, boolean alta) {
        String normalizado = "";

        if (alta) {
            return normalizado = removerStopWords(limpiarPuntuacion(limpiarTextoCaracteresEspeciales(input.toLowerCase())));
        }
        else {
            return normalizado = input.toLowerCase();
        }
    }//Normalizar

    public int getHashCode(String texto){
        return texto.hashCode();
    }

    /**
     * Función que elimina las "stopwords" de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de "stopwords".
     */
    public String removerStopWords(String input) {
        String stopWords = "";
        String sCurrentLine;
        String regex = "\\b(";

        try {
            FileReader fr = new FileReader(rutaConfig + "ficheroStopWords");
            BufferedReader br = new BufferedReader(fr);
            try {
                while ((sCurrentLine = br.readLine()) != null) {

                    regex += sCurrentLine;
                }
                regex += ")\\b\\s?";
            }
            catch (IOException e) {

            }
        }
        catch (FileNotFoundException e) {

        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        String s_stopwords = m.replaceAll("");
        return s_stopwords;
    }//removerStopWords

    /**
     * Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public String limpiarTextoCaracteresEspeciales(String input) {
        // Descomposición canónica
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Nos quedamos únicamente con los caracteres ASCII
        Pattern pattern = Pattern.compile("\\P{ASCII}+");
        return pattern.matcher(normalized).replaceAll("");
    }//limpiarTextoCaracteresEspeciales

    /**
     * Función que elimina signos de puntuación de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de signos de puntuación.
     */
    public String limpiarPuntuacion(String input) {
        String s_noPuntuacion = input.replaceAll("\\p{Punct}+", "");;
        return s_noPuntuacion;
    }//limpiarPuntuacion

    /**
     * Función que aplica un proceso de "stemming" a
     * un "token" de texto.
     * @param input
     * @return "token" de texto modificado por stemming.
     */
    public String aplicarStemming(String input) {
        Stemm_es stemm = new Stemm_es();

        return stemm.stemm(input);
    }


}
