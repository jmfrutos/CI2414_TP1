import com.jaunt.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import analysis.*;


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
    private String rutaFuentes = "/Original/";
    private String rutaDominio;
    private String rutaConfig = "/Config/";
    private String rutaIndice = "/Indice/";
    private String ficheroStopWords = "stopwordES.txt"; //http://www.ranks.nl/stopwords/spanish
    private boolean normalizacionAlta = false;
    private boolean stemming_norm;
    private File carpetaFuentes = new File(rutaFuentes);
    private File carpetaConfig = new File(rutaConfig);
    private File carpetaIndice = new File(rutaIndice);

    private Analyzer analizador;

    JAgente(String direccion_url, boolean tipoNormalizacion, boolean stemming) throws JauntException {
        agente = new UserAgent();
        direccion = direccion_url;
        normalizacionAlta = tipoNormalizacion;
        stemming_norm = stemming;

        analizador = new StandardAnalyzer();

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
        try {
            agente.visit(direccion);


            Comment doctype = agente.doc.getFirst(Comment.DOCTYPE);

            if (doctype.toString().toUpperCase().startsWith("<!DOCTYPE HTML")) {
                System.out.println(agente.response.getHeader("date"));

                //Extrae el contenido que se encuentra dentro del BODY del documento y lo normaliza.
                String cuerpo = "";
                Element body = agente.doc.findFirst("<body>");

                if (body.innerHTML().contains("<style>")){
                    Element body2 = body.findFirst("<style>");
                    cuerpo = body.innerText(" ", true, true).replace(body2.innerText(" ", true, true), "");
                }
                else {
                    cuerpo = body.innerText(" ", true, true);
                }

                tokenizer = new StringTokenizer(analizador.Normalizar(cuerpo, normalizacionAlta));

                try {
                    PrintWriter writer = new PrintWriter(rutaDominio + direccion.replaceAll("[^\\p{L}\\p{Nd}]+", "") + ".txt", "UTF-8");
                    while(tokenizer.hasMoreTokens()){
                        String str = "";
                        if (stemming_norm) {
                            str = analizador.aplicarStemming(tokenizer.nextToken());
                        }
                        else {
                            str = tokenizer.nextToken();
                        }
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
                        //if (link.getAt("href").contains(".html") || link.getAt("href").contains(".htm") || link.getAt("href").contains(".php") || link.getAt("href").contains(".asp") || link.getAt("href").contains(".aspx") || link.getAt("href").contains(".xml")) {
                        sitemap.add(link.getAt("href"));
                        //}
                    }
                }
                //AÑADE PAGINA A LA LISTA DE YA VISITADAS
                visitadas.add(direccion);
                System.out.println(direccion + "*#*" + analizador.getHashCode(direccion));

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
                        JAgente recorre = new JAgente(link, normalizacionAlta, stemming_norm);
                        recorre.recorrerWEB();
                    }
                }
            }
        }
        catch (JauntException e) {
            //System.err.println(e);
        }
    }



    public static void main(String[] args){
        try {
            JAgente agente = new JAgente("http://semanariouniversidad.ucr.ac.cr", true, true);
            agente.recorrerWEB();
        }
        catch (Exception e){

        }
    }
}