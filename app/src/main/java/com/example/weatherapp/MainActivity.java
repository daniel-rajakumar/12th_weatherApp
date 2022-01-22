package com.example.weatherapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TimeZone;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    String zip = "08852";

    void run() {
        CityWeather cityWeather = new CityWeather(zip);

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.run();
    }

}

class CityWeather {
    String apiKey = "5f05d53229dbaaa68c7dca9bf5eacb09";
    JSONObject jsonObject_geo, jsonObject_oneCall, jsonObject_history;

    TreeMap<Integer, JSONObject> map = new TreeMap<>();

    CityWeather(String zip) {
        try {
            String link = "http://api.openweathermap.org/geo/1.0/zip?zip=" + zip  + ",US&appid=" + apiKey;
            jsonObject_geo = getJsonFromUrl(link);

            Log.d("data", String.format("lat: %s, lon: %s", jsonObject_geo.get("lat"), jsonObject_geo.get("lon")));

            link = "https://api.openweathermap.org/data/2.5/onecall"
                    + "?lat=" + jsonObject_geo.get("lat")
                    + "&lon=" + jsonObject_geo.get("lon")
                    + "&exclude=" + "daily"
                    + "&units=" + "metric"
                    + "&appid=" + apiKey;

            jsonObject_oneCall = getJsonFromUrl(link);

            link = "http://api.openweathermap.org/data/2.5/onecall/timemachine"
                    + "?lat=" + jsonObject_geo.get("lat")
                    + "&lon=" + jsonObject_geo.get("lon")
                    + "&dt=" + localDateTimeToUnix(LocalDateTime.now())
                    + "&units=" + "metric"
                    + "&appid=" + apiKey;

            jsonObject_history = getJsonFromUrl(link);

            JSONArray jsonArray_hourly = jsonObject_history.getJSONArray("hourly");
            for (int i = 0; i < jsonArray_hourly.length(); i++){
                map.put(i - jsonArray_hourly.length(), jsonArray_hourly.getJSONObject(i));
            }

            map.put(0, jsonObject_oneCall.getJSONObject("current"));

            jsonArray_hourly = jsonObject_oneCall.getJSONArray("hourly");
            for (int i = 0; i < jsonArray_hourly.length(); i++){
                map.put(i + 1, jsonArray_hourly.getJSONObject(i));
            }

            for (Map.Entry<Integer, JSONObject> entry: map.entrySet()){
                Log.d("data", entry.getKey() + "\t-> "
//                        + new Date(Long.parseLong(entry.getValue().get("dt").toString()) * 1000)
//                        + ",   " + entry.getValue().get("temp")
                         + entry.getValue()

                );
            }

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    long localDateTimeToUnix(LocalDateTime localDateTime){
       return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 1000;
    }

    LocalDateTime unixToLocalDateTime(long unix){
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(unix * 1000), TimeZone.getDefault().toZoneId());
    }

    JSONObject getJsonFromUrl(String url) {
        final JSONObject[] jsonObject = new JSONObject[1];
        Thread thread = new Thread(() -> {
            try {
                jsonObject[0] = new JSONObject(
                        new Scanner(
                                new URL(url).openStream()
                        ).nextLine());
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
        });

        try {
            thread.start();
            thread.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        return jsonObject[0];
    }


}