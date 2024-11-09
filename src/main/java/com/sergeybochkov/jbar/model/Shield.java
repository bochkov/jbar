package com.sergeybochkov.jbar.model;

import com.sergeybochkov.jbar.AppProps;
import com.sergeybochkov.jbar.service.BarcodeGen;
import com.sergeybochkov.jbar.service.ImgBytes;
import com.sergeybochkov.jbar.widgets.ItemInTable;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
public final class Shield implements ItemInTable {

    private final LocalDate date;
    private final String verifier;
    private final String department;
    private final Integer count;

    public int count() {
        return count;
    }

    public String longDate() {
        return date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
    }

    public String verifier() {
        return verifier;
    }

    public byte[] logo() throws IOException {
        File logo = new File(AppProps.TMPL_DIR, "logo.png");
        return ImgBytes.asBytes(logo);
    }

    public byte[] barcode() throws IOException {
        String data = String.format("%s%s",
                department,
                date.format(DateTimeFormatter.ofPattern("MMyyyy"))
        );
        String label = String.format("%s  %s",
                department,
                date.format(DateTimeFormatter.ofPattern("MM  yyyy"))
        );
        return BarcodeGen.barcode(data, label);
    }

    @Override
    public String[] toRow() {
        return new String[]{
                department,
                date.format(DateTimeFormatter.ofPattern("MM.yyyy")),
                verifier,
                String.valueOf(count)
        };
    }
}
