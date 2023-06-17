package com.sergeybochkov.jbar.widgets;

import java.awt.*;

import com.sergeybochkov.jbar.Shield;
import lombok.RequiredArgsConstructor;
import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeException;
import net.sourceforge.barbecue.BarcodeFactory;

@RequiredArgsConstructor
public final class SBarcode {

    private final Shield shield;

    public Barcode barcode() throws BarcodeException {
        Barcode barcode = BarcodeFactory.createCode128(shield.barcodeDate());
        barcode.setLabel(shield.barcodeLabel());
        barcode.setBarHeight(40);
        barcode.setBarWidth(2);
        barcode.setFont(new Font("Verdana", Font.BOLD, 13));
        barcode.setDrawingQuietSection(false);
        barcode.setDrawingText(true);
        barcode.setDoubleBuffered(true);
        return barcode;
    }
}
