package document;

/**
 * Created by CAndres on 5/11/2016.
 */
public class Document {

    //Por ESCALABILIDAD utilizamos tipo long
    // In Java SE 8 and later, you can use the long data type to represent an unsigned 64-bit long, which has a minimum value of 0 and a maximum value of 2^64-1.
    //The Long class also contains methods like compareUnsigned, divideUnsigned etc to support arithmetic operations for unsigned long.
    //https://blogs.oracle.com/darcy/entry/unsigned_api
    public long docID;

    //Obtener de los documentos html <title>Page Title</title> o el primer <h1> o <h2>
    public String titulo;

    // String of length Integer.MAX_VALUE (always 2147483647 (231 - 1)
    public String cuerpo;

    //Link de la pagina web
    public String enlace;

    public Document() {
        this.docID=0;
        this.titulo="";
        this.cuerpo="";
        this.enlace="";
    }

    public Document(long docID, String titulo, String cuerpo, String enlace) {
        this.docID = docID;
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.enlace = enlace;
    }

    public long getDocID() {
        return docID;
    }

    public void setDocID(long docID) {
        this.docID = docID;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    @Override
    public String toString() {
        return "document.Document{" +
                "docID=" + docID +
                ", titulo='" + titulo + '\'' +
                ", cuerpo='" + cuerpo + '\'' +
                ", enlace='" + enlace + '\'' +
                '}';
    }
}
