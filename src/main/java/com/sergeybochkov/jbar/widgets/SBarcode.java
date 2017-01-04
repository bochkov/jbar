package com.sergeybochkov.jbar.widgets;

import com.sergeybochkov.jbar.Shield;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;

import java.awt.*;

public final class SBarcode {

    private final Barcode barcode;

    public SBarcode(Shield shield) throws BarcodeException {
        barcode = BarcodeFactory.createCode128(shield.barcodeDate());
        barcode.setLabel(shield.barcodeLabel());
        barcode.setBarHeight(40);
        barcode.setBarWidth(2);
        barcode.setFont(new Font("Verdana", Font.BOLD, 13));
        barcode.setDrawingQuietSection(false);
        barcode.setDrawingText(true);
        barcode.setDoubleBuffered(true);
    }

    public Barcode barcode() {
        return barcode;
    }
}
