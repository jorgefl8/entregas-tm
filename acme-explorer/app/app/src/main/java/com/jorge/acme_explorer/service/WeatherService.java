package com.jorge.acme_explorer.service;

import com.jorge.acme_explorer.entity.WeatherResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeatherService {

    private static final String BASE_URL = "https://api.openweathermap.org/";
    private static final String UNITS_METRIC = "metric";
    private static final String LANG_ES = "es";

    private static WeatherService instance;
    private final WeatherRetrofitInterface api;

    private WeatherService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.api = retrofit.create(WeatherRetrofitInterface.class);
    }

    public static synchronized WeatherService getInstance() {
        if (instance == null) {
            instance = new WeatherService();
        }
        return instance;
    }

    public Call<WeatherResponse> getCurrentWeather(double lat, double lon, String apiKey) {
        return api.getCurrentWeather(lat, lon, apiKey, UNITS_METRIC, LANG_ES);
    }
}
