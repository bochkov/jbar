package com.sergeybochkov.jbar;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.sergeybochkov.jbar.widgets.SBarcode;
import lombok.RequiredArgsConstructor;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.output.OutputException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@RequiredArgsConstructor
public final class Template {

    private static final DocumentBuilderFactory DFACTORY = DocumentBuilderFactory.newInstance();
    private static final TransformerFactory TFACTORY = TransformerFactory.newInstance();
    private static final int A4_PAPER_PIXELS = 660;

    private final File templateDir;
    private final Document document;

    public static Template fromFile(File file) throws SAXException, IOException, ParserConfigurationException {
        Document doc = DFACTORY.newDocumentBuilder().parse(file);
        return new Template(file.getParentFile(), doc);
    }

    public String description() {
        NodeList nodeList = document.getDocumentElement().getElementsByTagName("desc");
        return nodeList.getLength() > 0 ?
                nodeList.item(0).getTextContent() :
                "";
    }

    public void toFile(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            StringWriter str = new StringWriter();
            TFACTORY.newTransformer().transform(new DOMSource(document), new StreamResult(str));
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
                Element element = (Element) g.cloneNode(true);  // делаем шаблон
                element.setAttribute("id", String.valueOf(++count));
                pasteDateAndVerifier(shield, element);  // вставляем дату поверки и поверителя
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
        return this;
    }

    private void pasteDateAndVerifier(Shield shield, Element element) {
        NodeList list = element.getElementsByTagName("text");
        for (int j = 0; j < list.getLength(); ++j) {
            Node child = list.item(j);
            if (child instanceof Element el) {
                String data = el.getTextContent().trim();
                if (data.contains("{{verifier}}"))
                    el.setTextContent(data.replace("{{verifier}}", shield.verification()));
                if (data.contains("{{date}}"))
                    el.setTextContent(data.replace("{{date}}", shield.longDate()));
            }
        }
    }

    private void pasteBarAndLogo(Shield shield, Element element) throws IOException {
        NodeList list = element.getElementsByTagName("image");
        for (int j = 0; j < list.getLength(); ++j) {
            Node child = list.item(j);
            if (child instanceof Element el) {
                if (el.getAttribute("id").equals("barcode"))
                    el.setAttribute("xlink:href", generateBarcode(shield));
                if (el.getAttribute("id").equals("logo"))
                    el.setAttribute("xlink:href", pasteLogo());
            }
        }
    }

    private String generateBarcode(Shield shield) throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Barcode barcode = new SBarcode(shield).barcode();
            BufferedImage image = new BufferedImage(
                    barcode.getWidth() - 2, // поправки на какие-то левые пиксели
                    barcode.getHeight() - 2,  // поправки на какие-то левые пиксели
                    BufferedImage.TYPE_BYTE_BINARY
            );
            barcode.draw((Graphics2D) image.getGraphics(), 0, 0);
            ImageIO.write(image, "PNG", out);
            out.flush();
            return String.format("data:image/png;base64,%s",
                    Base64.getEncoder().encodeToString(out.toByteArray())
            );
        } catch (BarcodeException | OutputException ex) {
            throw new IOException(ex);
        }
    }

    private String pasteLogo() throws IOException {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            File logo = new File(templateDir, "logo.png");
            BufferedImage img = ImageIO.read(logo);
            ImageIO.write(img, "png", out);
            return String.format("data:image/png;base64,%s",
                    Base64.getEncoder().encodeToString(out.toByteArray())
            );
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
