package com.jorge.viajes.util;

import com.jorge.viajes.entity.Trip;

import java.util.ArrayList;
import java.util.List;

public class Constantes {
    public static final String[] ciudades = {
            "Tirana", "Berlín", "Andorra La Vieja", "Ereván", "Viena", "Bakú", "Bruselas",
            "Minsk", "Sarajevo", "Sofía", "Praga", "Zagreb", "Copenhague", "Bratislava",
            "Liubliana", "Madrid", "Tallin", "Helsinki", "París", "Tiflis", "Atenas",
            "Budapest", "Dublín", "Reikiavik", "Roma", "Riga", "Vilna", "Luxemburgo",
            "Skopie", "La Valeta", "Mónaco", "Podgorica", "Oslo", "Ámsterdam", "Varsovia",
            "Lisboa", "Londres", "Bucarest", "Moscú", "Belgrado", "Estocolmo", "Kiev",
            "Ciudad Del Vaticano"
    };

    public static final String[] lugarSalida = {
            "Sevilla", "Málaga", "Faro", "Barcelona", "Madrid", "Valencia"
    };

    public static final List<Trip> VIAJES = Trip.generaViajes(20);
    public static final List<Trip> SELECCIONADOS = new ArrayList<>();
}
