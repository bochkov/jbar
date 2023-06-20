package com.sergeybochkov.jbar.model;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sergeybochkov.jbar.AppProps;
import com.sergeybochkov.jbar.service.BarcodeGen;
import com.sergeybochkov.jbar.service.ImgBytes;
import com.sergeybochkov.jbar.util.RuDateFormat;
import com.sergeybochkov.jbar.widgets.TableItemizable;

public final class Shield implements TableItemizable {

    private final DateFormat dfLong = new RuDateFormat("MMMM yyyy");
    private final DateFormat dfShort = new RuDateFormat("MM.yyyy");

    private final Date date;
    private final String verifier;
    private final String department;
    private final Integer count;

    public Shield(String verifier, Integer month, Integer year, String department, Integer count) {
        this.verifier = verifier;
        this.department = department;
        this.count = count;
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        this.date = cal.getTime();
    }

    public int count() {
        return count;
    }

    public String longDate() {
        return dfLong.format(date);
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
                new SimpleDateFormat("MMyyyy").format(date)
        );
        String label = String.format("%s  %s  %s",
                department,
                new SimpleDateFormat("MM").format(date),
                new SimpleDateFormat("yyyy").format(date)
        );
        return BarcodeGen.barcode(data, label);
    }

    @Override
    public String[] toItem() {
        return new String[]{
                department,
                dfShort.format(date),
                verifier,
                String.valueOf(count)
        };
    }
}
