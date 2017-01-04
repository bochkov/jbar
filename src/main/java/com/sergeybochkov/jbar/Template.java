package com.sergeybochkov.jbar;

import com.sergeybochkov.jbar.widgets.SBarcode;
import net.sourceforge.barbecue.Barcode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.List;

public final class Template {

    public static final String OUT_NAME = "main.svg";

    private static final int A4_PAPER_PIXELS = 660;

    private final Document document;

    public Template(File file) throws Exception {
        this.document = DocumentBuilderFactory.newInstance()
                .newDocumentBuilder()
                .parse(file);
    }

    public String description() {
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("description");
        return (nodeList.getLength() > 0) ?
                nodeList.item(0).getTextContent() :
                "";
    }

    public File toFile() throws Exception {
        StringWriter writer = new StringWriter();
        TransformerFactory.newInstance().newTransformer()
                .transform(new DOMSource(document), new StreamResult(writer));
        File outFile = new File(OUT_NAME);
        try (OutputStreamWriter fileWriter = new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8")) {
            fileWriter.write(writer.toString());
        }
        return outFile;
    }

    public File generate(List<Shield> shields) throws Exception {
        Rectangle rect = getTemplateBounds();
        Element root = document.getDocumentElement();
        Node defs = root.getElementsByTagName("defs").item(0);
        Node g = root.getElementsByTagName("g").item(0);

        int count = 0;
        int countInRow = 0;
        for (Shield shield : shields) {
            if (g instanceof Element) {
                Element element = (Element) g.cloneNode(true);  // делаем шаблон
                element.setAttribute("id", String.valueOf(++count));
                pasteDateAndVerificator(shield, element);  // вставляем дату поверки и поверителя
                pasteBarAndLogo(shield, element);  // вставялем штрихкод
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
        return toFile();
    }

    private void pasteDateAndVerificator(Shield shield, Element element) {
        NodeList list = element.getElementsByTagName("text");
        for (int j = 0; j < list.getLength(); ++j) {
            Node child = list.item(j);
            if (child instanceof Element) {
                Element el = (Element) child;
                String data = el.getTextContent().trim();
                if (data.contains("{{verificator}}"))
                    el.setTextContent(data.replace("{{verificator}}", shield.verification()));
                if (data.contains("{{date}}"))
                    el.setTextContent(data.replace("{{date}}", shield.longDate()));
            }
        }
    }

    private String pasteLogo() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            File logo = new File(System.getProperty("user.dir"), "templates/logo.png");
            BufferedImage img = ImageIO.read(logo);
            ImageIO.write(img, "png", out);
            return String.format("data:image/png;base64,%s",
                    Base64.getEncoder().encodeToString(out.toByteArray()));
        }
    }

    private void pasteBarAndLogo(Shield shield, Element element) throws Exception {
        NodeList list = element.getElementsByTagName("image");
        for (int j = 0; j < list.getLength(); ++j) {
            Node child = list.item(j);
            if (child instanceof Element) {
                Element el = (Element) child;
                if (el.getAttribute("id").equals("barcode"))
                    el.setAttribute("xlink:href", generateBarcode(shield));
                if (el.getAttribute("id").equals("logo"))
                    el.setAttribute("xlink:href", pasteLogo());
            }
        }
    }

    private String generateBarcode(Shield shield) throws Exception {
        Barcode barcode = new SBarcode(shield).barcode();
        BufferedImage image = new BufferedImage(barcode.getWidth() - 2,
                barcode.getHeight() - 2, BufferedImage.TYPE_BYTE_BINARY); // поправки на какие-то левые пиксели
        barcode.draw((Graphics2D) image.getGraphics(), 0, 0);
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ImageIO.write(image, "PNG", out);
            return String.format("data:image/png;base64,%s",
                    Base64.getEncoder().encodeToString(out.toByteArray()));
        }
    }

    private Rectangle getTemplateBounds() {
        Node node = document.getElementsByTagName("rect").item(0);
        Element element = (Element) node;
        return new Rectangle(
                Integer.parseInt(element.getAttribute("width")) + 3,
                Integer.parseInt(element.getAttribute("height")) + 3);
    }
}
