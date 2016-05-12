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
    public String title;

    // String of length Integer.MAX_VALUE (always 2147483647 (231 - 1)
    public String body;

    //Link de la pagina web
    public String link;

    //Fecha o referencia de cuando se modifico el archivo
    public long modified;

    public Document() {
        this.docID=0;
        this.title ="";
        this.body ="";
        this.link ="";
        this.modified=0;
    }

    public Document(long docID, String title, String body, String link) {
        this.docID = docID;
        this.title = title;
        this.body = body;
        this.link = link;
        this.modified=0;
    }

    public long getDocID() {
        return docID;
    }

    public void setDocID(long docID) {
        this.docID = docID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public long getModified() {
        return modified;
    }

    public void setModified(long modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return "document.Document{" +
                "docID=" + docID +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}
