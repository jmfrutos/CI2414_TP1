import com.jaunt.*;

import java.io.File;
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
    private Set<String> sitemap = new LinkedHashSet<String>(); //¿?
    private static Set<String> visitadas = new LinkedHashSet<String>();
    private static String dominio;
    private String rutaFuentes = "/Fuentes/";
    private String rutaConfig = "/Config/";
    private String rutaIndice = "/Indice/";
    private File carpetaFuentes = new File(rutaFuentes);
    private File carpetaConfig = new File(rutaConfig);
    private File carpetaIndice = new File(rutaIndice);

    JAgente(String direccion_url) throws JauntException {
        agente = new UserAgent();
        direccion = direccion_url;

        if (!carpetaFuentes.isDirectory()) {
            carpetaFuentes.mkdir();
        }

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

            Comment doctype = agente.doc.getFirst(Comment.DOCTYPE); //get doc's first child doctype
            System.out.println("doctype: " + doctype);                 //print the comment

            if (doctype.toString().toUpperCase().startsWith("<!DOCTYPE HTML")) {
                System.out.println(agente.response.getHeader("date"));


                tokenizer = new StringTokenizer(agente.doc.innerText().toLowerCase());

                try {
                    PrintWriter writer = new PrintWriter(rutaFuentes + direccion.replaceAll("[^\\p{L}\\p{Nd}]+", "") + ".txt", "UTF-8");
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
                    if ((link.getAt("href").contains(dominio)) && !link.getAt("href").contains("#")) {
                        //LIMITADO SOLO A HMTLS**************************
                        //if (link.getAt("href").contains(".html")) {
                        sitemap.add(link.getAt("href"));
                        //}
                    }
                }
                //AÑADE PAGINA A LA LISTA DE YA VISITADAS
                visitadas.add(direccion);
                System.out.println(direccion + "*#*" + getHashCode(direccion));

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
        }
        catch (JauntException e) {
            System.err.println(e);
        }
    }

    public int getHashCode(String texto){
        return texto.hashCode();
    }
}
