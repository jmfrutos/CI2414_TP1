package analysis;

import com.jaunt.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import document.Document;
import analysis.PorterStemmer;
import index.IndexWriterConfig;

/**
 * Created by CAndres on 5/11/2016.
 */
public class StandardAnalyzer extends Analyzer  {
    private UserAgent agente;


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

        doc.setBody_normalized(agente.doc.innerText().toLowerCase());

        // ----   STEMMING ------
        //if(config.COMPRESSION_STEMMING) stem(doc);

        // ----   STOPING WORD ------
        //if(config.COMPRESSION_STOP_WORDS) stop_words(doc);

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


}
