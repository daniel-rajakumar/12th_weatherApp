package com.example.weatherapp;

import android.widget.ImageView;

public class HourlyWeather {
    private String temp, time;
    private int weatherIcon;
    private boolean isCurrent;

    public HourlyWeather(String temp, String time, int weatherIcon, boolean isCurrent) {
        this.temp = temp;
        this.time = time;
        this.weatherIcon = weatherIcon;
        this.isCurrent = isCurrent;
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

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("HourlyWeather{");
        sb.append("temp='").append(temp).append('\'');
        sb.append(", time='").append(time).append('\'');
        sb.append(", weatherIcon=").append(weatherIcon);
        sb.append('}');
        return sb.toString();
    }
}
