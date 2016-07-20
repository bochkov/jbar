package com.sergeybochkov.jbar;

import com.sergeybochkov.helpers.Pair;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.output.OutputException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;

/** Класс генерирует штрихкоды и представляет наклейки в формате svg */
public class ImageMaker implements Runnable {
    /**
     * Конструктор класса
     * @param collection коллекция наклеек
     * @param template файл шаблона наклейки
     */
    public ImageMaker(ShieldCollection collection, File template){
        this.collection = collection;
        this.template = template;
        this.result = null;
    }

    public void run(){
        try {
            result = generate();
        }
        catch (IOException e){
            Application.showError("Ошибка ввода/вывода\n" + e.getMessage());
        }
    }

    public File generateFile() {
        File file = new File("main.svg");
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), "UTF-8")) {
            writer.write(result);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return file;
    }

    /**
     * Добавляет в xml-элемент из shield-объекта дату поверки и имя поверителя
     * @param shield что вставить
     * @param element куда вставить
     */
    private void pasteDateAndVerificator(Shield shield, Element element){
        NodeList list = element.getElementsByTagName("text");
        for (int j = 0; j < list.getLength(); ++j){
            Node child = list.item(j);
            if (child instanceof Element){
                Element el = (Element)child;
                String data = el.getTextContent().trim();

                if (data.contains("{{verificator}}"))
                    el.setTextContent(data.replace("{{verificator}}", shield.getName()));
                else if (data.contains("{{date}}"))
                    el.setTextContent(data.replace("{{date}}", shield.getDate()));
            }
        }
    }

    /**
     * Добавляет в xml-элемент логотип фирмы
     * @return кодированная в base64 картинка
     */
    private String pasteLogo() {
        String result = "data:image/png;base64,";
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            File logo = new File(System.getProperty("user.dir"), "templates/logo.png");
            BufferedImage img = ImageIO.read(logo);
            ImageIO.write(img, "png", out);
            result += Base64.getEncoder().encodeToString(out.toByteArray());
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * Добавляет в xml-элемент из shield-объекта штрихкод и логотип
     * @param shield что вставить
     * @param element куда вставить
     * @throws MalformedURLException exception
     */
    private void pasteBarAndLogo(Shield shield, Element element) throws MalformedURLException {
        NodeList list = element.getElementsByTagName("image");
        for (int j = 0; j < list.getLength(); ++j){
            Node child = list.item(j);
            if (child instanceof Element){
                Element el = (Element)child;
                if (el.getAttribute("id").equals("barcode")){
                    String data = shield.getCode();
                    el.setAttribute("xlink:href", generateBarcode(data));
                }
                if (el.getAttribute("id").equals("logo"))
                    el.setAttribute("xlink:href", pasteLogo());
            }
        }
    }

    /**
     * Печать в svg-файл всех наклеек
     * @return String svg-строка
     * @throws IOException исключение ввода-вывода
     */
    public String generate() throws IOException {
        Document doc = Parser.getDoc(template);
        if (doc == null)
            throw new IOException("Ошибка файла XML");

        int width = getTemplateBounds(doc).getFirst() + 3;
        int height = getTemplateBounds(doc).getSecond() + 3;
        int x = 0;
        int y = 0;
        int br = A4_PAPER_PIXELS / width;

        Element root = doc.getDocumentElement();
        Node defs = root.getElementsByTagName("defs").item(0);
        Node g = root.getElementsByTagName("g").item(0);

        Object[] coll = collection.getShields().toArray();
        int count = 0;
        for (int i = 0; i < coll.length; ++i){
            Shield shield = (Shield)coll[i];
            if (g instanceof Element){
                // делаем шаблон
                Element element = (Element)g.cloneNode(true);
                element.setAttribute("id", String.valueOf(i));

                // вставляем дату поверки и поверителя
                pasteDateAndVerificator(shield, element);

                // вставялем штрихкод
                pasteBarAndLogo(shield, element);

                defs.appendChild(element);
            }

            for (int j = 0; j < shield.getNum(); ++j){
                if (count > br){
                    x = 0;
                    y += height;
                    count = 0;
                }

                Element use = doc.createElement("use");
                use.setAttribute("xlink:href", "#" + i);
                use.setAttribute("x", String.valueOf(x));
                use.setAttribute("y", String.valueOf(y));
                root.appendChild(use);

                ++count;
                x += width;
            }
        }
        doc.normalizeDocument();
        return Parser.toString(doc);
    }

    /**
     * Генерация штрихкода
     * @param data Строка для кодирования
     * @return String изображение в формате base64
     */
    private String generateBarcode(String data){
        String result = "data:image/png;base64,";
        Barcode barcode;
        try {
            barcode = BarcodeFactory.createCode128(data);
            barcode.setBarHeight(40);
            barcode.setBarWidth(2);
            barcode.setFont(new Font("Verdana", Font.BOLD, 13));
            barcode.setDrawingQuietSection(false);
            barcode.setDrawingText(true);
            barcode.setDoubleBuffered(true);

            String label = data.substring(0, 3) + " " +
                    data.substring(3, 5) + " " + data.substring(5);
            char[] chars = label.toCharArray();
            StringBuilder builder = new StringBuilder();
            for (char ch : chars){
                builder.append(ch);
                builder.append(" ");
            }
            barcode.setLabel(builder.toString());

            // поправки на какие-то левые пиксели
            BufferedImage image = new BufferedImage(barcode.getWidth() - 2,
                    barcode.getHeight() - 2, BufferedImage.TYPE_BYTE_BINARY);
            Graphics2D g = (Graphics2D)image.getGraphics();
            barcode.draw(g, 0, 0);

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                ImageIO.write(image, "PNG", out);
                result += Base64.getEncoder().encodeToString(out.toByteArray());
            }
        }
        catch (BarcodeException e) {
            Application.showError("Ошибка генерации штрихкода:\n" + e.getMessage());
        }
        catch (OutputException e) {
            Application.showError("Ошибка записи штрихкода:\n" + e.getMessage());
        }
        catch (IOException e) {
            Application.showError("Ошибка записи:\n" + e.getMessage());
        }

        return result;
    }

    /**
     * Возвращает пару (ширина, высота) первого элемента, т.е.
     * ширину и высоту одной наклейки
     * @param doc xml-документ
     * @return Pair
     */
    private Pair getTemplateBounds(Document doc){
        Node node = doc.getElementsByTagName("rect").item(0);
        Element element = (Element)node;
        int height = Integer.parseInt(element.getAttribute("height"));
        int width = Integer.parseInt(element.getAttribute("width"));
        return new Pair(width, height);
    }

    private ShieldCollection collection;
    private File template;
    private String result;

    private static final int A4_PAPER_PIXELS = 660;
}
