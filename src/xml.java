import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

/**
 * Created by Jose on 12/06/2016.
 */
public class XML {

    public String getElemento(String etiqueta, String archivo) {
        File file = new File(archivo);
        String elemento = "";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            try {
                Document document = documentBuilder.parse(file);
                elemento = document.getElementsByTagName(etiqueta).item(0).getTextContent();
            }
            catch (IOException | SAXException e){

            }
        }
        catch (ParserConfigurationException e){

        }
        return elemento;
    }

    public String getDOCID(String archivo) {
        File file = new File(archivo);
        String elemento = "";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            try {
                Document document = documentBuilder.parse(file);
                elemento = document.getElementsByTagName("docid").item(0).getTextContent();
            }
            catch (IOException | SAXException e){

            }
        }
        catch (ParserConfigurationException e){

        }
        return elemento;
    }

    public String getURL(String archivo) {
        File file = new File(archivo);
        String elemento = "";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            try {
                Document document = documentBuilder.parse(file);
                elemento = document.getElementsByTagName("url").item(0).getTextContent();
            }
            catch (IOException | SAXException e){

            }
        }
        catch (ParserConfigurationException e){

        }
        return elemento;
    }

    public String getTITULO(String archivo) {
        File file = new File(archivo);
        String elemento = "";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            try {
                Document document = documentBuilder.parse(file);
                elemento = document.getElementsByTagName("titulo").item(0).getTextContent();
            }
            catch (IOException | SAXException e){

            }
        }
        catch (ParserConfigurationException e){

        }
        return elemento;
    }

    public String getCUERPO(String archivo) {
        File file = new File(archivo);
        String elemento = "";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
                .newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            try {
                Document document = documentBuilder.parse(file);
                elemento = document.getElementsByTagName("cuerpo").item(0).getTextContent();
            }
            catch (IOException | SAXException e){

            }
        }
        catch (ParserConfigurationException e){

        }
        return elemento;
    }

}
