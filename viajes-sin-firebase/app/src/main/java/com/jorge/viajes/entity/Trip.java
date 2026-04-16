package com.jorge.viajes.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jorge.viajes.util.Constantes;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Trip implements Parcelable {
    private String titulo, ciudad, lugarSalida, descripcion;
    private int precio;
    private long fechaSalida, fechaLlegada;
    private boolean seleccionado;

    public Trip(String titulo, String ciudad, String lugarSalida, String descripcion,
                int precio, long fechaSalida, long fechaLlegada) {
        this.titulo = titulo;
        this.ciudad = ciudad;
        this.lugarSalida = lugarSalida;
        this.descripcion = descripcion;
        this.precio = precio;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.seleccionado = false;
    }

    protected Trip(Parcel in) {
        titulo = in.readString();
        ciudad = in.readString();
        lugarSalida = in.readString();
        descripcion = in.readString();
        precio = in.readInt();
        fechaSalida = in.readLong();
        fechaLlegada = in.readLong();
        seleccionado = in.readByte() != 0;
    }

    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) { return new Trip(in); }
        @Override
        public Trip[] newArray(int size) { return new Trip[size]; }
    };

    public static List<Trip> generaViajes(int num) {
        List<Trip> viajes = new ArrayList<>();
        int numCiudades = Constantes.ciudades.length;
        int numSalidas = Constantes.lugarSalida.length;
        long baseTime = 1744761600L; // 16 Apr 2026

        for (int i = 0; i < num; i++) {
            int idx = ThreadLocalRandom.current().nextInt(0, numCiudades);
            String ciudad = Constantes.ciudades[idx];
            String salida = Constantes.lugarSalida[i % numSalidas];
            int precio = ThreadLocalRandom.current().nextInt(100, 2000);
            long fechaSalida = baseTime + (long) ThreadLocalRandom.current().nextInt(0, 180) * 86400L;
            long fechaLlegada = fechaSalida + (long) ThreadLocalRandom.current().nextInt(3, 21) * 86400L;
            String titulo = "Viaje desde " + salida + " hasta " + ciudad;
            String descripcion = "Viaje con salida desde " + salida + " con destino " + ciudad
                    + ". Precio por persona: " + precio + "€.";
            viajes.add(new Trip(titulo, ciudad, salida, descripcion, precio, fechaSalida, fechaLlegada));
        }
        return viajes;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titulo);
        dest.writeString(ciudad);
        dest.writeString(lugarSalida);
        dest.writeString(descripcion);
        dest.writeInt(precio);
        dest.writeLong(fechaSalida);
        dest.writeLong(fechaLlegada);
        dest.writeByte((byte) (seleccionado ? 1 : 0));
    }

    @Override
    public int describeContents() { return 0; }

    public String getTitulo() { return titulo; }
    public String getCiudad() { return ciudad; }
    public String getLugarSalida() { return lugarSalida; }
    public String getDescripcion() { return descripcion; }
    public int getPrecio() { return precio; }
    public long getFechaSalida() { return fechaSalida; }
    public long getFechaLlegada() { return fechaLlegada; }
    public boolean isSeleccionado() { return seleccionado; }
    public void setSeleccionado(boolean seleccionado) { this.seleccionado = seleccionado; }
}
