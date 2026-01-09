package com.sergeybochkov.jbar.service;

import com.sergeybochkov.jbar.model.Shield;
import lombok.RequiredArgsConstructor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@RequiredArgsConstructor
public final class Template {

    private static final DocumentBuilderFactory D_FACTORY = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory T_FACTORY = TransformerFactory.newInstance();
    private static final int A4_PAPER_PIXELS = 660;

    private final Document document;

    public static Template fromFile(File file) throws SAXException, IOException, ParserConfigurationException {
        Document doc = D_FACTORY.newDocumentBuilder().parse(file);
        return new Template(doc);
    }

    public String description() {
        NodeList nodes = document.getDocumentElement().getElementsByTagName("desc");
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
    }

    public Integer index() {
        NodeList nodes = document.getDocumentElement().getElementsByTagName("metadata");
        if (nodes.getLength() > 0) {
            Node node = nodes.item(0).getAttributes().getNamedItem("tabindex");
            if (node != null) {
                return Integer.parseInt(node.getNodeValue());
            }
        }
        return -1;
    }

    public void toFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            StringWriter str = new StringWriter();
            T_FACTORY.newTransformer().transform(new DOMSource(document), new StreamResult(str));
            writer.write(str.toString());
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

    public Template generate(List<Shield> shields) throws IOException {
        Rectangle rect = templateBounds();
        Element root = document.getDocumentElement();
        Node defs = root.getElementsByTagName("defs").item(0);
        Node g = root.getElementsByTagName("g").item(0);

        int count = 0;
        int countInRow = 0;
        for (Shield shield : shields) {
            if (g instanceof Element) {
                Element element = (Element) g.cloneNode(true);
                element.setAttribute("id", String.valueOf(++count));
                pasteImageById(element, "barcode", shield.barcode());
                pasteImageById(element, "logo", shield.logo());
                pasteStringByMark(element, "{{verifier}}", shield.verifier());
                pasteStringByMark(element, "{{date}}", shield.longDate());
                pasteStringByMark(element, "{{barcode_label}}", shield.barcodeLabel());
                defs.appendChild(element);
            }

            for (int j = 0; j < shield.count(); ++j) {
                if (countInRow > A4_PAPER_PIXELS / rect.width) {
                    rect.x = 0;
                    rect.y += rect.height;
                    countInRow = 0;
                }

                Element use = document.createElement("use");
                use.setAttribute("xlink:href", "#" + count);
                use.setAttribute("x", String.valueOf(rect.x));
                use.setAttribute("y", String.valueOf(rect.y));
                root.appendChild(use);
                ++countInRow;
                rect.x += rect.width;
            }
        }
        document.normalizeDocument();
        return this;
    }

    private void pasteStringByMark(Element element, String mark, String value) {
        NodeList list = element.getElementsByTagName("text");
        for (int i = 0; i < list.getLength(); ++i) {
            Node child = list.item(i);
            if (child instanceof Element el) {
                String data = el.getTextContent().trim();
                if (data.contains(mark)) {
                    el.setTextContent(data.replace(mark, value));
                }
            }
        }
    }

    private void pasteImageById(Element element, String id, byte[] img) {
        NodeList list = element.getElementsByTagName("image");
        for (int j = 0; j < list.getLength(); ++j) {
            Node child = list.item(j);
            if (child instanceof Element el && el.getAttribute("id").equals(id)) {
                String data = String.format("data:image/png;base64,%s", Base64.getEncoder().encodeToString(img));
                el.setAttribute("xlink:href", data);
            }
        }
    }

    private Rectangle templateBounds() {
        Node node = document.getElementsByTagName("rect").item(0);
        Element element = (Element) node;
        return new Rectangle(
                Integer.parseInt(element.getAttribute("width")) + 3,
                Integer.parseInt(element.getAttribute("height")) + 3
        );
    }
}
