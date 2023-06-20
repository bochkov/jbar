package com.sergeybochkov.jbar.util;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;

public final class RuDateFormat extends SimpleDateFormat {

    public enum Mode {
        IME, ROD
    }

    public static final String[] MONTHS_I = {
            "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
            "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"
    };
    public static final String[] MONTHS_R = {
            "Января", "Февраля", "Марта", "Апреля", "Мая", "Июня",
            "Июля", "Августа", "Сентября", "Октября", "Ноября", "Декабря"
    };

    public RuDateFormat(String pattern) {
        this(pattern, Mode.IME);
    }

    public RuDateFormat(String pattern, Mode mode) {
        super(pattern, new DateFormatSymbols() {
            @Override
            public String[] getMonths() {
                return switch (mode) {
                    case IME -> MONTHS_I;
                    case ROD -> MONTHS_R;
                };
            }
        });
    }
}
