import com.jaunt.*;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

    JAgente(String direccion_url, boolean tipoNormalizacion, boolean stemming) throws JauntException {
        agente = new UserAgent();
        direccion = direccion_url;
        normalizacionAlta = tipoNormalizacion;
        stemming_norm = stemming;

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

                tokenizer = new StringTokenizer(Normalizar(cuerpo, normalizacionAlta));

                try {
                    PrintWriter writer = new PrintWriter(rutaDominio + direccion.replaceAll("[^\\p{L}\\p{Nd}]+", "") + ".txt", "UTF-8");
                    while(tokenizer.hasMoreTokens()){
                        String str = "";
                        if (stemming_norm) {
                            str = aplicarStemming(tokenizer.nextToken());
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

    /**
     * Función que normaliza de dos maneras
     * una cadena de texto.
     * @param input
     * @param alta
     * @return cadena de texto limpia normalizada.
     */
    public String Normalizar(String input, boolean alta) {
        String normalizado = "";

        if (alta) {
            return normalizado = removerStopWords(limpiarPuntuacion(limpiarTextoCaracteresEspeciales(input.toLowerCase())));
        }
        else {
            return normalizado = input.toLowerCase();
        }
    }//Normalizar

    public int getHashCode(String texto){
        return texto.hashCode();
    }

    /**
     * Función que elimina las "stopwords" de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de "stopwords".
     */
    public String removerStopWords(String input) {
        String stopWords = "";
        String sCurrentLine;
        String regex = "\\b(";

        try {
            FileReader fr = new FileReader(rutaConfig + ficheroStopWords);
            BufferedReader br = new BufferedReader(fr);
            try {
                while ((sCurrentLine = br.readLine()) != null) {

                    regex += sCurrentLine;
                }
                regex += ")\\b\\s?";
            }
            catch (IOException e) {

            }
        }
        catch (FileNotFoundException e) {

        }

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);
        String s_stopwords = m.replaceAll("");
        return s_stopwords;
    }//removerStopWords

    /**
     * Función que elimina acentos y caracteres especiales de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de acentos y caracteres especiales.
     */
    public static String limpiarTextoCaracteresEspeciales(String input) {
        // Descomposición canónica
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        // Nos quedamos únicamente con los caracteres ASCII
        Pattern pattern = Pattern.compile("\\P{ASCII}+");
        return pattern.matcher(normalized).replaceAll("");
    }//limpiarTextoCaracteresEspeciales

    /**
     * Función que elimina signos de puntuación de
     * una cadena de texto.
     * @param input
     * @return cadena de texto limpia de signos de puntuación.
     */
    public static String limpiarPuntuacion(String input) {
        String s_noPuntuacion = input.replaceAll("\\p{Punct}+", "");;
        return s_noPuntuacion;
    }//limpiarPuntuacion

    /**
     * Función que aplica un proceso de "stemming" a
     * un "token" de texto.
     * @param input
     * @return "token" de texto modificado por stemming.
     */
    public String aplicarStemming(String input) {
        Stemm_es stemm = new Stemm_es();

        return stemm.stemm(input);
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
