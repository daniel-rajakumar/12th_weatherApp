package com.example.weatherapp;

public class DailyWeather {
    private String time, temp_low, temp_high, description;
    private int weatherIcon;

    public DailyWeather(String time, String temp_low, String temp_high, String description, int weatherIcon) {
        this.time = time;
        this.temp_low = temp_low;
        this.temp_high = temp_high;
        this.description = description;
        this.weatherIcon = weatherIcon;
    }

    public String getTime() {
        return time;
    }

    public String getTemp_low() {
        return temp_low;
    }

    public String getTemp_high() {
        return temp_high;
    }

    public String getDescription() {
        return description;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }
}
