package com.jorge.acme_explorer.util;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class UtilFecha {

    public static String formateaFecha(long millis) {
        DateFormat formato = DateFormat.getDateInstance(DateFormat.MEDIUM, new Locale("es", "ES"));
        return formato.format(new Date(millis));
    }
}
