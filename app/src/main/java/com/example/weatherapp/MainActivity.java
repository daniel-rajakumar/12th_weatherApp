package com.example.weatherapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
    String lat, lon;

    ArrayList<TreeMap<Integer, JSONObject>> days = new ArrayList<>();

    CityWeather(String zip) {
        String link = "http://api.openweathermap.org/geo/1.0/zip?zip=" + zip  + ",US&appid=" + apiKey;
        jsonObject_geo = getJsonFromUrl(link);

        try {
            lat = jsonObject_geo.get("lat").toString();
            lon = jsonObject_geo.get("lon").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        link = "https://api.openweathermap.org/data/2.5/onecall"
                + "?lat=" + lat
                + "&lon=" + lon
//                + "&exclude=" + "daily"
                + "&units=" + "metric"
                + "&appid=" + apiKey;
        jsonObject_oneCall = getJsonFromUrl(link);

        link = "http://api.openweathermap.org/data/2.5/onecall/timemachine"
                + "?lat=" + lat
                + "&lon=" + lon
                + "&dt=" + localDateTimeToUnix(LocalDateTime.now())
                + "&units=" + "metric"
                + "&appid=" + apiKey;
        jsonObject_history = getJsonFromUrl(link);


        // take care of current day
        days.add(new TreeMap<>());
        try {
            JSONArray jsonArray_hourly = jsonObject_history.getJSONArray("hourly");
            for (int i = 0; i < jsonArray_hourly.length(); i++)
                days.get(0).put(i - jsonArray_hourly.length(), jsonArray_hourly.getJSONObject(i));

            days.get(0).put(0, jsonObject_oneCall.getJSONObject("current"));

            jsonArray_hourly = jsonObject_oneCall.getJSONArray("hourly");
            for (int i = 0; i < jsonArray_hourly.length(); i++)
                days.get(0).put(i + 1, jsonArray_hourly.getJSONObject(i));
        } catch (JSONException e){
            e.printStackTrace();
        }

        // take care of next day
        try {
            JSONArray jsonArray_daily = jsonObject_oneCall.getJSONArray("daily");

            for (int i = 0; i < jsonArray_daily.length(); i++){
                Log.d("data", jsonArray_daily.get(i).toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




        for (int i = 0; i < days.size(); i++){
            Log.d("data", i + " -> " + days.get(i));
        }


//        for (Map.Entry<Integer, JSONObject> entry: days.get(0).entrySet()){
//                Log.d("data", entry.getKey() + "\t-> "
////                        + new Date(Long.parseLong(entry.getValue().get("dt").toString()) * 1000)
////                        + ",   " + entry.getValue().get("temp")
//                         + entry.getValue()
//                );
//        }
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