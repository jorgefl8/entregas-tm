package com.jorge.acme_explorer.entity;

public class Travel {

    private String titulo;
    private String descripcion;
    private String ciudadOrigen;
    private double latOrigen;
    private double lngOrigen;
    private String ciudadDestino;
    private double latDestino;
    private double lngDestino;
    private long fechaSalida;
    private long fechaLlegada;
    private int precio;
    private String imagenUrl;

    public Travel() {
    }

    public Travel(String titulo, String descripcion,
                  String ciudadOrigen, double latOrigen, double lngOrigen,
                  String ciudadDestino, double latDestino, double lngDestino,
                  long fechaSalida, long fechaLlegada,
                  int precio, String imagenUrl) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.ciudadOrigen = ciudadOrigen;
        this.latOrigen = latOrigen;
        this.lngOrigen = lngOrigen;
        this.ciudadDestino = ciudadDestino;
        this.latDestino = latDestino;
        this.lngDestino = lngDestino;
        this.fechaSalida = fechaSalida;
        this.fechaLlegada = fechaLlegada;
        this.precio = precio;
        this.imagenUrl = imagenUrl;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getCiudadOrigen() { return ciudadOrigen; }
    public void setCiudadOrigen(String ciudadOrigen) { this.ciudadOrigen = ciudadOrigen; }

    public double getLatOrigen() { return latOrigen; }
    public void setLatOrigen(double latOrigen) { this.latOrigen = latOrigen; }

    public double getLngOrigen() { return lngOrigen; }
    public void setLngOrigen(double lngOrigen) { this.lngOrigen = lngOrigen; }

    public String getCiudadDestino() { return ciudadDestino; }
    public void setCiudadDestino(String ciudadDestino) { this.ciudadDestino = ciudadDestino; }

    public double getLatDestino() { return latDestino; }
    public void setLatDestino(double latDestino) { this.latDestino = latDestino; }

    public double getLngDestino() { return lngDestino; }
    public void setLngDestino(double lngDestino) { this.lngDestino = lngDestino; }

    public long getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(long fechaSalida) { this.fechaSalida = fechaSalida; }

    public long getFechaLlegada() { return fechaLlegada; }
    public void setFechaLlegada(long fechaLlegada) { this.fechaLlegada = fechaLlegada; }

    public int getPrecio() { return precio; }
    public void setPrecio(int precio) { this.precio = precio; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }
}
