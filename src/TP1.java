import com.jaunt.JauntException;

/**
 * Created by Jose on 07/05/2016.
 */

public class TP1 {

    public static void main(String[] args) throws JauntException {
        //AQUI PRINCIPAL
        JAgente ag = new JAgente("http://vistaatenasbnb.com");
        ag.recorrerWEB();
        TP1_Controles cl = new TP1_Controles();
        cl.setVisible(true);



    }

}
