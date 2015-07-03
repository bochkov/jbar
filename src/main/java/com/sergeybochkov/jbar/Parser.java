package com.sergeybochkov.jbar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

/**
 * Предоставляет набор функций для работы с xml-файлами
 */
public class Parser {
    /**
     * @param file xml-файл
     * @return Document
     */
    public static Document getDoc(File file){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(file);
        }
        catch (ParserConfigurationException | SAXException | IOException e) { e.printStackTrace(); }

        return null;
    }

    /**
     * Возвращает значение содержащееся в теге description
     * @param file xml-файл
     * @return String
     */
    public static String getDesc(File file){
        Document doc = getDoc(file);
        if (doc == null) return null;

        Element root = doc.getDocumentElement();
        NodeList nodeList = root.getElementsByTagName("description");
        if (nodeList.getLength() > 0)
            return nodeList.item(0).getTextContent();
        else
            return null;
    }

    /**
     * Преобразует документ в строку
     * @param document document
     * @return String
     */
    public static String toString(Document document){
        StringWriter writer;
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(document), new StreamResult(writer = new StringWriter()));
            return writer.toString();
        } catch (TransformerException e) { e.printStackTrace(); }

        return null;
    }
}
