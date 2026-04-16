package com.jorge.viajes.util;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UtilFecha {
    public static String formateaFecha(long segundos) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(segundos * 1000);
        DateFormat formato = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        Date fecha = calendar.getTime();
        return formato.format(fecha);
    }

    public static String formateaFecha(Calendar calendar) {
        DateFormat formato = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return formato.format(calendar.getTime());
    }

    public static long calendarALong(Calendar calendar) {
        return calendar.getTimeInMillis() / 1000;
    }
}
