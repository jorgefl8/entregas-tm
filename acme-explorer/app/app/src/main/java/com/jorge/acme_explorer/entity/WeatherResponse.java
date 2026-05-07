package com.jorge.acme_explorer.entity;

import java.util.List;

public class WeatherResponse {

    private WeatherLatLong coord;
    private List<Weather> weather;
    private WeatherConditions main;
    private Wind wind;
    private long timezone;
    private long id;
    private String name;
    private int cod;

    public WeatherResponse() {
    }

    public WeatherLatLong getCoord() { return coord; }
    public void setCoord(WeatherLatLong coord) { this.coord = coord; }

    public List<Weather> getWeather() { return weather; }
    public void setWeather(List<Weather> weather) { this.weather = weather; }

    public WeatherConditions getMain() { return main; }
    public void setMain(WeatherConditions main) { this.main = main; }

    public Wind getWind() { return wind; }
    public void setWind(Wind wind) { this.wind = wind; }

    public long getTimezone() { return timezone; }
    public void setTimezone(long timezone) { this.timezone = timezone; }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCod() { return cod; }
    public void setCod(int cod) { this.cod = cod; }
}
