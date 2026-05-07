package com.jorge.acme_explorer.entity;

import com.google.gson.annotations.SerializedName;

public class WeatherConditions {

    private float temp;

    @SerializedName("feels_like")
    private float feelsLike;

    @SerializedName("temp_min")
    private float tempMin;

    @SerializedName("temp_max")
    private float tempMax;

    private int pressure;
    private int humidity;

    public WeatherConditions() {
    }

    public float getTemp() { return temp; }
    public void setTemp(float temp) { this.temp = temp; }

    public float getFeelsLike() { return feelsLike; }
    public void setFeelsLike(float feelsLike) { this.feelsLike = feelsLike; }

    public float getTempMin() { return tempMin; }
    public void setTempMin(float tempMin) { this.tempMin = tempMin; }

    public float getTempMax() { return tempMax; }
    public void setTempMax(float tempMax) { this.tempMax = tempMax; }

    public int getPressure() { return pressure; }
    public void setPressure(int pressure) { this.pressure = pressure; }

    public int getHumidity() { return humidity; }
    public void setHumidity(int humidity) { this.humidity = humidity; }
}
