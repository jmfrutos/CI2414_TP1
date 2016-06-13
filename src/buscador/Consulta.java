package buscador;

/**
 * Created by Jose on 13/06/2016.
 */
public class Consulta {
    private String consulta;
    private String mensaje1;
    private String cabecera;
    private String pie;
    private String terminoBuscado;

    public Consulta(String c){
        consulta = c;
        mensaje1 =
                "     <h1>\n" +
                "       SIN RESULTADOS, LE RECOMENDAMOS MEJOR USAR ->\n" +
                "     </h1>\n" +
                "     <a href=\"http://www.google.com\">GOOGLE</a>\n";

        terminoBuscado = "<h1>\n" +
                "       Resultados para:\n" + c +
                "     </h1>\n";

        cabecera = "<html>\n" +
                "   <head>\n" +
                "     \n" +
                "   </head>\n" +
                "   <body>\n" +
                "     <p style=\"margin-top: 0\">\n" +
                "       \n" +
                "     </p>";

        pie = "   </body>\n" +
                " </html>\n" +
                " ";
    }
    public String buscar(){
        String resultados = cabecera + terminoBuscado + mensaje1 + pie;



        return resultados;
    }
    public String normalizarConsulta(){

        return "";
    }
    public String formatoHTML(String titulo, String link) {
        String texto_formato = "";

        texto_formato = "<h2>" + titulo + "</h2>\n" +
                        "<a href=\"" + link +"\">" + link + "</a>\n";

        return texto_formato;
    }
}
