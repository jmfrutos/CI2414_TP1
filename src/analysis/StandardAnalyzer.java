package analysis;

import com.jaunt.*;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import document.Document;

/**
 * Created by CAndres on 5/11/2016.
 */
public class StandardAnalyzer extends Analyzer  {
    private UserAgent agente;


    public StandardAnalyzer() {
    }

    public void analyze(Document doc){

        agente = new UserAgent();
        try {
            agente.open(new File(doc.getFileName()));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ResponseException e) {
            e.printStackTrace();
        }

        doc.setBody_normalized(agente.doc.innerText().toLowerCase());

    }

}
