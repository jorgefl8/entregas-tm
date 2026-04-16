package com.jorge.viajes.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Filtro implements Parcelable {
    private int precioMin;
    private int precioMax;
    private long fechaIni;
    private long fechaFin;

    public Filtro() {
        precioMin = 0;
        precioMax = Integer.MAX_VALUE;
        fechaIni = 0;
        fechaFin = Long.MAX_VALUE;
    }

    protected Filtro(Parcel in) {
        precioMin = in.readInt();
        precioMax = in.readInt();
        fechaIni = in.readLong();
        fechaFin = in.readLong();
    }

    public static final Creator<Filtro> CREATOR = new Creator<Filtro>() {
        @Override
        public Filtro createFromParcel(Parcel in) { return new Filtro(in); }
        @Override
        public Filtro[] newArray(int size) { return new Filtro[size]; }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(precioMin);
        dest.writeInt(precioMax);
        dest.writeLong(fechaIni);
        dest.writeLong(fechaFin);
    }

    @Override
    public int describeContents() { return 0; }

    public int getPrecioMin() { return precioMin; }
    public void setPrecioMin(int precioMin) { this.precioMin = precioMin; }
    public int getPrecioMax() { return precioMax; }
    public void setPrecioMax(int precioMax) { this.precioMax = precioMax; }
    public long getFechaIni() { return fechaIni; }
    public void setFechaIni(long fechaIni) { this.fechaIni = fechaIni; }
    public long getFechaFin() { return fechaFin; }
    public void setFechaFin(long fechaFin) { this.fechaFin = fechaFin; }
}
