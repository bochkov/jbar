package com.sergeybochkov.jbar.service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.output.OutputException;

@Slf4j
public final class BarcodeGen {

    public static byte[] barcode(String data, String label) throws IOException {
        try {
            Barcode barcode = BarcodeFactory.createCode128(data);
            barcode.setLabel(label);
            barcode.setBarHeight(40);
            barcode.setBarWidth(2);
            barcode.setFont(new Font("Verdana", Font.BOLD, 13));
            barcode.setDrawingQuietSection(false);
            barcode.setDrawingText(true);
            barcode.setDoubleBuffered(true);

            BufferedImage img = new BufferedImage(
                    barcode.getWidth() - 2,  // поправки на какие-то левые пиксели
                    barcode.getHeight() - 2, // поправки на какие-то левые пиксели
                    BufferedImage.TYPE_BYTE_BINARY
            );
            barcode.draw((Graphics2D) img.getGraphics(), 0, 0);
            return ImgBytes.asBytes(img);
        } catch (BarcodeException | OutputException ex) {
            throw new IOException(ex);
        }
    }

    private BarcodeGen() {
    }

}
