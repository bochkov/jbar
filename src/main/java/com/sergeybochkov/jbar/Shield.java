package com.sergeybochkov.jbar;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public final class Shield {

    private static final DateFormat DF_LONG = new SimpleDateFormat("MMMM yyyy", new DateFormatSymbols() {
        @Override
        public String[] getMonths() {
            return new String[] {"Января", "Февраля", "Марта", "Апреля", "Мая", "Июня",
                    "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"};
        }
    });
    private static final DateFormat DF_SHORT = new SimpleDateFormat("MM.yyyy");
    private static final DateFormat DF_BARCODE = new SimpleDateFormat("MMyyyy");

    private final Date date;
    private final String verification;
    private final String department;
    private final Integer count;

    public Shield(String verification, Integer month, Integer year, String department, Integer count) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, 1);
        this.date = cal.getTime();
        this.verification = verification;
        this.department = department;
        this.count = count;
    }

    public String department() {
        return department;
    }

    public int count() {
        return count;
    }

    public String longDate() {
        return DF_LONG.format(date);
    }

    public String shortDate() {
        return DF_SHORT.format(date);
    }

    public String barcodeDate() {
        return String.format("%s%s", department, DF_BARCODE.format(date));
    }

    public String barcodeLabel() {
        return String.format("%s  %s  %s", department,
                new SimpleDateFormat("MM").format(date),
                new SimpleDateFormat("yyyy").format(date));
    }

    public String verification() {
        return verification;
    }
}
