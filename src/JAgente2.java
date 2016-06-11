/**
 * Created by CAndres on 5/29/2016.
 */
import com.jaunt.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import index.IndexWriterConfig;

import analysis.*;


/**
 * Created by Jose on 07/05/2016.
 */
public class JAgente2 {

    private UserAgent agente;
    private String direccion;
    private Set<String> sitemap = new LinkedHashSet<String>(); //¿?
    private static Set<String> visitadas = new LinkedHashSet<String>();
    private static String dominio;
    private String rutaFuentes = "/Original/";
    private String rutaDominio;
    private boolean normalizacionAlta = false;
    private boolean stemming_norm;
    private File carpetaFuentes = new File(rutaFuentes);


    private IndexWriterConfig config;

    private Analyzer analizador;

    JAgente2(String direccion_url, boolean tipoNormalizacion, boolean stemming) throws JauntException {
        agente = new UserAgent();
        direccion = direccion_url;
        normalizacionAlta = tipoNormalizacion;
        stemming_norm = stemming;

        analizador = new StandardAnalyzer();
        config = new IndexWriterConfig();

        try {
            URI uri = new URI(direccion_url);
            dominio = uri.getHost();

        }
        catch (URISyntaxException e) {

        }

        rutaDominio = rutaFuentes + dominio + "/";

        carpetaFuentes = new File(rutaDominio);

        if (!carpetaFuentes.isDirectory()) {
            carpetaFuentes.mkdir();
        }
    }

    public void recorrerWEB() throws JauntException {

        if(config.AGENT_MODE==0){
            try {
                agente.visit(direccion);
                System.out.println("Hola Andres");

                Comment doctype = agente.doc.getFirst(Comment.DOCTYPE);

                if (doctype.toString().toUpperCase().startsWith("<!DOCTYPE HTML")) {
                    System.out.println(agente.response.getHeader("date"));

                    //Extrae el contenido que se encuentra dentro del BODY del documento y lo normaliza.
                    String cuerpo = "";
                    Element title = agente.doc.findFirst("<title>");
                    Element body = agente.doc.findFirst("<body>");

                    if (body.innerHTML().contains("<style>")) {
                        Element body2 = body.findFirst("<style>");
                        cuerpo = body.innerText(" ", true, true).replace(body2.innerText(" ", true, true), "");
                    } else {
                        cuerpo = body.innerText(" ", true, true);
                    }



                    try {
                        PrintWriter writer = new PrintWriter(rutaDominio + direccion.replaceAll("[^\\p{L}\\p{Nd}]+", "") + ".txt", "UTF-8");
                        writer.println("<title>"+title.getText()+"</title>");
                            writer.println("<body>"+cuerpo+"</body>");

                        writer.close();
                    } catch (IOException e) {

                    }
                    //EXTRAE LINKS Y GUARDA LOS DE LA MISMA PAGINA
                    for (Element link : agente.doc.findEach("<a href>")) {
                        if ((link.getAt("href").contains(dominio)) && !link.getAt("href").contains("#")) {
                            //LIMITADO SOLO A HMTLS**************************
                            //if (link.getAt("href").contains(".html") || link.getAt("href").contains(".htm") || link.getAt("href").contains(".php") || link.getAt("href").contains(".asp") || link.getAt("href").contains(".aspx") || link.getAt("href").contains(".xml")) {
                            sitemap.add(link.getAt("href"));
                            //}
                        }
                    }
                    //AÑADE PAGINA A LA LISTA DE YA VISITADAS
                    visitadas.add(direccion);
                    System.out.println(direccion + "*#*" + analizador.getHashCode(direccion));

                    System.out.println("Encontrados " + sitemap.size() + " hipervinculos:");
                    for (String link : sitemap) {
                        boolean visitado = false;
                        for (String str : visitadas) {
                            if (str.contains(link)) {
                                visitado = true;
                                break;
                            }
                        }
                        if (!visitado) {
                            JAgente2 recorre = new JAgente2(link, normalizacionAlta, stemming_norm);
                            recorre.recorrerWEB();
                        }
                    }
                }

            } catch (JauntException e) {
                //System.err.println(e);
            }
        }
        else {

        }
    }



    public static void main(String[] args){
        try {
            JAgente2 agente = new JAgente2("http://ciem.ucr.ac.cr", true, true);
            agente.recorrerWEB();
        }
        catch (Exception e){

        }
    }
}
