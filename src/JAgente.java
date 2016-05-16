import com.jaunt.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Matcher;


/**
 * Created by Jose on 07/05/2016.
 */
public class JAgente {

    private UserAgent agente;
    private String direccion;
    private StringTokenizer tokenizer;
    private Elements links;
    private static Set<String> sitemap = new LinkedHashSet<String>(); //¿?
    private static Set<String> visitadas = new LinkedHashSet<String>();
    private static String dominio;


    JAgente(String direccion_url) throws JauntException {
        agente = new UserAgent();
        direccion = direccion_url;

        try {
            URI uri = new URI(direccion_url);
            dominio = uri.getHost();

        }
        catch (URISyntaxException e) {

        }

        System.out.println(direccion_url);
    }

    public void recorrerWEB() throws JauntException {
        try {
            agente.visit(direccion);
            tokenizer = new StringTokenizer(agente.doc.innerText().toLowerCase());
            try {
                PrintWriter writer = new PrintWriter(direccion.replaceAll("[^\\p{L}\\p{Nd}]+", "") + ".txt", "UTF-8");
                while(tokenizer.hasMoreTokens()){
                    String str=tokenizer.nextToken();
                    writer.println(str);
                }
                writer.close();
            }
            catch (IOException e) {

            }
            //EXTRAE LINKS Y GUARDA LOS DE LA MISMA PAGINA
            for (Element link: agente.doc.findEach("<a href>")) {
                if ((link.getAt("href").contains(dominio)) && !link.getAt("href").endsWith("#")) {
                    sitemap.add(link.getAt("href"));
                }
            }
            //AÑADE PAGINA A LA LISTA DE YA VISITADAS
            visitadas.add(direccion);

            System.out.println("Encontrados " + sitemap.size() + " hipervinculos:");
            for(String link : sitemap){
                boolean visitado = false;
                for(String str: visitadas) {
                    if (str.contains(link)) {
                        visitado = true;
                        break;
                    }
                }
                if (!visitado) {
                    JAgente recorre = new JAgente(link);
                    recorre.recorrerWEB();
                }
            }
        }
        catch (JauntException e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args){
        try {
            JAgente agente = new JAgente("http://semanariouniversidad.ucr.ac.cr");
            agente.recorrerWEB();
        }
        catch (Exception e){

        }
    }
}
