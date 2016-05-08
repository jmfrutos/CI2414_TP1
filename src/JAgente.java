import com.jaunt.*;
import java.util.*;


/**
 * Created by Jose on 07/05/2016.
 */
public class JAgente {

    private UserAgent agente;
    private String direccion;
    private StringTokenizer tokenizer;

    JAgente(String direccion_url) throws JauntException {
        agente = new UserAgent();
        direccion = direccion_url;
    }

    public void recorrerWEB() throws JauntException {
        try {
            agente.visit(direccion);
            tokenizer = new StringTokenizer(agente.doc.innerText().toLowerCase());
            while(tokenizer.hasMoreTokens()){
                String str=tokenizer.nextToken();
                System.out.println(str);
            }
        }
        catch (JauntException e) {
            System.err.println(e);
        }

    }
}
