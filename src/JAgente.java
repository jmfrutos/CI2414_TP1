import com.jaunt.JauntException;
import com.jaunt.UserAgent;

/**
 * Created by Jose on 07/05/2016.
 */
public class JAgente {

    private UserAgent agente;
    private String direccion;

    JAgente(String direccion_url) throws JauntException {
        agente = new UserAgent();
        direccion = direccion_url;
    }

    public void recorrerWEB() throws JauntException {
        try {
            agente.visit(direccion);
            System.out.println(agente.doc.innerHTML()); //PRUEBA
        }
        catch (JauntException e) {
            System.err.println(e);
        }

    }
}
