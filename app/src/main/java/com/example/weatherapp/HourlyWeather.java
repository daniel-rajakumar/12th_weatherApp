package com.example.weatherapp;

import android.widget.ImageView;

public class HourlyWeather {
    private String temp, time;
    private int weatherIcon, weatherGif;
    private boolean isCurrent;

    public HourlyWeather(String temp, String time, int weatherIcon, int weatherGif, boolean isCurrent) {
        this.temp = temp;
        this.time = time;
        this.weatherIcon = weatherIcon;
        this.isCurrent = isCurrent;
        this.weatherGif = weatherGif;
    }

    public String getTemp() {
        return temp;
    }

    public String getTime() {
        return time;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public boolean isCurrent() {
        return isCurrent;
    }

    public int getWeatherGif() { return weatherGif; }
}
