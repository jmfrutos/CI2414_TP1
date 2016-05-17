package analysis;


import document.Document;
import index.IndexWriterConfig;

/**
 * Created by CAndres on 5/11/2016.
 */
public abstract class Analyzer {
    IndexWriterConfig config;

    public Analyzer() {
    }

    public abstract void analyze(Document doc);

    public abstract String Normalizar(String input, boolean alta);

    public abstract String removerStopWords(String input);

    public abstract int getHashCode(String texto);

    public abstract String limpiarTextoCaracteresEspeciales(String input);

    public abstract String limpiarPuntuacion(String input);

    public abstract String aplicarStemming(String input);
}
