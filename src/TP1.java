/**
 * Created by Jose on 07/05/2016.
 */

import com.jaunt.*;

public class TP1 {

    private UserAgent agente;
    private String direccion;

    TP1(String direccion_url) throws JauntException {
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
