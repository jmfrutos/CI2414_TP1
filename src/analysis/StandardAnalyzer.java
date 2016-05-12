package analysis;

import com.jaunt.*;
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
    private StringTokenizer tokenizer;

    public StandardAnalyzer() {
    }

    public void analyze(Document doc){

        tokenizer = new StringTokenizer(doc.getBody());
        try {
            while(tokenizer.hasMoreTokens()){
                String str=tokenizer.nextToken();
                System.out.println(str);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
